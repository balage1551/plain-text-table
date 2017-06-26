package hu.vissy.texttable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hu.vissy.texttable.column.ColumnDefinition;

/**
 * Contains the processed data of a table. The processed data is over the
 * extraction and conversion phase, but not tailored (aligned, padded,
 * shortened) to the cell.
 *
 * @author Balage
 *
 * @param <D>
 *            The type of the input record
 */
public final class TableData<D> {

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
    private List<Integer> widths;


    TableData(List<ColumnDefinition<D, ?, ?>> columns, List<D> data, boolean calculateAggregation) {
        super();
        this.calculateAggregation = calculateAggregation;
        columns.stream().forEach(c -> this.columns.add(new ColumnInfo(c)));
        populate(data);
    }

    /**
     * @return The number of columns.
     */
    public int getColumnCount() {
        return columns.size();
    }

    private void populate(List<D> data) {
        // Initialize the state objects
        columns.forEach(ci -> ci.initializeState());

        // Iterates over the real data records
        for (D d : data) {
            if (d == null) {
                rows.add(separator);
            } else {
                TableRow row = new TableRow(getColumnCount());
                // Calculates the cell values
                for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                    TableData<D>.ColumnInfo ci = columns.get(columnIndex);
                    ColumnDefinition<D, ?, ?> cd = ci.getDefinition();
                    String value = cd.getRowData(d, ci.getState());
                    row.setData(columnIndex, value);
                }
                rows.add(row);
            }
        }

        // Calculates the aggregated values
        if (calculateAggregation) {
            aggregateRow = new TableRow(getColumnCount());
            for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                TableData<D>.ColumnInfo ci = columns.get(columnIndex);
                ColumnDefinition<D, ?, ?> cd = ci.getDefinition();
                String value = cd.getAggregateRowConstant()
                        .orElse(cd.getAggregateData(ci.getState()));
                aggregateRow.setData(columnIndex, value);
            }
        }

        // Calculates the column widths
        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            TableData<D>.ColumnInfo ci = columns.get(columnIndex);
            int maxWidth = ci.getDefinition().getTitle().length();
            for (TableRow r : rows) {
                if (r != separator) {
                    int w = r.getValue(columnIndex).length();
                    if (w > maxWidth) {
                        maxWidth = w;
                    }
                }
            }
            if (calculateAggregation) {
                maxWidth = Math.max(maxWidth, aggregateRow.getValue(columnIndex) == null ? 0 : aggregateRow.getValue(columnIndex).length());
            }

            ci.setWidth(maxWidth);
        }

        // Stores the width information in an unmodifiable list.
        List<Integer> widths = new ArrayList<>(columns.size());
        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            widths.add(columns.get(columnIndex).getWidth());
        }
        this.widths = Collections.unmodifiableList(widths);
    }


    /**
     * @return An unmodifiable list of column widths.
     */
    public List<Integer> getColumnWidths() {
        return widths;
    }

    /**
     * @return The unmodifiable list of row data.
     */
    public List<TableRow> getRowsUnmodifiable() {
        return Collections.unmodifiableList(rows);
    }


    List<TableRow> getRows() {
        return rows;
    }

    /**
     * @return The unmodifiable aggragation data row.
     */
    public TableRow getAggregateRow() {
        return aggregateRow;
    }

    /**
     * Checks if a table row is a separator marker or a common data row.
     * 
     * @param tr
     *            The row to check.
     * @return True if the row is a separator, false if the row is a common data
     *         row.
     */
    public boolean isSeparator(TableRow tr) {
        return tr == separator;
    }


}
