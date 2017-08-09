package hu.vissy.texttable.dataconverter;

/**
 * A trivial data converter for strings. it returns the data unmodified.
 *
 * @author Balage
 *
 */
public class StringDataConverter extends TypedDataConverter<String> {

    /**
     * Constructor.
     */
    public StringDataConverter() {
        super(String.class);
    }

    @Override
    public String convert(String data) {
        return data;
    }

}
