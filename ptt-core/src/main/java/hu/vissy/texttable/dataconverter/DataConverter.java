package hu.vissy.texttable.dataconverter;

/**
 * The data converter is responsible to convert cell values to string and
 * formatting them while converting.
 *
 * @author Balage
 *
 * @param <T>
 *            The type of the cell value.
 */
@FunctionalInterface
public interface DataConverter<T> {

    /**
     * Converts the data to string.
     * 
     * @param data
     *            The data to convert.
     * @return The converted data.
     */
    public String convert(T data);
}
