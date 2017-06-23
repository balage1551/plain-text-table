package hu.vissy.texttable.tester;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import hu.vissy.texttable.TableFormatter;
import hu.vissy.texttable.column.ColumnDefinition;
import hu.vissy.texttable.contentformatter.CellContentFormatter;
import hu.vissy.texttable.dataextractor.DataExtractor;
import hu.vissy.texttable.dataextractor.StatelessDataExtractor;

public class Tester {

    public static void main(String[] args) {
        new Tester().run();
    }

    private static final String[] FRUITS = new String[] { "apple", "banana", "cherry", "date",
            "eggplant", "fig", "huckleberry" };

    private List<TestObject> generate(int count) {
        Random r = new Random(42);
        return IntStream.range(0, count)
                .mapToObj(i -> new TestObject(i, FRUITS[r.nextInt(FRUITS.length)],
                        Math.floor(100 * r.nextDouble()) / 10))
                .collect(Collectors.toList());
    }

    private class Sum {
        double sum = 0;
    }

    private void run() {
        TableFormatter<TestObject> formatter = new TableFormatter.Builder<TestObject>()
                .withHeading("Alma")
                .withShowAggregation(true)
                .withColumn(new ColumnDefinition.Builder<TestObject, Void, String>()
                        .withTitle("Fruit")
                        .withDataExtractor(new StatelessDataExtractor<>(o -> o.getName()))
                        .withCellContentFormatter(new CellContentFormatter.Builder().withMinWidth(8).build())
                        .build())
                .withColumn(new ColumnDefinition.Builder<TestObject, Sum, Double>()
                        .withTitle("Quantity")
                        .withCellContentFormatter(CellContentFormatter.rightAlignedCell())
                        .withDataExtractor(new DataExtractor<>((o, s) -> {
                            double v = o.getQuantity();
                            s.sum += v;
                            return v;
                        }, () -> new Sum(), (s) -> s.sum))
                        .build())
                .build();

        formatter.apply(generate(10));
    }
}
