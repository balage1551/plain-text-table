package hu.vissy.texttable;

/**
 * An aggregation row marker.
 *
 * @author Balage
 *
 * @param <D>
 *            The type of the input record.
 */
public class AggregatorRow<D> extends InputRow<D> {

    private Object key;

    /**
     * Constructor.
     *
     * @param key
     *            The aggregation key the row should handle.
     */
    public AggregatorRow(Object key) {
        super();
        this.key = key;
    }

    /**
     * @return The aggregation key.
     */
    public Object getKey() {
        return key;
    }
}
