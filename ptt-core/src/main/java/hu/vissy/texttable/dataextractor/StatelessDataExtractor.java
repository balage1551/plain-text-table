package hu.vissy.texttable.dataextractor;

import java.util.function.Function;

/**
 * A simplified version of {@linkplain DataExtractor} for stateless columns.
 * <p>
 * This is a shortcut for the statement:
 * </p>
 *
 * <pre>
 *      {@code new DataExtractor<D, Void, T>((d,s) -> rowDataExtractor.apply(d), () -> null, (k, s) -> null);}
 * </pre>
 *
 * @author Balage
 *
 * @param <D>
 *            The type of the input record.
 * @param <T>
 *            The type of the cell value.
 */
public class StatelessDataExtractor<D, T> extends DataExtractor<D, Void, T> {

    /**
     * The costructor.
     *
     * @param rowDataExtractor
     *            A closure which extracts the value from the data record. This
     *            closure will be called for each (non null) data record in the
     *            same order they are in the input list.
     */
    public StatelessDataExtractor(Function<D, T> rowDataExtractor) {
        super((d, s) -> rowDataExtractor.apply(d), () -> null, (k, s) -> null);
    }

}
