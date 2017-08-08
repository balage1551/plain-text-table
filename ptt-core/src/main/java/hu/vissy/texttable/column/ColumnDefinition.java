package hu.vissy.texttable.column;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import hu.vissy.texttable.contentformatter.CellContentFormatter;
import hu.vissy.texttable.dataconverter.DataConverter;
import hu.vissy.texttable.dataconverter.TrivialDataConverter;
import hu.vissy.texttable.dataextractor.DataExtractor;
import hu.vissy.texttable.dataextractor.StatefulDataExtractor;
import hu.vissy.texttable.dataextractor.StatelessDataExtractor;

/**
 * The column definition implementation.
 *
 * <p>
 * A column definition contains information how to extract the value from the
 * record, how to convert it to string and how to format it within the cell.
 * </p>
 *
 * @author Balage
 *
 * @param <D>
 *            The type of the input record.
 * @param <S>
 *            The type of the state class.
 * @param <T>
 *            The type of the cell value.
 */
public class ColumnDefinition<D, S, T> {


    private static abstract class BuilderBase<D, S, T, A extends BuilderBase<D, S, T, A>> {
        protected CellContentFormatter cellContentFormatter = CellContentFormatter
                .leftAlignedCell();
        protected String title = "";
        protected DataConverter<T> dataConverter = new TrivialDataConverter<>();
        protected DataExtractor<D, S, T> dataExtractor;
        protected Map<Object, String> aggregateRowConstants = new HashMap<>();


        /**
         * Sets the cell content formatter for the column.
         * <p>
         * The content formatter is responsible for fitting the value into the
         * given width of the column. It shorten and applies elipsis to the
         * value if it is too long, or align and pad if it is too short.
         * </p>
         *
         * @param cellContentFormatter
         *            The cell formatter instance.
         * @return The builder instance.
         */
        @SuppressWarnings("unchecked")
        public A withCellContentFormatter(CellContentFormatter cellContentFormatter) {
            this.cellContentFormatter = cellContentFormatter;
            return (A) this;
        }

        /**
         * Sets the title (header) of the column.
         *
         * @param title
         *            The title of the column.
         * @return The builder instance.
         */
        @SuppressWarnings("unchecked")
        public A withTitle(String title) {
            this.title = title;
            return (A) this;
        }

        /**
         * Sets the data converter.
         * <p>
         * The data converter is responsible to convert the cell data to string
         * while formatting it.
         * </p>
         *
         * @param dataConverter
         *            The data converter.
         * @return The builder instance.
         */
        @SuppressWarnings("unchecked")
        public A withDataConverter(DataConverter<T> dataConverter) {
            this.dataConverter = dataConverter;
            return (A) this;
        }

        /**
         * When the column is stateless, an empty value is returned as
         * aggregated value by default. With this setter, an alternative
         * constant value may be provided.
         * <p>
         * This method is a shortcut to the
         * <code>withAggregateRowConstant(null, aggregateRowConstant)</code>
         * function call.
         * </p>
         * <p>
         * <i>Note, that setting this value to a non-null value will override
         * the aggregate value even for a stateful column.</i>
         * </p>
         *
         * @param aggregateRowConstant
         *            The constant string to return.
         * @return The builder instance.
         */
        public A withAggregateRowConstant(String aggregateRowConstant) {
            return withAggregateRowConstant(null, aggregateRowConstant);
        }


        /**
         * When the column is stateless, an empty value is returned as
         * aggregated value by default. With this setter, an alternative
         * constant value may be provided.
         * <p>
         * <i>Note, that setting this value to a non-null value will override
         * the aggregate value even for a stateful column.</i>
         * </p>
         *
         * @param key
         *            Key of the aggregator row.
         * @param aggregateRowConstant
         *            The constant string to return.
         * @return The builder instance.
         */
        @SuppressWarnings("unchecked")
        public A withAggregateRowConstant(Object key, String aggregateRowConstant) {
            this.aggregateRowConstants.put(key, aggregateRowConstant);
            return (A) this;
        }

        /**
         * @return The constructed {@linkplain ColumnDefinition} instance.
         */
        public ColumnDefinition<D, S, T> build() {
            if (dataExtractor == null) {
                throw new IllegalStateException("dataExtractor should be set.");
            }
            return new ColumnDefinition<>(this);
        }
    }

    /**
     * A builder for stateful columns.
     *
     * @author Balage
     *
     * @param <D>
     *            The type of the input record.
     * @param <S>
     *            The type of the state class.
     * @param <T>
     *            The type of the cell value.
     */
    public static class StatefulBuilder<D, S, T> extends BuilderBase<D, S, T, StatefulBuilder<D, S, T>> {

        /**
         * Sets the data extractor.
         * <p>
         * The data extractor is responsible to aquire the column data from the
         * record. Also, when the column is stateful, it should maintain the
         * state object.
         * </p>
         *
         * @param dataExtractor
         *            The data extractor instance.
         * @return The builder instance.
         */
        public StatefulBuilder<D, S, T> withDataExtractor(StatefulDataExtractor<D, S, T> dataExtractor) {
            this.dataExtractor = dataExtractor;
            return this;
        }
    }

    /**
     * A simplified, stateless implementation of the builder.
     *
     * @author Balage
     *
     * @param <D>
     *            The type of the input record.
     * @param <T>
     *            The type of the cell value.
     */
    public static class StatelessBuilder<D, T>
            extends BuilderBase<D, Void, T, StatelessBuilder<D, T>> {

        /**
         * Sets the data extractor.
         * <p>
         * This method is more strict allowing only stateless extractors to be
         * set.
         * </p>
         *
         * @param dataExtractor
         *            The data extractor instance.
         * @return The builder instance.
         */
        public StatelessBuilder<D, T> withDataExtractor(
                StatelessDataExtractor<D, T> dataExtractor) {
            this.dataExtractor = dataExtractor;
            return this;
        }


        /**
         * A convenient method for creating data extractor.
         * <p>
         * A stateless column doesn't have to specify state object initializer
         * and aggregate value provider, so the only real parameter is the
         * extractor closure which makes it possible to provide it directly
         * without explicitly constructing a {@linkplain DataExtractor}
         * instance.
         * </p>
         *
         * @param dataExtractorCallback
         *            The data extractor closure.
         * @return The builder instance.
         */
        public StatelessBuilder<D, T> withDataExtractor(Function<D, T> dataExtractorCallback) {
            withDataExtractor(new StatelessDataExtractor<>(dataExtractorCallback));
            return this;
        }
    }


    /**
     * Convenient function for quick prototyping. Creates a simple column using
     * only defaults.
     *
     * @param title
     *            The title of the column.
     * @param dataExtractorCallback
     *            The data extractor closure.
     * @param <D>
     *            The type of the input record.
     * @param <T>
     *            The type of the cell value.
     * @return The created column definition.
     */
    public static <D, T> ColumnDefinition<D, Void, T> createSimpleStateless(String title, Function<D, T> dataExtractorCallback) {
        return new StatelessBuilder<D, T>().withTitle(title).withDataExtractor(dataExtractorCallback).build();
    }

    /**
     * Convenient function for quick prototyping. Creates a simple column using
     * only defaults, but with given alignment.
     *
     * @param title
     *            The title of the column.
     * @param cellFormatter
     *            Cell formatter.
     * @param dataExtractorCallback
     *            The data extractor closure.
     * @param <D>
     *            The type of the input record.
     * @param <T>
     *            The type of the cell value.
     * @return The created column definition.
     */
    public static <D, T> ColumnDefinition<D, Void, T> createSimpleStateless(String title, CellContentFormatter cellFormatter,
            Function<D, T> dataExtractorCallback) {
        return new StatelessBuilder<D, T>()
                .withTitle(title)
                .withCellContentFormatter(cellFormatter)
                .withDataExtractor(dataExtractorCallback)
                .build();
    }

    private String title;
    private DataExtractor<D, S, T> dataExtractor;
    private DataConverter<T> dataConverter;
    private CellContentFormatter cellContentFormatter;
    private Map<Object, String> aggregateRowConstants;

    private ColumnDefinition(BuilderBase<D, S, T, ?> builder) {
        this.title = builder.title;
        this.cellContentFormatter = builder.cellContentFormatter;
        this.dataConverter = builder.dataConverter;
        this.dataExtractor = builder.dataExtractor;
        this.aggregateRowConstants = builder.aggregateRowConstants;
    }


    /**
     * @return The title (header) of the column.
     */
    public String getTitle() {
        return title;
    }


    /**
     * @return The data extractor instance.
     */
    public DataExtractor<D, S, T> getDataExtractor() {
        return dataExtractor;
    }


    /**
     * @return The data converter instance.
     */
    public DataConverter<T> getDataConverter() {
        return dataConverter;
    }


    /**
     * @return The cell formatter instance.
     */
    public CellContentFormatter getCellContentFormatter() {
        return cellContentFormatter;
    }


    /**
     * Extracts the cell data for a data record and converts it to String.
     *
     * @param d
     *            The data record to extract the cell value from.
     * @param state
     *            The state object.
     * @return The extracted and converted value.
     */
    @SuppressWarnings("unchecked")
    public String getRowData(D d, Object state) {
        return getDataConverter().convert(getDataExtractor().extractRowData(d, (S) state));
    }


    /**
     * Extracts the aggregated data and converts it to String.
     *
     * @param key
     *            The aggregation key to query value of.
     * @param state
     *            The state object.
     * @return The extracted and converted value.
     */
    @SuppressWarnings("unchecked")
    public String getAggregateData(Object key, Object state) {
        return getDataConverter().convert(getDataExtractor().extractAggregateData(key, (S) state));
    }


    /**
     * @param key
     *            The aggregation key to query value of.
     * @return The aggregate row constant.
     */
    public Optional<String> getAggregateRowConstant(Object key) {
        return Optional.ofNullable(aggregateRowConstants.get(key));
    }


}
