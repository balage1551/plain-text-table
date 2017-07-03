package hu.vissy.texttable.wiki.dataconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import hu.vissy.texttable.TableFormatter;
import hu.vissy.texttable.column.ColumnDefinition;

public class CollectionExample {

    public static void main(String[] args) {

        TableFormatter<List<Integer>> formatter = new TableFormatter.Builder<List<Integer>>()
                .withColumn(new ColumnDefinition.StatelessBuilder<List<Integer>, List<Integer>>()
                        .withTitle("List")
                        .withDataConverter((c) -> c == null ? null : c.stream().sorted().map(i -> "" + i).collect(Collectors.joining("; ")))
                        .withDataExtractor(d -> d)
                        .build())
                .build();

        List<List<Integer>> data = Collections.singletonList(new ArrayList<>(Arrays.asList(
                new Integer[] { 42, 1, 1551, -2 })));

        System.out.println(formatter.apply(data));
    }

}
