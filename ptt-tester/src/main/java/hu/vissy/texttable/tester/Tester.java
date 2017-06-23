package hu.vissy.texttable.tester;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import hu.vissy.texttable.ConfigurableTablePrinter;
import hu.vissy.texttable.PrinterColumnList;
import hu.vissy.texttable.StringPrinterColumn;
import hu.vissy.texttable.columndefinition.ColumnAlignment;

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
        ConfigurableTablePrinter<TestObject> printer;

        PrinterColumnList<TestObject> columnList = new PrinterColumnList<>("Tasks");
        columnList.withPrintBaseline(true);
        columnList.addColumns(
                        new StringPrinterColumn<TestObject, Void>("Alma",
                                        (d, s) -> d.getName())
                        .withBaselineExtractor((s) -> "TOTAL"),
                        new StringPrinterColumn<TestObject, Sum>("Sum", (d, s) -> {
                            s.sum += d.getQuantity();
                            return "" + d.getQuantity();
                        }).withBaselineExtractor((s) -> "" + (s.sum))
                        .withStateInitializer(() -> new Sum()).withDecorator(b -> b
                                        .withAlignment(ColumnAlignment.RIGHT)));
        printer = new ConfigurableTablePrinter<>(columnList);
        System.out.println(printer.print(generate(10)));
    }
}
