package hu.vissy.texttable;

import java.util.ArrayList;
import java.util.List;

import hu.vissy.texttable.column.ColumnDefinition;

public class TableData<D> {

    private class ColumnInfo {
        private ColumnDefinition<D, ?, ?> definition;
        private Object state;
        private int width;

        public ColumnInfo(ColumnDefinition<D, ?, ?> definition) {
            super();
            this.definition = definition;
        }

        public ColumnDefinition<D, ?, ?> getDefinition() {
            return definition;
        }

        public Object getState() {
            return state;
        }

        public Object initializeState() {
            return state = definition.getDataExtractor().getStateInitializer().get();
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }


    }

    private List<ColumnInfo> columns = new ArrayList<>();
    private List<TableRow> rows = new ArrayList<>();
    private TableRow separator = new TableRow(0);
    private TableRow aggregateRow;
    private boolean calculateAggregation;


    public TableData(List<ColumnDefinition<D, ?, ?>> columns, List<D> data, boolean calculateAggregation) {
        super();
        this.calculateAggregation = calculateAggregation;
        columns.stream().forEach(c -> this.columns.add(new ColumnInfo(c)));
        populate(data);
    }

    public int getColumnCount() {
        return columns.size();
    }

    private void populate(List<D> data) {
        columns.forEach(ci -> ci.initializeState());
        for (D d : data) {
            if (d == null) {
                rows.add(separator);
            } else {
                TableRow row = new TableRow(getColumnCount());
                for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                    TableData<D>.ColumnInfo ci = columns.get(columnIndex);
                    ColumnDefinition<D, ?, ?> cd = ci.getDefinition();
                    String value = cd.getRowData(d, ci.getState());
                    row.setData(columnIndex, value);
                }
                rows.add(row);
            }
        }

        if (calculateAggregation) {
            aggregateRow = new TableRow(getColumnCount());
            for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                TableData<D>.ColumnInfo ci = columns.get(columnIndex);
                ColumnDefinition<D, ?, ?> cd = ci.getDefinition();
                String value = cd.getAggregateData(ci.getState());
                aggregateRow.setData(columnIndex, value);
            }
        }

        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            TableData<D>.ColumnInfo ci = columns.get(columnIndex);
            int maxWidth = ci.getDefinition().getTitle().length();
            for (TableRow r : rows) {
                if (r != separator) {
                    int w = r.getValue(columnIndex).length();
                    if (w < maxWidth) {
                        maxWidth = w;
                    }
                }
            }
            if (calculateAggregation) {
                maxWidth = Math.max(maxWidth, aggregateRow.getValue(columnIndex).length());
            }

            ci.setWidth(maxWidth);
        }
    }


    public List<Integer> getColumnWidths() {
        List<Integer> widths = new ArrayList<>(columns.size());
        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            widths.add(columns.get(columnIndex).getWidth());
        }
        return widths;
    }

    public List<TableRow> getRows() {
        return rows;
    }

    public boolean isSeparator(TableRow tr) {
        return tr == separator;
    }


}
