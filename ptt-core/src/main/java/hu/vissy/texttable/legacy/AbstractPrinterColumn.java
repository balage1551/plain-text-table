package hu.vissy.texttable.legacy;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import hu.vissy.texttable.columndefinition.ColumnDefinition;
import hu.vissy.texttable.columndefinition.ColumnDefinition.Builder;

/**
 * Abstract base class for column definitions.
 *
 * @author balage
 *
 * @param <D>
 *            The data the column works on
 * @param <S>
 *            The state object assigned to the column
 * @param <T>
 *            The type of the data it emits
 * @param <A>
 *            The class itself (internal generic parameter: for inheritence and
 *            builder pattern)
 */
public abstract class AbstractPrinterColumn<D, S, T, A extends AbstractPrinterColumn<D, S, T, A>> {

    // Decorator is a post creation callback to alter the behaviour of the
    // column definition.
    private Consumer<ColumnDefinition.Builder> decorator;
    private String title = "(unnamed)";

    private BiFunction<D, S, T> dataExtractor = (d, s) -> null;
    private Supplier<S> stateInitializer = () -> null;
    private Function<S, T> baselineExtractor = (s) -> null;

    public AbstractPrinterColumn(String title, BiFunction<D, S, T> dataExtractor) {
        super();
        this.title = title;
        this.dataExtractor = dataExtractor;
    }


    public AbstractPrinterColumn(String title, BiFunction<D, S, T> dataExtractor, Consumer<Builder> decorator) {
        super();
        this.title = title;
        this.dataExtractor = dataExtractor;
        this.decorator = decorator;
    }

    /**
     * Creates the column definition of the column.
     *
     * @return the decorated column definition.
     */
    public ColumnDefinition getColumnDefinition() {
        Builder builder = getColumnBuilder().withTitle(getTitle());
        if (decorator != null) {
            decorator.accept(builder);
        }
        return builder.build();
    }

    /**
     * @return A title of the column.
     */
    public String getTitle() {
        return title;
    }


    /**
     * Decorator is a post creation callback to alter the behaviour of the
     * column definition.
     *
     * @param decorator
     *            The decorator.
     * @return The object itself (fluent api)
     */
    @SuppressWarnings("unchecked")
    public A withDecorator(Consumer<ColumnDefinition.Builder> decorator) {
        this.decorator = decorator;
        return (A) this;
    }


    @SuppressWarnings("unchecked")
    public A withStateInitializer(Supplier<S> stateInitializer) {
        this.stateInitializer = stateInitializer;
        return (A) this;
    }


    @SuppressWarnings("unchecked")
    public A withBaselineExtractor(Function<S, T> baselineExtractor) {
        this.baselineExtractor = baselineExtractor;
        return (A) this;
    }


    /**
     * Returns the builder implementation of the corresponding column
     * definition.
     *
     * @return The column definition builder.
     */
    protected abstract ColumnDefinition.Builder getColumnBuilder();

    @SuppressWarnings("unchecked")
    T getData(D data, Object object) {
        return extractData(data, (S) object);
    }

    @SuppressWarnings("unchecked")
    public T getBaselineData(Object object) {
        return calculateBaselineData((S) object);
    }

    /**
     * Extracts the data from the context.
     *
     * @param context
     *            The context to process.
     * @return The extracted data.
     */
    private T extractData(D data, S state) {
        return dataExtractor.apply(data, state);
    }


    private T calculateBaselineData(S state) {
        return baselineExtractor.apply(state);
    }


    S createState() {
        return stateInitializer.get();
    }





}
