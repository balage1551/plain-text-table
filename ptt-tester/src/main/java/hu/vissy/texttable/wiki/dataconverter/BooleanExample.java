package hu.vissy.texttable.wiki.dataconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hu.vissy.texttable.TableFormatter;
import hu.vissy.texttable.column.ColumnDefinition;
import hu.vissy.texttable.dataconverter.BooleanDataConverter;

public class BooleanExample {

    public static void main(String[] args) {

        TableFormatter<Boolean> formatter = new TableFormatter.Builder<Boolean>()
                .withColumn(new ColumnDefinition.StatelessBuilder<Boolean, Boolean>()
                        .withTitle("Test results")
                        .withDataConverter(new BooleanDataConverter("passed", "failed"))
                        .withDataExtractor(d -> d)
                        .build())
                .build();

        List<Boolean> data = new ArrayList<>(Arrays.asList(
                new Boolean[] { true, true, false, true }));

        System.out.println(formatter.apply(data));
    }

}
