package hu.vissy.texttable.dataconverter;

@FunctionalInterface
public interface DataConverter<T> {

    public String convert(T data);
}
