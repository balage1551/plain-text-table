package hu.vissy.texttable.dataconverter;

public abstract class TypedDataConverter<T> implements DataConverter<T> {

    private Class<T> acceptedClass;

    /**
     * Constructor to save erased generic type information.
     *
     * @param acceptedClass
     *            The class the converter accepts
     */
    public TypedDataConverter(Class<T> acceptedClass) {
        super();
        this.acceptedClass = acceptedClass;
    }

    public Class<T> getAcceptedClass() {
        return acceptedClass;
    }

}
