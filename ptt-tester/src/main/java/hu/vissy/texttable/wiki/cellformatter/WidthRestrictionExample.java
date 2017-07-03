package hu.vissy.texttable.wiki.cellformatter;

import java.util.Collections;
import java.util.List;

import hu.vissy.texttable.TableFormatter;
import hu.vissy.texttable.column.ColumnDefinition;
import hu.vissy.texttable.contentformatter.CellContentFormatter;

public class WidthRestrictionExample {

    public static void main(String[] args) {
        TableFormatter<Object> formatter = new TableFormatter.Builder<>()
                .withColumn(new ColumnDefinition.StatelessBuilder<>()
                        .withCellContentFormatter(new CellContentFormatter.Builder().withMinWidth(10).build())
                        .withTitle("min")
                        .withDataExtractor(d -> d)
                        .build())
                .withColumn(new ColumnDefinition.StatelessBuilder<>()
                        .withCellContentFormatter(new CellContentFormatter.Builder().withMaxWidth(4).build())
                        .withTitle("max")
                        .withDataExtractor(d -> d)
                        .build())
                .build();

        List<Object> data = Collections.singletonList("apple");

        System.out.println(formatter.apply(data));
    }

}
