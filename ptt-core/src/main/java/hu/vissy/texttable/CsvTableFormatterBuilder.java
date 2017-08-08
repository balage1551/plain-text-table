package hu.vissy.texttable;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.vissy.texttable.BorderFormatter.DefaultFormatters;
import hu.vissy.texttable.BorderFormatter.RowSpec;
import hu.vissy.texttable.column.ColumnDefinition;
import hu.vissy.texttable.contentformatter.CellAlignment;
import hu.vissy.texttable.contentformatter.CellContentFormatter;
import hu.vissy.texttable.contentformatter.EllipsisDecorator;
import hu.vissy.texttable.dataconverter.DataConverter;
import hu.vissy.texttable.dataconverter.NumberDataConverter;
import hu.vissy.texttable.dataconverter.StringDataConverter;
import hu.vissy.texttable.dataextractor.DataExtractor;
import hu.vissy.texttable.dataextractor.StatefulDataExtractor;
import hu.vissy.texttable.dataextractor.StatelessDataExtractor;

/**
 * A utility builder to create simple CSV export from data.
 *
 * @author Balage
 *
 * @param <D>
 *            The input record type.
 */
public class CsvTableFormatterBuilder<D> {

    private enum CsvColumnType {
        STRING,
        INTEGER,
        DOUBLE,
        DATE,
        TIME,
        DATETIME
    }

    private class DataColumn {
        public CsvColumnType type;
        public String header;
        public DataExtractor<D, ?, ?> extractor;

        public DataColumn(CsvColumnType type, String header, DataExtractor<D, ?, ?> extractor) {
            super();
            this.type = type;
            this.header = header;
            this.extractor = extractor;
        }
    }

    private Locale locale = Locale.getDefault();
    private char quote = '"';
    private String escapeQuote = "\"\"";
    private char delimiter = ',';
    private boolean headerLine = true;
    private int maximumFractionDigits = Integer.MAX_VALUE;
    private Function<String, String> quoter = null;

    private List<DataColumn> columns = new ArrayList<>();

    /**
     * Sets the locale used for number formatting.
     * <p>
     * Note, that this affects only numeric formatting. Date/time formatters use
     * ISO format.
     * </p>
     * <p>
     * Default value if not set: {@linkplain Locale#getDefault()}
     * </p>
     *
     * @param locale
     *            The locale to use.
     * @return The builder instance.
     */
    public CsvTableFormatterBuilder<D> withLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    /**
     * Sets the cell delimiter used in CSV.
     * <p>
     * Default value if not set: comma (",")
     * </p>
     *
     * @param delimiter
     *            The delimiter character.
     * @return The builder instance.
     */
    public CsvTableFormatterBuilder<D> withDelimiter(char delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    /**
     * Sets the quote character used in string columns. This method will use
     * double quote characters as escape.
     *
     * <p>
     * For more control on escaping and decorating strings, use the
     * {@linkplain #withQuoter(Function)} function. Note, that when quoter
     * function is set, these settings are ignored.
     * </p>
     * <p>
     * Default value if not set: quote (")
     * </p>
     *
     * @param quote
     *            The quote character.
     * @return The builder instance.
     * @see {@linkplain #withQuoter(Function)}
     */
    public CsvTableFormatterBuilder<D> withQuote(char quote) {
        return withQuote(quote, "" + quote + quote);
    }

    /**
     * Sets the quote character used in string columns.
     * <p>
     * For more control on escaping and decorating strings, use the
     * {@linkplain #withQuoter(Function)} function. Note, that when quoter
     * function is set, these settings are ignored.
     * </p>
     * <p>
     * Default value if not set: quote (") and double quotes for escape ("")
     * </p>
     *
     * @param quote
     *            The quote character.
     * @param escapeQuote
     *            The character used to replace quote character.
     * @return The builder instance.
     * @see {@linkplain #withQuoter(Function)}
     */
    public CsvTableFormatterBuilder<D> withQuote(char quote, String escapeQuote) {
        this.quote = quote;
        this.escapeQuote = escapeQuote;
        return this;
    }

    /**
     * Sets an arbitiary quote and escape function. The function will receive a
     * string and should return its escaped and version.
     *
     * <p>
     * Default value if not set: the values set by
     * {@linkplain #withQuote(char, String)} is used.
     * </p>
     *
     * @param quoter
     *            The quoter and escape function.
     * @return The builder instance.
     */
    public CsvTableFormatterBuilder<D> withQuoter(Function<String, String> quoter) {
        this.quoter = quoter;
        return this;
    }


    /**
     * Sets whether to print header line or not.
     *
     * <p>
     * Default value if not set: true (header line is printed)
     * </p>
     *
     * @param headerLine
     *            Whether to print header line or not.
     * @return The builder instance.
     */
    public CsvTableFormatterBuilder<D> withHeaderLine(boolean headerLine) {
        this.headerLine = headerLine;
        return this;
    }

    /**
     * Sets the maximum fractional digits to round the output to.
     *
     * <p>
     * Default value if not set: infinity (no rounding appears)
     * </p>
     *
     * @param maximumFractionDigits
     *            A non-negative value of the significant fractional digits.
     * @return The builder instance.
     */
    public CsvTableFormatterBuilder<D> withMaximumFractionDigits(int maximumFractionDigits) {
        this.maximumFractionDigits = maximumFractionDigits;
        return this;
    }



    private CsvTableFormatterBuilder<D> withColumn(CsvColumnType type, String header, Function<D, ?> extractor) {
        return withColumn(type, header, new StatelessDataExtractor<>(extractor));
    }

    private CsvTableFormatterBuilder<D> withColumn(CsvColumnType type, String header, DataExtractor<D, ?, ?> extractor) {
        columns.add(new DataColumn(type, header, extractor));
        return this;
    }

    /**
     * Adds a string column.
     *
     * @param header
     *            The title of the column.
     * @param extractor
     *            The data extractor of the column.
     * @return The builder itself.
     */
    public CsvTableFormatterBuilder<D> withStringColumn(String header, Function<D, String> extractor) {
        return withColumn(CsvColumnType.STRING, header, extractor);
    }

    /**
     * Adds a string column.
     *
     * @param header
     *            The title of the column.
     * @param extractor
     *            The data extractor of the column.
     * @return The builder itself.
     */
    public CsvTableFormatterBuilder<D> withStringColumn(String header, DataExtractor<D, ?, String> extractor) {
        return withColumn(CsvColumnType.STRING, header, extractor);
    }

    /**
     * Adds an integer column.
     *
     * @param header
     *            The title of the column.
     * @param extractor
     *            The data extractor of the column.
     * @return The builder itself.
     */
    public CsvTableFormatterBuilder<D> withIntegerColumn(String header, Function<D, Integer> extractor) {
        return withColumn(CsvColumnType.INTEGER, header, extractor);
    }

    /**
     * Adds an integer column.
     *
     * @param header
     *            The title of the column.
     * @param extractor
     *            The data extractor of the column.
     * @return The builder itself.
     */
    public CsvTableFormatterBuilder<D> withIntegerColumn(String header, DataExtractor<D, ?, Integer> extractor) {
        return withColumn(CsvColumnType.INTEGER, header, extractor);
    }

    /**
     * Adds a double column.
     *
     * @param header
     *            The title of the column.
     * @param extractor
     *            The data extractor of the column.
     * @return The builder itself.
     */
    public CsvTableFormatterBuilder<D> withDoubleColumn(String header, Function<D, Double> extractor) {
        return withColumn(CsvColumnType.DOUBLE, header, extractor);
    }

    /**
     * Adds a double column.
     *
     * @param header
     *            The title of the column.
     * @param extractor
     *            The data extractor of the column.
     * @return The builder itself.
     */
    public CsvTableFormatterBuilder<D> withDoubleColumn(String header, DataExtractor<D, ?, Double> extractor) {
        return withColumn(CsvColumnType.DOUBLE, header, extractor);
    }

    /**
     * Adds a date column.
     *
     * <p>
     * Dates are printed in ISO format.
     * </p>
     *
     * @param header
     *            The title of the column.
     * @param extractor
     *            The data extractor of the column.
     * @return The builder itself.
     */
    public CsvTableFormatterBuilder<D> withDateColumn(String header, Function<D, LocalDate> extractor) {
        return withColumn(CsvColumnType.DATE, header, extractor);
    }

    /**
     * Adds a date column.
     *
     * <p>
     * Dates are printed in ISO format.
     * </p>
     *
     * @param header
     *            The title of the column.
     * @param extractor
     *            The data extractor of the column.
     * @return The builder itself.
     */
    public CsvTableFormatterBuilder<D> withDateColumn(String header, DataExtractor<D, ?, LocalDate> extractor) {
        return withColumn(CsvColumnType.DATE, header, extractor);
    }

    /**
     * Adds a time column.
     *
     * <p>
     * Times are printed in ISO format.
     * </p>
     *
     * @param header
     *            The title of the column.
     * @param extractor
     *            The data extractor of the column.
     * @return The builder itself.
     */
    public CsvTableFormatterBuilder<D> withTimeColumn(String header, Function<D, LocalTime> extractor) {
        return withColumn(CsvColumnType.TIME, header, extractor);
    }

    /**
     * Adds a time column.
     *
     * <p>
     * Times are printed in ISO format.
     * </p>
     *
     * @param header
     *            The title of the column.
     * @param extractor
     *            The data extractor of the column.
     * @return The builder itself.
     */
    public CsvTableFormatterBuilder<D> withTimeColumn(String header, DataExtractor<D, ?, LocalTime> extractor) {
        return withColumn(CsvColumnType.TIME, header, extractor);
    }

    /**
     * Adds a date-time column.
     *
     * <p>
     * Dates and times are printed in ISO format, but the "T" within is replaced
     * to space.
     * </p>
     *
     * @param header
     *            The title of the column.
     * @param extractor
     *            The data extractor of the column.
     * @return The builder itself.
     */
    public CsvTableFormatterBuilder<D> withDateTimeColumn(String header, Function<D, LocalDateTime> extractor) {
        return withColumn(CsvColumnType.DATETIME, header, extractor);
    }

    /**
     * Adds a date-time column.
     *
     * <p>
     * Dates and times are printed in ISO format, but the "T" within is replaced
     * to space.
     * </p>
     *
     * @param header
     *            The title of the column.
     * @param extractor
     *            The data extractor of the column.
     * @return The builder itself.
     */
    public CsvTableFormatterBuilder<D> withDateTimeColumn(String header, DataExtractor<D, ?, LocalDateTime> extractor) {
        return withColumn(CsvColumnType.DATETIME, header, extractor);
    }


//    public CsvTableFormatterBuilder<D> fromTableFormatter(TableFormatter<D> formatter) {
//        for (TableFormatter<D>.IndexedColumnDefinition<?, ?> cd : formatter.getColumns()) {
//
//        }
//        return this;
//    }


    private class CsvStringDataConverter<T> implements DataConverter<T> {

        @Override
        public String convert(T d) {
            return quoter.apply(d == null ? null : "" + d);
        }
    }

    /**
     * Creates a {@linkplain TableFormatter} instance which prints the data in
     * CSV format.
     *
     * @return A table formatter to print data in CSV.
     */
    public TableFormatter<D> build() {
        if (quoter == null) {
            quoter = (s) -> (s == null) ? null : quote + s.replaceAll(Pattern.quote("" + quote), Matcher.quoteReplacement(escapeQuote)) + quote;
        }

        NumberFormat csvDoubleFormatter = NumberFormat.getInstance(locale);
        csvDoubleFormatter.setMaximumFractionDigits(maximumFractionDigits);
        csvDoubleFormatter.setGroupingUsed(false);
        csvDoubleFormatter.setRoundingMode(RoundingMode.HALF_UP);
        NumberDataConverter<Double> csvDoubleConverter = new NumberDataConverter<>(csvDoubleFormatter);

        NumberFormat csvIntegerFormatter = NumberFormat.getInstance(locale);
        csvIntegerFormatter.setMaximumFractionDigits(0);
        csvIntegerFormatter.setMinimumFractionDigits(0);
        csvIntegerFormatter.setGroupingUsed(false);
        csvIntegerFormatter.setRoundingMode(RoundingMode.UNNECESSARY);
        NumberDataConverter<Integer> csvIntegerConverter = new NumberDataConverter<>(csvIntegerFormatter);

        DataConverter<LocalDate> csvDateDataConverter = (s) -> s == null ? null : s.toString();
        DataConverter<LocalTime> csvTimeDataConverter = (s) -> s == null ? null : s.toString().substring(0, s.toString().indexOf("."));
        DataConverter<LocalDateTime> csvDateTimeDataConverter = (s) -> s == null ? null : s.toString().replaceAll("T", " ");

        CellContentFormatter csvCellContentFormatter = new CellContentFormatter.Builder()
                .withEllipsesDecorator(new EllipsisDecorator.Builder().withEllipsisSign("").build())
                .withCellAlignment(new CellAlignment() {
                    @Override
                    public String align(String data, int width) {
                        return data;
                    }
                })
                .build();

        EnumMap<CsvColumnType, DataConverter<?>> converterMapping = new EnumMap<>(CsvColumnType.class);
        converterMapping.put(CsvColumnType.STRING, new StringDataConverter());
        converterMapping.put(CsvColumnType.INTEGER, csvIntegerConverter);
        converterMapping.put(CsvColumnType.DOUBLE, csvDoubleConverter);
        converterMapping.put(CsvColumnType.DATE, csvDateDataConverter);
        converterMapping.put(CsvColumnType.TIME, csvTimeDataConverter);
        converterMapping.put(CsvColumnType.DATETIME, csvDateTimeDataConverter);

        TableFormatter.Builder<D> builder = new TableFormatter.Builder<>();

        if (headerLine) {
            builder.withHeaderConverter(new CsvStringDataConverter<String>());
        } else {
            builder.withShowHeader(false);
        }

        builder.withBorderFormatter(new BorderFormatter.Builder(DefaultFormatters.EMPTY)
                .withUniformRow(new RowSpec('\0', delimiter, '\0'))
                .withDrawVerticalSeparator(true)
                .build());

        for (DataColumn dc : columns) {
            ColumnDefinition<D, ?, ?> columnDef = this.createColumn(dc, csvCellContentFormatter, converterMapping);
            builder.withColumn(columnDef);
        }

        return builder.build();

    }


    @SuppressWarnings("unchecked")
    private ColumnDefinition<D, ?, ?> createColumn(DataColumn dc, CellContentFormatter csvCellContentFormatter,
            EnumMap<CsvColumnType, DataConverter<?>> converterMapping) {
        if (dc.extractor instanceof StatelessDataExtractor) {
            return new ColumnDefinition.StatelessBuilder<D, Object>()
                    .withTitle(dc.header)
                    .withDataConverter((DataConverter<Object>) converterMapping.get(dc.type))
                    .withCellContentFormatter(csvCellContentFormatter)
                    .withDataExtractor((StatelessDataExtractor<D, Object>) dc.extractor)
                    .build();
        } else {
            return new ColumnDefinition.StatefulBuilder<D, Object, Object>()
                    .withTitle(dc.header)
                    .withDataConverter((DataConverter<Object>) converterMapping.get(dc.type))
                    .withCellContentFormatter(csvCellContentFormatter)
                    .withDataExtractor((StatefulDataExtractor<D, Object, Object>) dc.extractor)
                    .build();
        }
    }

}
