package hu.vissy.texttable.dataconverter;

public class TrivialDataConverter<T> implements DataConverter<T> {

    @Override
    public String convert(T data) {
        return data == null ? "" : data.toString();
    }

}
