package hu.vissy.texttable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A builder to construct InputRow stucture.
 *
 * @author Balage
 *
 * @param <D>
 *            The type of the input record.
 */
/**
 * @author Balage
 *
 * @param <D>
 */
public class InputBuilder<D> {

    private List<InputRow<D>> rows = new ArrayList<>();

    /**
     * Convenient method to convert Version 1 simple data structure to InputRow
     * structure.
     *
     * @param showAggregation
     *            Whether to add final aggregator row.
     * @param data
     *            The list of data and separator (null) records.
     * @return The constructed list of input rows.
     */
    public static <D> List<InputRow<D>> convertFromVersion1(boolean showAggregation, List<D> data) {
        InputBuilder<D> builder = new InputBuilder<>();
        builder.addMixed(data);
        if (showAggregation) {
            builder.addAggregator(null);
        }
        return builder.build();
    }

    /**
     * Adds a data row.
     *
     * @param data
     *            The data to add.
     * @return The builder.
     */
    public InputBuilder<D> addData(D data) {
        rows.add(new DataRow<>(data));
        return this;
    }

    /**
     * Adds a separator marker.
     *
     * @return The builder.
     */
    public InputBuilder<D> addSeparator() {
        rows.add(new SeparatorRow<>());
        return this;
    }

    /**
     * Adds an aggregator row marker.
     *
     * @param key
     *            The aggregator key.
     *
     * @return The builder.
     */
    public InputBuilder<D> addAggregator(Object key) {
        rows.add(new AggregatorRow<>(key));
        return this;
    }

    /**
     * Adds old style records (used in version 1).
     *
     * @param data
     *            Data and separator (null) records.
     * @return The builder.
     */
    public InputBuilder<D> addMixed(Collection<D> data) {
        data.forEach(d -> rows.add(d == null ? new SeparatorRow<>() : new DataRow<>(d)));
        return this;
    }

    /**
     * @return Returns the rows (modifiable list) and keeps the records.
     */
    public List<InputRow<D>> getRows() {
        return rows;
    }

    /**
     * @return Returns the rows but clears the list (avoiding change anomalies).
     */
    public List<InputRow<D>> build() {
        List<InputRow<D>> res = rows;
        rows = new ArrayList<>();
        return res;
    }

}
