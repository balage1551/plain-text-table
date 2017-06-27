package hu.vissy.texttable.dataconverter;

/**
 * A trivial data converter for any type. it returns empty string when the data
 * is null and calls the {@linkplain Object#toString} method for any non-null
 * data.
 *
 * @author Balage
 *
 */
public class TrivialDataConverter<T> implements DataConverter<T> {

    @Override
    public String convert(T data) {
        return data == null ? "" : data.toString();
    }

}
