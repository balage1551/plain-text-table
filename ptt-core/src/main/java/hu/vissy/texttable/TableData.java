package hu.vissy.texttable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hu.vissy.texttable.TableRow.Type;
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
    private TableRow separator = new TableRow(Type.SEPARATOR, 0);
    private List<Integer> widths;


    TableData(List<InputRow<D>> data, TableFormatter<D> tableFormatter) {
        super();
        tableFormatter.getColumns().stream().forEach(c -> this.columns.add(new ColumnInfo(c.getDefinition())));
        populate(data, tableFormatter);
    }

    /**
     * @return The number of columns.
     */
    public int getColumnCount() {
        return columns.size();
    }

    private void populate(List<InputRow<D>> data, TableFormatter<D> tableFormatter) {
        // Initialize the state objects
        columns.forEach(ci -> ci.initializeState());

        // Iterates over the real data records
        for (InputRow<D> ir : data) {
            if (ir instanceof SeparatorRow) {
                rows.add(separator);
            } else if (ir instanceof AggregatorRow) {
                TableRow aggregateRow = new TableRow(Type.AGGREGATOR, getColumnCount());
                for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                    TableData<D>.ColumnInfo ci = columns.get(columnIndex);
                    ColumnDefinition<D, ?, ?> cd = ci.getDefinition();
                    Object key = ((AggregatorRow<D>) ir).getKey();
                    String value = cd.getAggregateRowConstant(key).orElse(cd.getAggregateData(key, ci.getState()));
                    aggregateRow.setData(columnIndex, value);
                }
                rows.add(aggregateRow);
            } else if (ir instanceof DataRow) {
                D d = ((DataRow<D>) ir).getData();
                TableRow row = new TableRow(Type.DATA, getColumnCount());
                // Calculates the cell values
                for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                    TableData<D>.ColumnInfo ci = columns.get(columnIndex);
                    ColumnDefinition<D, ?, ?> cd = ci.getDefinition();
                    String value = cd.getRowData(d, ci.getState());
                    row.setData(columnIndex, value);
                }
                rows.add(row);
            } else {
                throw new IllegalArgumentException("Unknown input row type: " + ir.getClass());
            }
        }

        // Calculates the column widths
        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            TableData<D>.ColumnInfo ci = columns.get(columnIndex);
            int maxWidth = tableFormatter.getHeaderConverter().convert(ci.getDefinition().getTitle()).length();
            for (TableRow r : rows) {
                if (r.getType() != Type.SEPARATOR) {
                    String value = r.getValue(columnIndex);
                    if (value == null) {
                        value = ci.getDefinition().getCellContentFormatter().getNullValue();
                    }
                    int w = value.length();
                    if (w > maxWidth) {
                        maxWidth = w;
                    }
                }
            }

            ci.setWidth(maxWidth);
        }

        // Bounds the width with column constraints And stores the width information in an unmodifiable list.
        List<Integer> widths = new ArrayList<>(columns.size());
        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            TableData<D>.ColumnInfo ci = columns.get(columnIndex);
            widths.add(ci.getDefinition().getCellContentFormatter().boundWidth(columns.get(columnIndex).getWidth()));
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

}
