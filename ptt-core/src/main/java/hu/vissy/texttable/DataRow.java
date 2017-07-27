package hu.vissy.texttable;

/**
 * A row containing data.
 *
 * @author Balage
 *
 * @param <D>
 *            The type of the input record.
 */
public class DataRow<D> extends InputRow<D> {

    private D data;

    /**
     * Constructor.
     *
     * @param data
     *            The row data.
     */
    public DataRow(D data) {
        super();
        this.data = data;
    }

    /**
     * @return The row data.
     */
    public D getData() {
        return data;
    }

}
