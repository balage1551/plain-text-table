package hu.vissy.texttable.column;

import java.util.Optional;
import java.util.function.Function;

import hu.vissy.texttable.contentformatter.CellContentFormatter;
import hu.vissy.texttable.dataconverter.DataConverter;
import hu.vissy.texttable.dataconverter.TrivialDataConverter;
import hu.vissy.texttable.dataextractor.DataExtractor;
import hu.vissy.texttable.dataextractor.StatelessDataExtractor;

/**
 * The column definition implementation.
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


    protected static class BuilderBase<D, S, T, A extends BuilderBase<D, S, T, A>> {
        protected CellContentFormatter cellContentFormatter = CellContentFormatter
                .leftAlignedCell();
        protected String title = "";
        protected DataConverter<T> dataConverter = new TrivialDataConverter<>();
        protected DataExtractor<D, S, T> dataExtractor;
        protected String aggregateRowConstant = null;

        public BuilderBase() {

        }

        @SuppressWarnings("unchecked")
        public A withCellContentFormatter(CellContentFormatter cellContentFormatter) {
            this.cellContentFormatter = cellContentFormatter;
            return (A) this;
        }

        @SuppressWarnings("unchecked")
        public A withTitle(String title) {
            this.title = title;
            return (A) this;
        }

        @SuppressWarnings("unchecked")
        public A withDataConverter(DataConverter<T> dataConverter) {
            this.dataConverter = dataConverter;
            return (A) this;
        }

        @SuppressWarnings("unchecked")
        public A withDataExtractor(DataExtractor<D, S, T> dataExtractor) {
            this.dataExtractor = dataExtractor;
            return (A) this;
        }

        @SuppressWarnings("unchecked")
        public A withAggregateRowConstant(String aggregateRowConstant) {
            this.aggregateRowConstant = aggregateRowConstant;
            return (A) this;
        }

        public ColumnDefinition<D, S, T> build() {
            if (dataExtractor == null) {
                throw new IllegalStateException("dataExtractor should be set.");
            }
            return new ColumnDefinition<>(this);
        }
    }

    public static class Builder<D, S, T> extends BuilderBase<D, S, T, Builder<D, S, T>> {
    }

    public static class StatelessBuilder<D, T>
            extends BuilderBase<D, Void, T, StatelessBuilder<D, T>> {

        public StatelessBuilder<D, T> withDataExtractor(
                StatelessDataExtractor<D, T> dataExtractor) {
            this.dataExtractor = dataExtractor;
            return this;
        }

        public StatelessBuilder<D, T> withDataExtractor(Function<D, T> dataExtractorCallback) {
            withDataExtractor(new StatelessDataExtractor<>(dataExtractorCallback));
            return this;
        }
    }

    private String title;
    private DataExtractor<D, S, T> dataExtractor;
    private DataConverter<T> dataConverter;
    private CellContentFormatter cellContentFormatter;
    private int index;
    private Optional<String> aggregateRowConstant;

    private ColumnDefinition(BuilderBase<D, S, T, ?> builder) {
        this.title = builder.title;
        this.cellContentFormatter = builder.cellContentFormatter;
        this.dataConverter = builder.dataConverter;
        this.dataExtractor = builder.dataExtractor;
        this.aggregateRowConstant = Optional.ofNullable(builder.aggregateRowConstant);
    }


    public String getTitle() {
        return title;
    }


    public DataExtractor<D, S, T> getDataExtractor() {
        return dataExtractor;
    }


    public DataConverter<T> getDataConverter() {
        return dataConverter;
    }


    public CellContentFormatter getCellContentFormatter() {
        return cellContentFormatter;
    }


    @SuppressWarnings("unchecked")
    public String getRowData(D d, Object state) {
        return getDataConverter().convert(getDataExtractor().extractRowData(d, (S) state));
    }


    @SuppressWarnings("unchecked")
    public String getAggregateData(Object state) {
        return getDataConverter().convert(getDataExtractor().extractAggregateData((S) state));
    }


    public void setIndex(int index) {
        this.index = index;
    }


    public int getIndex() {
        return index;
    }


    public Optional<String> getAggregateRowConstant() {
        return aggregateRowConstant;
    }


}
