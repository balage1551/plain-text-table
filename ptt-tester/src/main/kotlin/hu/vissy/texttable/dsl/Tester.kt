package hu.vissy.texttable.dsl

import hu.vissy.texttable.tester.TestObject
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import kotlin.math.floor

private class SubTotalDemoAggregator {
    var actFruit: String? = null
    var fruitSum = 0.0
    var totalSum = 0.0
}


fun main() {
    val formatter = tableFormatter<TestObject> {
        heading = "Test table"
        showAggregation = true
        separateDataWithLines = false

        simple<String>("Fruit") { d -> d.name }
        stateless<LocalDateTime> {
            title = "Timestamp"
            extractor { d -> d.date }
            converter<DefaultDateTime> { format = "yyyy-MM-dd" }
            cellFormatter {
                nullValue = "----------"
            }
        }
        stateless<Boolean> {
            title = "Valid"
            extractor { d -> d.isValid }
            converter<DefaultBoolean> {
                trueValue = "yes"
                falseValue = "no"
            }
            cellFormatter(center) {
            }
        }
        stateless<Int> {
            title = "Length"
            extractor { d -> d.length }
            converter<DefaultInt> {
                grouping = false
            }
        }
        stateful<Double, SubTotalDemoAggregator> {
            title = "quantity"
            initState { SubTotalDemoAggregator() }
            extractor { d, s ->
                val v: Double = d.quantity
                s.totalSum += v
                s.fruitSum += v
                v
            }
            aggregator { key, s ->
                s.totalSum
            }
            cellFormatter(right)
            converter<DefaultDouble> {
                minimumFractionDigits = 1
            }
        }
        stateless<Duration> {
            title = "duration"
            extractor { d -> d.duration }
            converter<DefaultDuration>()
        }
        stateless<String> {
            title = "truncated"
            extractor { d -> d.name }
            cellFormatter {
                maxWidth = 5
                ellipsis(segmentCenter, "_")
            }
        }
    }

    formatter?.apply(generate2(10))?.println()
}

fun String.println() {
    println(this)
}

private val FRUITS = arrayOf("apple", "banana", "cherry", "date", "eggplant", "fig", "huckleberry")


private fun generate2(count: Int): List<TestObject?> {
    val r = Random(150)
    return (0 until count)
            .map { i: Int ->
                if (i == 2 || i == 11) {
                    null
                } else {
                    TestObject(i.toLong(), FRUITS[i % FRUITS.size],
                            floor(100 * r.nextDouble()) / 10,
                            if (i == 3) null else LocalDateTime.now().plusSeconds(r.nextInt(7200) - 3600.toLong()),
                            Duration.ofSeconds(r.nextInt(8 * 7200).toLong()),
                            r.nextDouble() < .33,
                            r.nextInt(500000))
                }
            }
}
