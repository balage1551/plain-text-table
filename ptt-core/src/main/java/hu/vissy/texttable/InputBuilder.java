package hu.vissy.texttable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InputBuilder<D> {

    private List<InputRow<D>> rows = new ArrayList<>();

    public static <D> List<InputRow<D>> convertFromVersion1(boolean showAggregation, List<D> data) {
        InputBuilder<D> builder = new InputBuilder<>();
        builder.addMixed(data);
        if (showAggregation) {
            builder.addAggregator(null);
        }
        return builder.build();
    }

    public InputBuilder<D> addData(D data) {
        rows.add(new DataRow<>(data));
        return this;
    }

    public InputBuilder<D> addSeparator() {
        rows.add(new SeparatorRow<>());
        return this;
    }

    public InputBuilder<D> addAggregator(Object key) {
        rows.add(new AggregatorRow<>(key));
        return this;
    }

    public InputBuilder<D> addMixed(Collection<D> data) {
        data.forEach(d -> rows.add(d == null ? new SeparatorRow<>() : new DataRow<>(d)));
        return this;
    }

    public List<InputRow<D>> getRows() {
        return rows;
    }

    public List<InputRow<D>> build() {
        List<InputRow<D>> res = rows;
        rows = new ArrayList<>();
        return res;
    }

}
