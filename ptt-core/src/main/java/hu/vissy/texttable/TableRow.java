package hu.vissy.texttable;

import java.util.Arrays;

public class TableRow {

    private String[] values;

    public TableRow(int columnCount) {
        super();
        values = new String[columnCount];
        Arrays.fill(values, "");
    }


    void setData(int columnIndex, String value) {
        values[columnIndex] = value;
    }



    public String[] getValues() {
        return values;
    }

    public String getValue(int columnIndex) {
        return values[columnIndex];
    }



}
