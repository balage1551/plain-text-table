package hu.vissy.texttable.dataconverter;

import java.text.NumberFormat;

public class NumberDataConverter<T extends Number> implements DataConverter<T> {

    NumberFormat formatter;

    public NumberDataConverter() {
        this(NumberFormat.getInstance());
    }


    public NumberDataConverter(NumberFormat formatter) {
        super();
        this.formatter = formatter;
    }



    @Override
    public String convert(T data) {
        if (data == null) {
            return null;
        }

        return formatter.format(data);
    }

}
