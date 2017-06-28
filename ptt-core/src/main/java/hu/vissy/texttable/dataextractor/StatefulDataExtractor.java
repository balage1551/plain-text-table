package hu.vissy.texttable.dataextractor;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The data extractor for stateful columns.
 * <p>
 * The function of data extractor is to retrieve the value of a column from a
 * data record and to maintain state information while doing it.
 * </p>
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
public class StatefulDataExtractor<D, S, T> extends DataExtractor<D, S, T> {


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
    public StatefulDataExtractor(BiFunction<D, S, T> rowDataExtractor, Supplier<S> stateInitializer, Function<S, T> aggregateDataExtractor) {
        super(rowDataExtractor, stateInitializer, aggregateDataExtractor);
    }

}
