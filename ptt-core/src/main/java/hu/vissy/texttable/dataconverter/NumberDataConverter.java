package hu.vissy.texttable.dataconverter;

import java.math.RoundingMode;
import java.text.NumberFormat;

public class NumberDataConverter<T extends Number> implements DataConverter<T> {

    public static NumberDataConverter<Double> defaultDoubleFormatter() {
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        formatter.setGroupingUsed(false);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        return new NumberDataConverter<>(formatter);
    }

    public static NumberDataConverter<Integer> defaultIntegerFormatter() {
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(0);
        formatter.setMinimumFractionDigits(0);
        formatter.setGroupingUsed(true);
        formatter.setRoundingMode(RoundingMode.UNNECESSARY);
        return new NumberDataConverter<>(formatter);
    }

    public static NumberDataConverter<Long> defaultLongFormatter() {
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(0);
        formatter.setMinimumFractionDigits(0);
        formatter.setGroupingUsed(true);
        formatter.setRoundingMode(RoundingMode.UNNECESSARY);
        return new NumberDataConverter<>(formatter);
    }


    NumberFormat formatter;


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
