package hu.vissy.texttable.dataextractor;

import java.util.function.Function;

public class StatelessDataExtractor<D, T> extends DataExtractor<D, Void, T> {

    public StatelessDataExtractor(Function<D, T> rowDataExtractor) {
        super((d, s) -> rowDataExtractor.apply(d), () -> null, (s) -> null);
    }

}
