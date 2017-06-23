package hu.vissy.texttable.dataextractor;

import java.util.function.Function;

public class StatelessDataExtractor<D, T> extends DataExtractor<D, Void, T> {

    private String aggregateValue = null;

    public StatelessDataExtractor(Function<D, T> rowDataExtractor) {
        super((d, s) -> rowDataExtractor.apply(d), () -> null, (s) -> null);
    }

    public String getAggregateValue() {
        return aggregateValue;
    }

    public StatelessDataExtractor<D, T> withAggregateValue(String aggregateValue) {
        this.aggregateValue = aggregateValue;
        return this;
    }




}
