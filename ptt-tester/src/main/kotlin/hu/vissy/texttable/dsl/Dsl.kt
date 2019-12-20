@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package hu.vissy.texttable.dsl

import hu.vissy.texttable.TableFormatter
import hu.vissy.texttable.column.ColumnDefinition
import hu.vissy.texttable.contentformatter.*
import hu.vissy.texttable.contentformatter.EllipsisDecorator.TextSegment
import hu.vissy.texttable.dataconverter.*
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

fun <D : Any> tableFormatter(op: TableFormatterBuilder<D>.() -> Unit) = TableFormatterBuilder<D>().apply(op).build()

class TableFormatterBuilder<D : Any> {
    private val columns = mutableListOf<ColumnDefinition<D, Void, *>>()
    var heading: String? = null
    var showAggregation = false
    var separateDataWithLines = false

    fun build(): TableFormatter<D>? {
        return TableFormatter.Builder<D>().apply {
            withHeading(heading)
            withShowAggregation(showAggregation)
            withSeparateDataWithLines(separateDataWithLines)
            columns.forEach { withColumn(it) }
        }.build()
    }

    fun <T : Any> stateless(op: StatelessColumnBuilder<D, T>.() -> Unit) =
            StatelessColumnBuilder<D, T>().apply(op).build().apply { columns += this }

    fun <T : Any> simple(title: String = "", extractor: (D) -> T) =
            StatelessColumnBuilder<D, T>(title).apply { this.extractor = extractor }.build().apply { columns += this }

}

class StatelessColumnBuilder<D : Any, T : Any>(var title: String = "") {
    var extractor: ((D) -> T?)? = null
    var converter: DataConverter<T?> = TrivialDataConverter<T?>()
    var cellFormatter: CellContentFormatter = CellContentFormatter.leftAlignedCell()

    fun extractor(f: (D) -> T?) {
        this.extractor = f
    }

    inline fun <reified C : DataConverterBuilder<T?>> converter(op: C.() -> Unit = { -> }) =
            C::class.java.getDeclaredConstructor().newInstance().apply(op).build().apply { converter = this }

    fun cellFormatter(alignment: CellAlignmentMarker = left, op: CellContentFormatterBuilder.() -> Unit) =
            CellContentFormatterBuilder(alignment).apply(op).build().apply { cellFormatter = this }

    fun build(): ColumnDefinition<D, Void, T> = ColumnDefinition.StatelessBuilder<D, T>()
            .withTitle(title)
            .withDataExtractor(extractor)
            .withDataConverter(converter)
            .withCellContentFormatter(cellFormatter)
            .build()
}


abstract class DataConverterBuilder<T> {
    abstract fun build(): DataConverter<T>
}

open class DefaultDate : DataConverterBuilder<LocalDate?>() {
    var format: String = "yyyy-MM-dd"
        set(value) {
            field = value
            formatter = DateTimeFormatter.ofPattern(value)
        }
    var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(format)

    override fun build() = DateDataConverter(formatter)
}


open class DefaultTime : DataConverterBuilder<LocalTime?>() {
    var format: String = "hh:mm:ss"
        set(value) {
            field = value
            formatter = DateTimeFormatter.ofPattern(value)
        }
    var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(format)

    override fun build() = TimeDataConverter(formatter)
}


open class DefaultDateTime : DataConverterBuilder<LocalDateTime?>() {
    var format: String = "yyyy-MM-dd hh:mm:ss"
        set(value) {
            field = value
            formatter = DateTimeFormatter.ofPattern(value)
        }
    var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(format)

    override fun build() = DateTimeDataConverter(formatter)
}


open class DefaultBoolean : DataConverterBuilder<Boolean?>() {
    var trueValue = "true"
    var falseValue = "false"
    override fun build() = BooleanDataConverter(trueValue, falseValue)
}


abstract class DefaultNumber<T : Number>(val clazz: Class<T>) : DataConverterBuilder<T?>() {
    var formatter: NumberFormat = NumberFormat.getInstance()

    var locale: Locale = Locale.getDefault()
        set(value) {
            val old = formatter
            formatter = NumberFormat.getInstance(value)
            formatter.roundingMode = old.roundingMode
            formatter.isGroupingUsed = old.isGroupingUsed
            formatter.minimumFractionDigits = old.minimumFractionDigits
            formatter.maximumFractionDigits = old.maximumFractionDigits
        }

    var maximumFractionDigits
        get() = formatter.maximumFractionDigits
        set(value) {
            formatter.maximumFractionDigits = value
        }

    var minimumFractionDigits
        get() = formatter.minimumFractionDigits
        set(value) {
            formatter.minimumFractionDigits = value
        }

    var grouping
        get() = formatter.isGroupingUsed
        set(value) {
            formatter.isGroupingUsed = value
        }

    var rounding: RoundingMode
        get() = formatter.roundingMode
        set(value) {
            formatter.roundingMode = value
        }

    override fun build() = NumberDataConverter<T>(clazz, formatter)
}

abstract class DefaultDecimal<T : Number>(clazz: Class<T>) : DefaultNumber<T>(clazz) {
    init {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
        rounding = RoundingMode.UNNECESSARY
    }
}


open class DefaultInt : DefaultDecimal<Int>(Int::class.java) {
}

open class DefaultLong : DefaultDecimal<Long>(Long::class.java) {
}

open class DefaultDouble : DefaultDecimal<Double>(Double::class.java) {
    init {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
        rounding = RoundingMode.HALF_UP
    }
}

class DefaultDuration : DataConverterBuilder<Duration?>() {
    override fun build(): DataConverter<Duration?> =
            SimpleDurationDataConverter()
}

sealed class CellAlignmentMarker() {
    internal abstract fun build(padding: Char): CellAlignment
}

@Suppress("ClassName")
object left : CellAlignmentMarker() {
    override fun build(padding: Char) = LeftCellAlignment(padding)
}

@Suppress("ClassName")
object right : CellAlignmentMarker(){
    override fun build(padding: Char) = RightCellAlignment(padding)
}

@Suppress("ClassName")
object center : CellAlignmentMarker() {
    override fun build(padding: Char) = CenterCellAlignment(padding)
}


class CellContentFormatterBuilder(alignment: CellAlignmentMarker = left) {
    private var cellAlignment: CellAlignment

    var nullValue : String = ""

    var minWidth : Int = 0
    var maxWidth : Int = Int.MAX_VALUE

    var ellipsis : EllipsisDecorator = EllipsisDecorator.Builder().build()

    var padding: Char = ' '
        set(value) {
            field = value
            cellAlignment = alignment.build(value)
        }

    var alignment: CellAlignmentMarker = alignment
        set(value) {
            field = value
            cellAlignment = value.build(padding)
        }

    init {
        cellAlignment = alignment.build(padding)
    }

    fun ellipsis(keep : TextSegmentMarker = segmentStart, sign: String = "...", op: EllipsisDecoratorBuilder.() -> Unit = {->})
            = EllipsisDecoratorBuilder(keep, sign).apply(op).build().apply { ellipsis = this }


    fun build(): CellContentFormatter = CellContentFormatter.Builder()
            .withCellAlignment(cellAlignment)
            .withNullValue(nullValue)
            .withMinWidth(minWidth)
            .withMaxWidth(maxWidth)
            .withEllipsesDecorator(ellipsis)
            .build()
}


sealed class TextSegmentMarker(val segment : TextSegment)
@Suppress("ClassName")
object segmentStart : TextSegmentMarker(TextSegment.START)
@Suppress("ClassName")
object segmentEnd : TextSegmentMarker(TextSegment.END)
@Suppress("ClassName")
object segmentCenter : TextSegmentMarker(TextSegment.CENTER)


class EllipsisDecoratorBuilder(var keep: TextSegmentMarker = segmentStart, var sign: String = "...") {
    var trimToWord = false

    fun build(): EllipsisDecorator  = EllipsisDecorator.Builder()
            .withEllipsisSign(sign)
            .withKeptPart(keep.segment)
            .withTrimToWord(trimToWord)
            .build()

}

