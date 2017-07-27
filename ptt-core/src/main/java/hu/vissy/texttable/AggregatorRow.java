package hu.vissy.texttable;

public class AggregatorRow<D> extends InputRow<D> {

    private Object key;

    public AggregatorRow(Object key) {
        super();
        this.key = key;
    }

    public Object getKey() {
        return key;
    }
}
