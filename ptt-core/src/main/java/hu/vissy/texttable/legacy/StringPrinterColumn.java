package hu.vissy.texttable.legacy;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import hu.vissy.texttable.columndefinition.ColumnDefinition;
import hu.vissy.texttable.columndefinition.StringColumnType;
import hu.vissy.texttable.columndefinition.ColumnDefinition.Builder;

public class StringPrinterColumn<D, S> extends AbstractPrinterColumn<D, S, String, StringPrinterColumn<D, S>> {

    public StringPrinterColumn(String title, BiFunction<D, S, String> dataExtractor) {
        super(title, dataExtractor);
    }

    public StringPrinterColumn(String title, BiFunction<D, S, String> dataExtractor, Consumer<Builder> decorator) {
        super(title, dataExtractor, decorator);
    }

    @Override
    protected Builder getColumnBuilder() {
        return new ColumnDefinition.Builder(new StringColumnType());
    }

}
