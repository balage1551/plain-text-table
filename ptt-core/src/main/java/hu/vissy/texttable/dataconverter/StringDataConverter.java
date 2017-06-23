package hu.vissy.texttable.dataconverter;

public class StringDataConverter implements DataConverter<String> {

    @Override
    public String convert(String data) {
        return data;
    }

}
