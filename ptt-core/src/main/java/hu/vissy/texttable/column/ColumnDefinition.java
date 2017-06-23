package hu.vissy.texttable.column;

import hu.vissy.texttable.contentformatter.CellContentFormatter;
import hu.vissy.texttable.dataconverter.DataConverter;
import hu.vissy.texttable.dataconverter.TrivialDataConverter;
import hu.vissy.texttable.dataextractor.DataExtractor;

public class ColumnDefinition<D, S, T> {

    private String title;
    private DataExtractor<D, S, T> dataExtractor;
    private DataConverter<T> dataConverter;
    private CellContentFormatter cellContentFormatter;
    private int index;


    public static class Builder<D, S, T> {
        private CellContentFormatter cellContentFormatter = CellContentFormatter.leftAlignedCell();
        private String title = "";
        private DataConverter<T> dataConverter = new TrivialDataConverter<>();
        private DataExtractor<D, S, T> dataExtractor;

        public Builder() {

        }

        public Builder<D, S, T> withCellContentFormatter(CellContentFormatter cellContentFormatter) {
            this.cellContentFormatter = cellContentFormatter;
            return this;
        }

        public Builder<D, S, T> withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder<D, S, T> withDataConverter(DataConverter<T> dataConverter) {
            this.dataConverter = dataConverter;
            return this;
        }

        public Builder<D, S, T> withDataExtractor(DataExtractor<D, S, T> dataExtractor) {
            this.dataExtractor = dataExtractor;
            return this;
        }

        public ColumnDefinition<D, S, T> build() {
            if (dataExtractor == null) {
                throw new IllegalStateException("dataExtractor should be set.");
            }
            return new ColumnDefinition<>(this);
        }
    }


    private ColumnDefinition(Builder<D, S, T> builder) {
        this.title = builder.title;
        this.cellContentFormatter = builder.cellContentFormatter;
        this.dataConverter = builder.dataConverter;
        this.dataExtractor = builder.dataExtractor;
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




}
