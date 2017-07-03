package hu.vissy.texttable.wiki.dataconverter;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import hu.vissy.texttable.TableFormatter;
import hu.vissy.texttable.column.ColumnDefinition;
import hu.vissy.texttable.dataconverter.NumberDataConverter;

public class NumberExample2 {

    public static void main(String[] args) {
        NumberFormat numberFormatter = NumberFormat.getInstance(Locale.US);
        numberFormatter.setMaximumFractionDigits(5);
        numberFormatter.setMinimumFractionDigits(2);
        numberFormatter.setGroupingUsed(true);
        numberFormatter.setRoundingMode(RoundingMode.FLOOR);

        TableFormatter<Double> formatter = new TableFormatter.Builder<Double>()
                .withColumn(new ColumnDefinition.StatelessBuilder<Double, Double>()
                        .withTitle("double")
                        .withDataConverter(new NumberDataConverter<>(numberFormatter))
                        .withDataExtractor(d -> d)
                        .build())
                .build();

        List<Double> data = new ArrayList<>(Arrays.asList(
                new Double[] { Math.PI, -2.5d, 1234567d }));

        System.out.println(formatter.apply(data));
    }

}
