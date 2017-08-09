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
import hu.vissy.texttable.dataconverter.TypedDataConverter;
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
        UKNOWN,
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
        public DataConverter<?> converter;

        public DataColumn(CsvColumnType type, String header, DataExtractor<D, ?, ?> extractor) {
            super();
            this.type = type;
            this.header = header;
            this.extractor = extractor;
        }

        public DataColumn(CsvColumnType type, String header, DataExtractor<D, ?, ?> extractor, DataConverter<?> converter) {
            super();
            this.type = type;
            this.header = header;
            this.extractor = extractor;
            this.converter = converter;
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
     * Adds a column with an inherited data converter. The output will be
     * escaped and quoted.
     *
     * @param type
     *            The type of the column.
     * @param title
     *            The title of the column.
     * @param extractor
     *            The data extractor.
     * @param converter
     *            The data converter.
     * @return The builder itself.
     */
    public CsvTableFormatterBuilder<D> withInheritedColumn(CsvColumnType type, String title, DataExtractor<D, ?, ?> extractor,
            DataConverter<?> converter) {
        columns.add(new DataColumn(type, title, extractor, converter));
        return this;
    }



    /**
     * Adds a column with unknown type. It will be handled as a String with the
     * value get by toString().
     *
     * @param header
     *            The title of the column.
     * @param extractor
     *            The data extractor of the column.
     * @return The builder itself.
     */
    public <T> CsvTableFormatterBuilder<D> withUnknownColumn(String header, Function<D, T> extractor) {
        return withColumn(CsvColumnType.UKNOWN, header, extractor);
    }

    /**
     * Adds a column with unknown type. It will be handled as a String with the
     * value get by toString().
     *
     * @param header
     *            The title of the column.
     * @param extractor
     *            The data extractor of the column.
     * @return The builder itself.
     */
    public <T> CsvTableFormatterBuilder<D> withUnknownColumn(String header, DataExtractor<D, ?, T> extractor) {
        return withColumn(CsvColumnType.UKNOWN, header, extractor);
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


    @SuppressWarnings("unchecked")
    public CsvTableFormatterBuilder<D> fromTableFormatter(TableFormatter<D> formatter) {
        for (TableFormatter<D>.IndexedColumnDefinition<?, ?> cd : formatter.getColumns()) {
            DataConverter<?> converter = cd.getDefinition().getDataConverter();
            if (converter instanceof TypedDataConverter) {
                TypedDataConverter<?> typedConverter = (TypedDataConverter<?>) converter;
                if (Integer.class.isAssignableFrom(typedConverter.getAcceptedClass())) {
                    withIntegerColumn(cd.getTitle(), (DataExtractor<D, ?, Integer>) cd.getDefinition().getDataExtractor());
                } else if (Double.class.isAssignableFrom(typedConverter.getAcceptedClass())) {
                    withDoubleColumn(cd.getTitle(), (DataExtractor<D, ?, Double>) cd.getDefinition().getDataExtractor());
                } else if (LocalDate.class.isAssignableFrom(typedConverter.getAcceptedClass())) {
                    withDateColumn(cd.getTitle(), (DataExtractor<D, ?, LocalDate>) cd.getDefinition().getDataExtractor());
                } else if (LocalTime.class.isAssignableFrom(typedConverter.getAcceptedClass())) {
                    withTimeColumn(cd.getTitle(), (DataExtractor<D, ?, LocalTime>) cd.getDefinition().getDataExtractor());
                } else if (LocalDateTime.class.isAssignableFrom(typedConverter.getAcceptedClass())) {
                    withDateTimeColumn(cd.getTitle(), (DataExtractor<D, ?, LocalDateTime>) cd.getDefinition().getDataExtractor());
                } else if (String.class.isAssignableFrom(typedConverter.getAcceptedClass())) {
                    withStringColumn(cd.getTitle(), (DataExtractor<D, ?, String>) cd.getDefinition().getDataExtractor());
                } else {
                    withInheritedColumn(CsvColumnType.UKNOWN, cd.getTitle(), cd.getDefinition().getDataExtractor(), cd.getDefinition().getDataConverter());
                }
            } else {
                throw new IllegalArgumentException("Automatic column conversion needs TypedDataConverter: " + cd.getTitle() + " ("
                        + converter.getClass().getCanonicalName() + ")");
            }
        }
        return this;
    }



    private class CsvStringDataConverter<T> implements DataConverter<T> {

        @Override
        public String convert(T d) {
            return quoter.apply(d == null ? null : "" + d);
        }
    }



    private class CsvInheritedDataConverter<T> extends CsvStringDataConverter<T> {

        private DataConverter<T> innerDataConverter;

        public CsvInheritedDataConverter(DataConverter<T> innerDataConverter) {
            super();
            this.innerDataConverter = innerDataConverter;
        }

        @Override
        public String convert(T d) {
            String str = innerDataConverter.convert(d);
            return quoter.apply(str);
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
        NumberDataConverter<Double> csvDoubleConverter = new NumberDataConverter<>(Double.class, csvDoubleFormatter);

        NumberFormat csvIntegerFormatter = NumberFormat.getInstance(locale);
        csvIntegerFormatter.setMaximumFractionDigits(0);
        csvIntegerFormatter.setMinimumFractionDigits(0);
        csvIntegerFormatter.setGroupingUsed(false);
        csvIntegerFormatter.setRoundingMode(RoundingMode.UNNECESSARY);
        NumberDataConverter<Integer> csvIntegerConverter = new NumberDataConverter<>(Integer.class, csvIntegerFormatter);

        DataConverter<LocalDate> csvDateDataConverter = (s) -> s == null ? null : s.toString();
        DataConverter<LocalTime> csvTimeDataConverter = (s) -> {
            if (s == null) {
                return null;
            }
            String str = s.toString();
            if (str.indexOf('.') != -1) {
                str = str.substring(0, str.indexOf("."));
            }
            return str;
        };
        DataConverter<LocalDateTime> csvDateTimeDataConverter = (s) -> {
            if (s == null) {
                return null;
            }
            String str = s.toString().replaceAll("T", " ");
            if (str.indexOf('.') != -1) {
                str = str.substring(0, str.indexOf("."));
            }
            return str;
        };

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
        converterMapping.put(CsvColumnType.UKNOWN, (s) -> quoter.apply("" + s));
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
        DataConverter<?> dataConverter = converterMapping.get(dc.type);
        if (dc.converter != null) {
            dataConverter = new CsvInheritedDataConverter<>(dc.converter);
        }
        if (dc.extractor instanceof StatelessDataExtractor) {
            return new ColumnDefinition.StatelessBuilder<D, Object>()
                    .withTitle(dc.header)
                    .withDataConverter((DataConverter<Object>) dataConverter)
                    .withCellContentFormatter(csvCellContentFormatter)
                    .withDataExtractor((StatelessDataExtractor<D, Object>) dc.extractor)
                    .build();
        } else {
            return new ColumnDefinition.StatefulBuilder<D, Object, Object>()
                    .withTitle(dc.header)
                    .withDataConverter((DataConverter<Object>) dataConverter)
                    .withCellContentFormatter(csvCellContentFormatter)
                    .withDataExtractor((StatefulDataExtractor<D, Object, Object>) dc.extractor)
                    .build();
        }
    }

}
