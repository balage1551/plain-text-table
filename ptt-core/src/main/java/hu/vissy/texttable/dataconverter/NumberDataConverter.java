package hu.vissy.texttable.dataconverter;

import java.math.RoundingMode;
import java.text.NumberFormat;

public class NumberDataConverter<T extends Number> implements DataConverter<T> {

    NumberFormat formatter;

    public NumberDataConverter() {
        this.formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        formatter.setGroupingUsed(false);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
    }


    public NumberDataConverter(NumberFormat formatter) {
        super();
        this.formatter = formatter;
    }



    @Override
    public String convert(T data) {
        if (data == null)
            return null;

        return formatter.format(data);
    }

}
