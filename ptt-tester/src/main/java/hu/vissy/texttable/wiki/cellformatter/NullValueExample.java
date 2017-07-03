package hu.vissy.texttable.wiki.cellformatter;

import java.util.Collections;
import java.util.List;

import hu.vissy.texttable.TableFormatter;
import hu.vissy.texttable.column.ColumnDefinition;
import hu.vissy.texttable.contentformatter.CellContentFormatter;

public class NullValueExample {

    public static void main(String[] args) {
        TableFormatter<Object> formatter = new TableFormatter.Builder<>()
                .withColumn(new ColumnDefinition.StatelessBuilder<>()
                        .withTitle("standard")
                        .withDataExtractor(d -> d == "" ? null : d)
                        .build())
                .withColumn(new ColumnDefinition.StatelessBuilder<>()
                        .withCellContentFormatter(new CellContentFormatter.Builder().withNullValue("(null)").build())
                        .withTitle("custom")
                        .withDataExtractor(d -> d == "" ? null : d)
                        .build())
                .build();

        List<Object> data = Collections.singletonList("");

        System.out.println(formatter.apply(data));
    }

}
