package hu.vissy.texttable.dataextractor;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class DataExtractor<D, S, T> {

    private Supplier<S> stateInitializer = () -> null;
    private BiFunction<D, S, T> rowDataExtractor;
    private Function<S, T> aggregateDataExtractor = s -> null;

    public DataExtractor(BiFunction<D, S, T> rowDataExtractor, Supplier<S> stateInitializer, Function<S, T> aggregateDataExtractor) {
        super();
        this.stateInitializer = stateInitializer;
        this.rowDataExtractor = rowDataExtractor;
        this.aggregateDataExtractor = aggregateDataExtractor;
    }

    public Supplier<S> getStateInitializer() {
        return stateInitializer;
    }

    public BiFunction<D, S, T> getRowDataExtractor() {
        return rowDataExtractor;
    }

    public Function<S, T> getAggregateDataExtractor() {
        return aggregateDataExtractor;
    }


    public T extractRowData(D d, S state) {
        return rowDataExtractor.apply(d, state);
    }

    public T extractAggregateData(S state) {
        return aggregateDataExtractor.apply(state);
    }


}
