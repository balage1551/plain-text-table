package hu.vissy.texttable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a data row in the table.
 *
 * <p>
 * The data in the row is extracted and converted to string, but no padding,
 * shortening or other features are applied.
 * </p>
 *
 * <p>
 * This class is imutable from outside the TableFormatter.
 * </p>
 *
 * @author Balage
 *
 */
public final class TableRow {

    public enum Type {
        DATA, AGGREGATOR, SEPARATOR
    }

    private String[] values;
    private Type type;

    TableRow(Type type, int columnCount) {
        this.type = type;
        values = new String[columnCount];
        Arrays.fill(values, "");
    }


    void setData(int columnIndex, String value) {
        values[columnIndex] = value;
    }



    /**
     * @return An unmodifiable list of values of the row. (Note: this is for
     *         debug or other processing purposes and has a bad performance.)
     */
    public List<String> getValues() {
        return Collections.unmodifiableList(Arrays.asList(values));
    }

    /**
     * @param columnIndex
     *            The index of the column.
     * @return The value assigned to the given column.
     * @throws IndexOutOfBoundsException
     *             When the index is out of bounds of the row.
     */
    public String getValue(int columnIndex) {
        return values[columnIndex];
    }


    public Type getType() {
        return type;
    }

}
