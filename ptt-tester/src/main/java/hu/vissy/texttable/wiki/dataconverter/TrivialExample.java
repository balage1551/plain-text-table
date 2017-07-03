package hu.vissy.texttable.wiki.dataconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import hu.vissy.texttable.TableFormatter;
import hu.vissy.texttable.column.ColumnDefinition;
import hu.vissy.texttable.contentformatter.CenterCellAlignment;
import hu.vissy.texttable.contentformatter.LeftCellAlignment;
import hu.vissy.texttable.contentformatter.RightCellAlignment;

public class TrivialExample {

    public static void main(String[] args) {
        TableFormatter<Object> formatter = new TableFormatter.Builder<>()
                .withColumn(new ColumnDefinition.StatelessBuilder<>()
                        .withTitle("double")
                        .withDataExtractor(d -> d)
                        .build())
                .build();

        List<Object> data = new ArrayList<>(Arrays.asList(new Object[] { "apple", Math.PI, new int[] { 1, 2, 3 }, Collections.singleton("item") }));

        System.out.println(formatter.apply(data));

        System.out.println(new LeftCellAlignment('-').align("abc", 10));
        System.out.println(new RightCellAlignment('-').align("abc", 10));
        System.out.println(new CenterCellAlignment('-').align("abc", 10));
    }

}
