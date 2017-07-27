package hu.vissy.texttable;

public class DataRow<D> extends InputRow<D> {

    private D data;

    /**
     * @param data
     */
    public DataRow(D data) {
        super();
        this.data = data;
    }

    public D getData() {
        return data;
    }

}
