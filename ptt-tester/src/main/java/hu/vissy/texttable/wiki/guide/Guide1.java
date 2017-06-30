package hu.vissy.texttable.wiki.guide;

import java.util.ArrayList;
import java.util.List;

import hu.vissy.texttable.TableFormatter;
import hu.vissy.texttable.column.ColumnDefinition;
import hu.vissy.texttable.contentformatter.CellContentFormatter;
import hu.vissy.texttable.dataconverter.NumberDataConverter;

public class Guide1 {


    public static void main(String[] args) {
        TableFormatter<Foo1> formatter = new TableFormatter.Builder<Foo1>()
                .withColumn(new ColumnDefinition.StatelessBuilder<Foo1, Integer>()
                        .withTitle("bar")
                        .withDataExtractor(foo -> foo.getBar())
                        .withDataConverter(NumberDataConverter.defaultIntegerFormatter())
                        .withCellContentFormatter(CellContentFormatter.rightAlignedCell())
                        .build())
                .build();

        List<Foo1> data = new ArrayList<>();
        data.add(new Foo1(1));

        System.out.println(formatter.apply(data));
    }
}
