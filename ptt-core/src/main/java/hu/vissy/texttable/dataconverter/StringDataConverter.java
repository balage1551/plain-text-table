package hu.vissy.texttable.dataconverter;

/**
 * A trivial data converter for strings. it returns the data unmodified.
 *
 * @author Balage
 *
 */
public class StringDataConverter implements DataConverter<String> {

    @Override
    public String convert(String data) {
        return data;
    }

}
