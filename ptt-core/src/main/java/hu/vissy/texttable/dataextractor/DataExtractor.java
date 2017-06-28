package hu.vissy.texttable.dataextractor;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The common ancestor of the data extractors.
 *
 * @author Balage
 *
 *
 * @param <D>
 *            The type of the input record.
 * @param <S>
 *            The type of the state class.
 * @param <T>
 *            The type of the cell value.
 */
public abstract class DataExtractor<D, S, T> {

    private Supplier<S> stateInitializer = () -> null;
    private BiFunction<D, S, T> rowDataExtractor;
    private Function<S, T> aggregateDataExtractor = s -> null;

    /**
     * The constructor of the data extractor.
     *
     * @param rowDataExtractor
     *            A closure which extracts the value from the data record. This
     *            closure will be called for each (non null) data record in the
     *            same order they are in the input list. This closure also
     *            responsible to maintain and update the state object.
     * @param stateInitializer
     *            A closure to initialize the state object. It is called once,
     *            before processing any of the data records.
     * @param aggregateDataExtractor
     *            A closure to return aggregated value from the state. It is
     *            called at most once, after all the data records are processed
     *            and the aggregation line is populated.
     */
    public DataExtractor(BiFunction<D, S, T> rowDataExtractor, Supplier<S> stateInitializer, Function<S, T> aggregateDataExtractor) {
        super();
        this.stateInitializer = stateInitializer;
        this.rowDataExtractor = rowDataExtractor;
        this.aggregateDataExtractor = aggregateDataExtractor;
    }

    /**
     * @return The state initializer closure.
     */
    public Supplier<S> getStateInitializer() {
        return stateInitializer;
    }

    /**
     * @return The row data extraction closure.
     */
    public BiFunction<D, S, T> getRowDataExtractor() {
        return rowDataExtractor;
    }

    /**
     * @return The aggregation value extraction closure.
     */
    public Function<S, T> getAggregateDataExtractor() {
        return aggregateDataExtractor;
    }


    /**
     * Extracts the cell data from a data record.
     *
     * @param d
     *            The record to be processed.
     * @param state
     *            The state object.
     * @return The extracted data.
     */
    public T extractRowData(D d, S state) {
        return rowDataExtractor.apply(d, state);
    }

    /**
     * Extracts the aggregation data.
     *
     * @param state
     *            The state object.
     * @return The value for the aggregation.
     */
    public T extractAggregateData(S state) {
        return aggregateDataExtractor.apply(state);
    }


}
