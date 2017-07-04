package hu.vissy.texttable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import hu.vissy.texttable.BorderFormatter.LineType;
import hu.vissy.texttable.BorderFormatter.RowType;
import hu.vissy.texttable.column.ColumnDefinition;
import hu.vissy.texttable.contentformatter.CellContentFormatter;
import hu.vissy.texttable.contentformatter.EllipsisDecorator;
import hu.vissy.texttable.dataconverter.DataConverter;
import hu.vissy.texttable.dataconverter.NumberDataConverter;
import hu.vissy.texttable.dataconverter.StringDataConverter;
import hu.vissy.texttable.dataextractor.DataExtractor;
import hu.vissy.texttable.dataextractor.StatelessDataExtractor;

/**
 *
 * A flexible, configurable plain text table generator engine.
 *
 * <p>
 * The operation is divided into two seprated phases: the configuration and the
 * application. In the configuration phase the user configures the behaviour and
 * the look and feel of the table. In the application phase, the input data is
 * presented according the configuration. This separation of the two phases
 * allows a configure once apply multiple times behavoiur.
 * </p>
 * <p>
 * A very important feature of the API, that almost all the configuration
 * classes are imutable, making them safe to use multiple times.
 * </p>
 * <p>
 * Each table has a type parameter defining the input record type it accepts.
 * </p>
 * <p>
 * The formatting is performed in multiple steps. It is important to uderstand
 * these steps and their role in the formatting, because these steps are
 * configured independently.The steps are the following:
 * <ul>
 * <li><b>Data extraction</b>: the cell data is aquired from the input
 * record.</li>
 * <li><b>Data conversion</b>: the extracted data is converted to string.</li>
 * <li><b>Cell formatting</b>: the converted data is decorated (shortening,
 * internal padding, aligning) for the cell.</li>
 * <li><b>Table formatting</b>: the decorated cells are combined into a
 * table.</li>
 * </ul>
 * <p>
 * Through these steps, several type arguments are used. Here is the short
 * overview of them:
 * </p>
 * <ul>
 * <li><b>Record data (D)</b>: represents an input record. Can be any Java
 * class.</li>
 * <li><b>Cell data (T)</b>: represents the value of a cell of the record. Can
 * be any Java class.</li>
 * <li><b>State data (S)</b>: when a column is stateful (having information
 * travelling from record to record, mostly for aggregation), this class
 * represents the state data. Can be any Java class.</li>
 * </ul>
 * <h2>Data extraction (D ➜ T)</h2>
 * <p>
 * In this phase, the cell data (T) is extracted from the record (D). The
 * extraction is made by the {@linkplain DataExtractor} (stateful) and the
 * {@linkplain StatelessDataExtractor} (stateless) classes.
 * </p>
 * <h2>Data conversion (T ➜ String)</h2>
 * <p>
 * In this phase, the cell data (T) is converted and formatted into a string.
 * Here type specific formatting is possible, such as numeric or date/time
 * formatting. The classes responsible for this conversion implement the
 * {@linkplain DataConverter} interface. There are several prebuilt formatter
 * implementations are available for the most common cell types (numbers,
 * dates/times, boolean, String, etc.), but you may easily create any new
 * special purpose converters easily by implementing the DataConverter
 * interface. Note, that this interface is a {@linkplain FunctionalInterface},
 * so it is possible to use a closure.
 * </p>
 * <p>
 * At the end of this step, an intermediate data structure
 * ({@linkplain TableData}) is created.
 * </p>
 * <h2>Cell formatting</h2>
 * <p>
 * In this phase, the converted cell data is decorated by the rules defined for
 * its column. The following conversions are made:
 * </p>
 * <ul>
 * <li><b>Shortening (if the data is too long)</b>: the data is truncated and
 * elipsis is added to it.</li>
 * <li><b>Internal padding (if the data is too short)</b>: the data is padded
 * according the columns alignment.</li>
 * </ul>
 * <p>
 * An important fact, on which the table formatter is based: at the end of this
 * step, the cell data has the exact length (neither shorter nor longer) of the
 * cell.
 * </p>
 * <h2>Table formatting</h2>
 * <p>
 * In this phase, the formatted cells are decorated by the table borders. The
 * decoration is made according the settings of the class
 * {@linkplain BorderFormatter}.
 * </p>
 * <h2>Record separators</h2>
 * <p>
 * It is possible to add record separators between records by inserting
 * <code>null</code> values in the input.
 * </p>
 * <h1>Getting started (a simple example)</h1>
 * <p>
 * Most of the API uses builders and fluent API, so the table declaration could
 * be done in one statement. Here is a simple, but complete example:
 * </p>
 * <p>
 * First we define a record to print. It's simple and has only two data fields:
 * </p>
 *
 * <pre>
 * <code>
 *     private class JavaDocDemoRecord {
 *         private String fruit;
 *         private Double quantity;
 *
 *         public JavaDocDemoRecord(String fruit, Double quantity) {
 *             super();
 *             this.fruit = fruit;
 *             this.quantity = quantity;
 *         }
 *
 *         public String getFruit() {
 *             return fruit;
 *         }
 *
 *         public Double getQuantity() {
 *             return quantity;
 *         }
 *     }
 * </code>
 * </pre>
 * <p>
 * Also, we will need an aggregation object for quantity:
 * </p>
 *
 * <pre>
 * private class JavaDocDemoAggregator {
 *     public double sum;
 * }
 * </pre>
 * <p>
 * Now we can define the table:
 * </p>
 *
 * <pre>
 * <code>
 * {@code TableFormatter<JavaDocDemoRecord> formatter = new TableFormatter.Builder<JavaDocDemoRecord>()}
 *         .withHeading("Java doc demo")
 *         .withShowAggregation(true)
 *         .withSeparateDataWithLines(true)
 *         .withBorderFormatter(new BorderFormatter.Builder(DefaultFormatters.ASCII_LINEDRAW_DOUBLE).build())
 *         .withColumn(new ColumnDefinition.StatelessBuilder{@code <JavaDocDemoRecord, String>()}
 *                 .withTitle("Fruit")
 *                 .withAggregateRowConstant("TOTAL")
 *                 .withDataExtractor(o -&gt; o.getFruit())
 *                 .withCellContentFormatter(new CellContentFormatter.Builder().withMinWidth(8).build())
 *                 .build())
 *         .withColumn(new ColumnDefinition.Builder{@code <JavaDocDemoRecord, JavaDocDemoAggregator, Double>()}
 *                 .withTitle("Quantity")
 *                 .withCellContentFormatter(CellContentFormatter.rightAlignedCell())
 *                 .withDataConverter(NumberDataConverter.defaultDoubleFormatter())
 *                 .withDataExtractor(new DataExtractor{@code <>}((o, s) -&gt; {
 *                     double v = o.getQuantity();
 *                     s.sum += v;
 *                     return v;
 *                 }, () -&gt; new JavaDocDemoAggregator(), (s) -&gt; s.sum))
 *                 .build())
 *         .build();
 * </code>
 * </pre>
 *
 * <p>
 * Let's have a statement by statement overlook of the above code. First of all,
 * the {@linkplain TableFormatter} uses build pattern, so we instatiate a
 * builder with the type of our record. By calling the
 * {@linkplain Builder#withHeading(String)} two things happen: we assign a
 * heading line to the table and with the assignment we declare that a heading
 * should be printed. Also, we tell the formatter to calculate and print
 * aggregation line at the end of the table
 * ({@linkplain Builder#withShowAggregation(boolean)}). With the
 * {@linkplain Builder#withSeparateDataWithLines(boolean)} we force the
 * formatter to add lines between records. Then we choose the border formatter
 * to be used by the {@linkplain Builder#withBorderFormatter(BorderFormatter)}
 * method. (For the sake of simplicity we use a preset and doesn't make any
 * additional configuration.)
 * </p>
 * <p>
 * Next we add two columns to the table. The first one is a stateless string
 * column, so we use the {@linkplain ColumnDefinition.StatelessBuilder}. We add
 * title to the column
 * ({@linkplain ColumnDefinition.StatelessBuilder#withTitle(String)}) and tell
 * it to use a string constant in the aggregation line
 * ({@linkplain ColumnDefinition.StatelessBuilder#withAggregateRowConstant(String)}).
 * Because it is a stateless column, we can pass a closure as data extractor
 * ({@linkplain ColumnDefinition.StatelessBuilder#withDataExtractor(java.util.function.Function)}).
 * Finally, we choose a left aligned cell formatter (this is the default) but
 * restrict the column not to be shorter than 8 characters.
 * ({@linkplain ColumnDefinition.StatelessBuilder#withCellContentFormatter(hu.vissy.texttable.contentformatter.CellContentFormatter)})
 * </p>
 * <p>
 * The second column is a stateful one, because we would like to sum the
 * quantities. Therefore we use the
 * {@linkplain ColumnDefinition.StatefulBuilder} as builder. The title setting
 * is the same, and we use defaults for both data converter
 * ({@linkplain NumberDataConverter#defaultDoubleFormatter()}) and cell
 * formatting ({@linkplain CellContentFormatter#rightAlignedCell()}). The data
 * extractor is a little bit more complex, because we have to provide three
 * colsures: one for initializing the state class, one for extracting data
 * (which also responsible to update the state class) and one to aquire the
 * state data for the aggregation row.
 * </p>
 * <p>
 * That's it! Now our table is configured and ready to use. Let's make some data
 * to print:
 * </p>
 *
 * <pre>
 * <code>
 * {@code List<JavaDocDemoRecord> data = new ArrayList<>();}
 * data.add(new JavaDocDemoRecord("apple", 120.5d));
 * data.add(new JavaDocDemoRecord("banana", 20.119d));
 * data.add(null);
 * data.add(new JavaDocDemoRecord("cherry", 1551d));
 * </code>
 * </pre>
 *
 * <p>
 * Note the third record: it is null indicating there should be a inter-record
 * separator inserted.
 * </p>
 * <p>
 * There is only one task left: to apply the formatter on the data.
 * </p>
 *
 * <pre>
 * String s = formatter.apply(data);
 * System.out.println(s);
 * </pre>
 *
 * <p>
 * The output should be something like this:
 * </p>
 *
 * <pre>
 * +=====================+
 * | Java doc demo       |
 * +==========+==========+
 * | Fruit    | Quantity |
 * +==========+==========+
 * | apple    |   120,50 |
 * +----------+----------+
 * | banana   |    20,12 |
 * +==========+==========+
 * | cherry   |  1551,00 |
 * +==========+==========+
 * | TOTAL    |  1691,62 |
 * +==========+==========+
 * </pre>
 * <p>
 * This was a very simple example only showing the general usage of the API.
 * There are many additional configuration possibilities this example didn't
 * show.
 * </p>
 *
 * @author Balage
 *
 * @param <D>
 *            The type of the input record.
 * @since 1.0.0
 */
public class TableFormatter<D> {

    /**
     * Builder for {@linkplain TableFormatter}.
     *
     * @author Balage
     *
     * @param <D>
     *            The type of the input record.
     */
    public static class Builder<D> {
        private List<ColumnDefinition<D, ?, ?>> columns;
        private BorderFormatter borderFormatter = new BorderFormatter.Builder(BorderFormatter.DEFAULT_TYPE).build();
        private String heading = null;
        private boolean showAggregation = false;
        private boolean separateDataWithLines = false;
        private DataConverter<String> headerConverter = new StringDataConverter();

        /**
         * The constructor of the builder.
         */
        public Builder() {
            this.columns = new ArrayList<>();

        }

        /**
         * Adds a column (definition) to table.
         *
         * @param column
         *            The column definition.
         * @return The builder instance.
         */
        public Builder<D> withColumn(ColumnDefinition<D, ?, ?> column) {
            this.columns.add(column);
            return this;
        }

        /**
         * Defines the border formatter to use.
         *
         * @param borderFormatter
         *            The border formatter.
         * @return The builder instance.
         */
        public Builder<D> withBorderFormatter(BorderFormatter borderFormatter) {
            this.borderFormatter = borderFormatter;
            return this;
        }

        /**
         * Specifies the heading (titlebar) of the table. By setting the heading
         * to any not null value, the display of heading is implicitely enabled.
         *
         * @param heading
         *            The heading text. Note that the heading text will never
         *            extend the table width, it will be truncated instead.
         * @return The builder instance.
         */
        public Builder<D> withHeading(String heading) {
            this.heading = heading;
            return this;
        }

        /**
         * Speficies whether to calculate and show aggregation row.
         *
         * @param showAggregation
         *            If true, an aggregation row will be displayed at the
         *            bottom of the table
         * @return The builder instance.
         */
        public Builder<D> withShowAggregation(boolean showAggregation) {
            this.showAggregation = showAggregation;
            return this;
        }

        /**
         * Specifies whether to add lines between data rows. Note, that this is
         * not the separator lines forced by adding null records to the input,
         * however the separator line has higher priority and would hide
         * inter-record lines.
         *
         * @param separateDataWithLines
         *            If true, a line will be drawn between each data rows.
         * @return The builder instance.
         */
        public Builder<D> withSeparateDataWithLines(boolean separateDataWithLines) {
            this.separateDataWithLines = separateDataWithLines;
            return this;
        }



        /**
         * Defines alternative data converter for header column.
         *
         * @param headerConverter
         *            The new header converter.
         * @return The builder instance.
         */
        public Builder<D> withHeaderConverter(DataConverter<String> headerConverter) {
            this.headerConverter = headerConverter;
            return this;
        }

        /**
         * @return The created {@linkplain TableFormatter} istance.
         */
        public TableFormatter<D> build() {
            return new TableFormatter<>(this);
        }
    }

    class IndexedColumnDefinition<S, T> {
        private ColumnDefinition<D, S, T> definition;
        private int index;

        public IndexedColumnDefinition(int index, ColumnDefinition<D, S, T> definition) {
            super();
            this.definition = definition;
            this.index = index;
        }

        public ColumnDefinition<D, S, T> getDefinition() {
            return definition;
        }

        public String getTitle() {
            return definition.getTitle();
        }

        public int getIndex() {
            return index;
        }
    }

    private List<IndexedColumnDefinition<?, ?>> columns;
    private BorderFormatter borderFormatter;
    private String heading;
    private boolean showAggregation;
    private boolean separateDataWithLines;
    private DataConverter<String> headerConverter;

    private TableFormatter(Builder<D> builder) {
        this.showAggregation = builder.showAggregation;
        List<IndexedColumnDefinition<?, ?>> cols = new ArrayList<>();
        builder.columns.stream().forEach(cd -> cols.add(new IndexedColumnDefinition<>(cols.size(), cd)));
        this.columns = Collections.unmodifiableList(cols);
        this.borderFormatter = builder.borderFormatter;
        this.heading = builder.heading;
        this.separateDataWithLines = builder.separateDataWithLines;
        this.headerConverter = builder.headerConverter;
    }


    /**
     * Applies the formatter on the list of records.
     *
     * @param data
     *            The data apply the formatter to.
     * @return The formatted table.
     */
    public String apply(List<D> data) {
        // Building intermediate structure
        TableData<D> td = processData(data);
        // Calculating column widths
        List<Integer> widths = td.getColumnWidths();

        StringBuilder sb = new StringBuilder();
        // Printing heading
        if (heading != null) {
            sb.append(borderFormatter.drawLine(widths, LineType.TOP_EDGE, true));
            int maxHeadingWidth = borderFormatter.calculateOneColumnWidth(widths);
            if (heading.length() > maxHeadingWidth) {
                heading = new EllipsisDecorator.Builder().build().decorate(heading, maxHeadingWidth);
            }
            sb.append(borderFormatter.drawData(
                    Collections.singletonList(String.format("%1$-" + maxHeadingWidth + "s", heading)),
                    RowType.HEADING));
            sb.append(borderFormatter.drawLine(widths, LineType.HEADING_LINE, false));
        } else {
            sb.append(borderFormatter.drawLine(widths, LineType.TOP_EDGE, false));
        }

        // Printing header
        sb.append(borderFormatter.drawData(columns.stream()
                .map(cd -> cd.getDefinition().getCellContentFormatter()
                        .formatCell(headerConverter.convert(cd.getTitle()), widths.get(cd.getIndex())))
                .collect(Collectors.toList()), RowType.HEADER));
        sb.append(borderFormatter.drawLine(widths, LineType.HEADER_LINE, false));

        // Printing data rows
        boolean prevSep = true;
        for (TableRow tr : td.getRows()) {
            if (td.isSeparator(tr)) {
                sb.append(borderFormatter.drawLine(widths, LineType.SEPARATOR_LINE, false));
                prevSep = true;
            } else {
                if (separateDataWithLines && !prevSep) {
                    sb.append(borderFormatter.drawLine(widths, LineType.INTERNAL_LINE, false));
                }
                sb.append(borderFormatter.drawData(columns.stream()
                        .map(cd -> cd.getDefinition().getCellContentFormatter().formatCell(tr.getValue(cd.getIndex()), widths.get(cd.getIndex())))
                        .collect(Collectors.toList()), RowType.DATA));
                prevSep = false;
            }
        }
        // Printing aggragation row
        if (showAggregation) {
            sb.append(borderFormatter.drawLine(widths, LineType.AGGREGATE_LINE, false));
            sb.append(borderFormatter.drawData(
                    columns.stream().map(cd -> cd.getDefinition().getCellContentFormatter().formatCell(
                            td.getAggregateRow().getValue(cd.getIndex()),
                            widths.get(cd.getIndex())))
                            .collect(Collectors.toList()),
                    RowType.AGGREGATE));

        }
        sb.append(borderFormatter.drawLine(widths, LineType.BOTTOM_EDGE, false));

        return sb.toString();
    }


    /**
     * Produces the intermediate data structure. The transformation applies the
     * data extraction and data conversion steps.
     * <p>
     * <i>Note: this function has not much use except for debugging purposes,
     * but is kept for future use, such as CSV export.</i>
     * </p>
     *
     * @param data
     *            The data to transform.
     * @return The transformed data as an intermediate imutable structure.
     */
    public TableData<D> processData(List<D> data) {
        TableData<D> td = new TableData<>(data, this);
        return td;
    }


    /**
     * @return The unmodifiable list of columns
     */
    public List<IndexedColumnDefinition<?, ?>> getColumns() {
        return columns;
    }


    /**
     * @return The border formatter
     */
    public BorderFormatter getBorderFormatter() {
        return borderFormatter;
    }


    /**
     * @return The heading text
     */
    public String getHeading() {
        return heading;
    }


    /**
     * @return Whether to print aggregation row
     */
    public boolean isShowAggregation() {
        return showAggregation;
    }


    /**
     * @return Whether to print lines between data rows
     */
    public boolean isSeparateDataWithLines() {
        return separateDataWithLines;
    }


    /**
     * @return The header converter
     */
    public DataConverter<String> getHeaderConverter() {
        return headerConverter;
    }


}
