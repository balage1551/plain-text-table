package hu.vissy.texttable.tester;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import hu.vissy.texttable.BorderFormatter;
import hu.vissy.texttable.BorderFormatter.DefaultFormatters;
import hu.vissy.texttable.CsvTableFormatterBuilder;
import hu.vissy.texttable.InputBuilder;
import hu.vissy.texttable.InputRow;
import hu.vissy.texttable.TableFormatter;
import hu.vissy.texttable.column.ColumnDefinition;
import hu.vissy.texttable.contentformatter.CellContentFormatter;
import hu.vissy.texttable.dataconverter.BooleanDataConverter;
import hu.vissy.texttable.dataconverter.DateDataConverter;
import hu.vissy.texttable.dataconverter.DateTimeDataConverter;
import hu.vissy.texttable.dataconverter.NumberDataConverter;
import hu.vissy.texttable.dataconverter.SimpleDurationDataConverter;
import hu.vissy.texttable.dataconverter.TimeDataConverter;
import hu.vissy.texttable.dataconverter.TypedTrivialDataConverter;
import hu.vissy.texttable.dataextractor.StatefulDataExtractor;

public class Tester {

    public static void main(String[] args) {
        new Tester().run(true, true);
        new Tester().run2();
        new Tester().runJavaDocDemo();
        new Tester().run(false, true);
        new Tester().run(true, false);
        new Tester().run(false, false);
        new Tester().runSubTotalDocDemo();
    }

    private static final String[] FRUITS = new String[] { "apple", "banana", "cherry", "date",
            "eggplant", "fig", "huckleberry" };

    private List<TestObject> generate(int count) {
        Random r = new Random(150);
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    if (i == 2 || i == 11) {
                        return null;
                    } else {
                        return new TestObject(i, FRUITS[r.nextInt(FRUITS.length)],
                                Math.floor(100 * r.nextDouble()) / 10,
                                LocalDateTime.now().plusSeconds(
                                        r.nextInt(7200) - 3600),
                                Duration.ofSeconds(r.nextInt(8 * 7200)),
                                r.nextDouble() < .33,
                                r.nextInt(500000));
                    }
                })
                .collect(Collectors.toList());
    }

    private List<TestObject> generate2(int count) {
        Random r = new Random(150);
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    if (i == 2 || i == 11) {
                        return null;
                    } else {
                        return new TestObject(i, FRUITS[i],
                                Math.floor(100 * r.nextDouble()) / 10,
                                LocalDateTime.now().plusSeconds(
                                        r.nextInt(7200) - 3600),
                                Duration.ofSeconds(r.nextInt(8 * 7200)),
                                r.nextDouble() < .33,
                                r.nextInt(500000));
                    }
                })
                .collect(Collectors.toList());
    }

    private class Sum {
        double sum = 0;
        long s = 0;
    }

    private void run(boolean showHeader, boolean showHeading) {
        TableFormatter<TestObject> formatter = new TableFormatter.Builder<TestObject>()
                .withHeading(showHeading ? "Fruit list" : null)
                .withShowHeader(showHeader)
                .withShowAggregation(true)
                .withBorderFormatter(new BorderFormatter.Builder(DefaultFormatters.UNICODE_LINEDRAW).withPaddingWidth(1).build())
                .withSeparateDataWithLines(true)
                .withColumn(new ColumnDefinition.StatelessBuilder<TestObject, String>()
                        .withTitle("Fruit")
                        .withAggregateRowConstant("TOTAL")
                        .withDataExtractor(o -> o.getName())
                        .withDataConverter(new TypedTrivialDataConverter<>(String.class))
                        .withCellContentFormatter(new CellContentFormatter.Builder().withMinWidth(8).build())
                        .build())
                .withColumn(new ColumnDefinition.StatefulBuilder<TestObject, Sum, Double>()
                        .withTitle("Quantity")
                        .withCellContentFormatter(CellContentFormatter.rightAlignedCell())
                        .withDataConverter(NumberDataConverter.defaultDoubleFormatter())
                        .withDataExtractor(new StatefulDataExtractor<>((o, s) -> {
                            double v = o.getQuantity();
                            s.sum += v;
                            return v;
                        }, () -> new Sum(), (k, s) -> s.sum))
                        .build())
                .withColumn(new ColumnDefinition.StatelessBuilder<TestObject, LocalDateTime>()
                        .withTitle("Timestamp").withAggregateRowConstant("")
                        .withDataExtractor(o -> o.getDate())
                        .withDataConverter(new DateTimeDataConverter())
                        .withCellContentFormatter(
                                CellContentFormatter.centeredCell())
                        .build())
                .withColumn(new ColumnDefinition.StatelessBuilder<TestObject, LocalDate>()
                        .withTitle("Date").withAggregateRowConstant("")
                        .withDataExtractor(o -> LocalDate.of(o.getDate().getYear(),
                                o.getDate().getMonth(),
                                o.getDate().getDayOfMonth()))
                        .withDataConverter(new DateDataConverter())
                        .withCellContentFormatter(
                                CellContentFormatter.centeredCell())
                        .build())
                .withColumn(new ColumnDefinition.StatelessBuilder<TestObject, LocalTime>()
                        .withTitle("Time").withAggregateRowConstant("")
                        .withDataExtractor(o -> LocalTime.of(o.getDate().getHour(),
                                o.getDate().getMinute(), o.getDate()
                                        .getSecond()))
                        .withDataConverter(new TimeDataConverter())
                        .withCellContentFormatter(
                                CellContentFormatter.centeredCell())
                        .build())
                .withColumn(new ColumnDefinition.StatefulBuilder<TestObject, Sum, Duration>()
                        .withTitle("Duration")
                        .withDataExtractor(new StatefulDataExtractor<>((o, s) -> {
                            s.s += o.getDuration().getSeconds();
                            return o.getDuration();
                        }, () -> new Sum(), (k, s) -> Duration.ofSeconds(s.s)))
                        .withDataConverter(new SimpleDurationDataConverter())
                        .withCellContentFormatter(
                                CellContentFormatter.rightAlignedCell())
                        .build())
                .withColumn(new ColumnDefinition.StatefulBuilder<TestObject, Sum, Integer>()
                        .withTitle("Length")
                        .withDataExtractor(new StatefulDataExtractor<>((o, s) -> {
                            s.s += o.getLength();
                            return o.getLength();
                        }, () -> new Sum(), (k, s) -> (int) s.s))
                        .withDataConverter(NumberDataConverter.defaultIntegerFormatter())
                        .withCellContentFormatter(
                                CellContentFormatter.rightAlignedCell())
                        .build())
                .withColumn(new ColumnDefinition.StatelessBuilder<TestObject, Boolean>()
                        .withTitle("Valid")
                        .withDataExtractor(o -> o.isValid())
                        .withDataConverter(new BooleanDataConverter("ok", "----"))
                        .withCellContentFormatter(
                                CellContentFormatter.centeredCell())
                        .build())
                .build();
        List<TestObject> data = generate(15);
        System.out.println(formatter.apply(data));


        TableFormatter<TestObject> csvFormatter = new CsvTableFormatterBuilder<TestObject>()
                .withDelimiter(';')
                .fromTableFormatter(formatter)
                .build();

        System.out.println(csvFormatter.apply(data));

    }

    private void run2() {
        TableFormatter<TestObject> formatter = new TableFormatter.Builder<TestObject>()
                .withHeading("Heading")
                .withShowAggregation(true)
                .withBorderFormatter(new BorderFormatter.Builder(DefaultFormatters.NO_VERTICAL).build())
                .withSeparateDataWithLines(true)
                .withColumn(new ColumnDefinition.StatelessBuilder<TestObject, String>()
                        .withTitle("Column #1")
                        .withAggregateRowConstant("TOTAL")
                        .withDataExtractor(o -> o.getName())
                        .withCellContentFormatter(new CellContentFormatter.Builder().withMinWidth(8).build())
                        .build())
                .withColumn(new ColumnDefinition.StatefulBuilder<TestObject, Sum, Double>()
                        .withTitle("Column #2")
                        .withCellContentFormatter(CellContentFormatter.rightAlignedCell())
                        .withDataConverter(NumberDataConverter.defaultDoubleFormatter())
                        .withDataExtractor(new StatefulDataExtractor<>((o, s) -> {
                            double v = o.getQuantity();
                            s.sum += v;
                            return v;
                        }, () -> new Sum(), (k, s) -> s.sum))
                        .build())
                .build();
        String s = formatter.apply(generate2(4));
        s = s.replaceAll("\n", "\n         * ");
        System.out.println(s);

    }

    private class JavaDocDemoRecord {
        private String fruit;
        private Double quantity;

        public JavaDocDemoRecord(String fruit, Double quantity) {
            super();
            this.fruit = fruit;
            this.quantity = quantity;
        }

        public String getFruit() {
            return fruit;
        }

        public Double getQuantity() {
            return quantity;
        }
    }

    private class JavaDocDemoAggregator {
        public double sum;
    }

    private void runJavaDocDemo() {
        TableFormatter<JavaDocDemoRecord> formatter = new TableFormatter.Builder<JavaDocDemoRecord>()
                .withHeading("Java doc demo")
                .withShowAggregation(true)
//                .withSeparateDataWithLines(true)
                .withBorderFormatter(new BorderFormatter.Builder(DefaultFormatters.UNICODE_LINEDRAW).build())
                .withColumn(new ColumnDefinition.StatelessBuilder<JavaDocDemoRecord, String>()
                        .withTitle("Fruit")
                        .withAggregateRowConstant("TOTAL")
                        .withDataExtractor(o -> o.getFruit())
                        .withCellContentFormatter(new CellContentFormatter.Builder().withMinWidth(8).build())
                        .build())
                .withColumn(new ColumnDefinition.StatefulBuilder<JavaDocDemoRecord, JavaDocDemoAggregator, Double>()
                        .withTitle("Quantity")
                        .withCellContentFormatter(CellContentFormatter.rightAlignedCell())
                        .withDataConverter(NumberDataConverter.defaultDoubleFormatter())
                        .withDataExtractor(new StatefulDataExtractor<>((o, s) -> {
                            double v = o.getQuantity();
                            s.sum += v;
                            return v;
                        }, () -> new JavaDocDemoAggregator(), (k, s) -> s.sum))
                        .build())
                .build();

        List<JavaDocDemoRecord> data = new ArrayList<>();
        data.add(new JavaDocDemoRecord("apple", 120.5d));
        data.add(new JavaDocDemoRecord("banana", 20.119d));
        data.add(null);
        data.add(new JavaDocDemoRecord("cherry", 1551d));

        String s = formatter.apply(data);
        System.out.println(s);

    }




    private class SubTotalDemoRecord {
        private String fruit;
        private int quarter;
        private Double quantity;

        public SubTotalDemoRecord(String fruit, int quarter, Double quantity) {
            super();
            this.fruit = fruit;
            this.quarter = quarter;
            this.quantity = quantity;
        }

        public String getFruit() {
            return fruit;
        }

        public int getQuarter() {
            return quarter;
        }

        public Double getQuantity() {
            return quantity;
        }
    }

    private class SubTotalDemoAggregator {
        public String actFruit;
        public double fruitSum;
        public double totalSum;
    }

    private enum SubTotalDemoAggrRowId {
        FRUIT_TOTAL, TOTAL
    }

    private void runSubTotalDocDemo() {
        TableFormatter<SubTotalDemoRecord> formatter = new TableFormatter.Builder<SubTotalDemoRecord>()
                .withHeading("Subtotal demo")
                .withBorderFormatter(new BorderFormatter.Builder(DefaultFormatters.ASCII_LINEDRAW_DOUBLE).build())
                .withColumn(new ColumnDefinition.StatefulBuilder<SubTotalDemoRecord, SubTotalDemoAggregator, String>()
                        .withTitle("Fruit")
                        .withAggregateRowConstant(SubTotalDemoAggrRowId.TOTAL, "GRAND TOTAL")
                        .withDataConverter(new TypedTrivialDataConverter<>(String.class))
                        .withDataExtractor(new StatefulDataExtractor<>(
                                (o, s) -> {
                                    s.actFruit = o.getFruit();
                                    return o.getFruit();
                                },
                                () -> new SubTotalDemoAggregator(),
                                (k, s) -> ((SubTotalDemoAggrRowId) k) == SubTotalDemoAggrRowId.FRUIT_TOTAL ? s.actFruit + " total" : null))
                        .withCellContentFormatter(new CellContentFormatter.Builder().withMinWidth(8).build())
                        .build())
                .withColumn(new ColumnDefinition.StatelessBuilder<SubTotalDemoRecord, Integer>()
                        .withTitle("Quarter")
                        .withDataConverter(NumberDataConverter.defaultIntegerFormatter())
                        .withDataExtractor(o -> o.getQuarter())
                        .withCellContentFormatter(CellContentFormatter.rightAlignedCell())
                        .build())
                .withColumn(new ColumnDefinition.StatefulBuilder<SubTotalDemoRecord, SubTotalDemoAggregator, Double>()
                        .withTitle("Quantity")
                        .withCellContentFormatter(CellContentFormatter.rightAlignedCell())
                        .withDataConverter(NumberDataConverter.defaultDoubleFormatter())
                        .withDataExtractor(new StatefulDataExtractor<>((o, s) -> {
                            double v = o.getQuantity();
                            s.totalSum += v;
                            s.fruitSum += v;
                            return v;
                        }, () -> new SubTotalDemoAggregator(), (k, s) -> {
                            switch ((SubTotalDemoAggrRowId) k) {
                            case FRUIT_TOTAL:
                                double t = s.fruitSum;
                                s.fruitSum = 0;
                                return t;
                            case TOTAL:
                                return s.totalSum;
                            default:
                                throw new IllegalStateException("Unknown aggregation key: " + k);
                            }
                        }))
                        .build())
                .build();

        InputBuilder<SubTotalDemoRecord> builder = new InputBuilder<>();
        builder.addData(new SubTotalDemoRecord("apple", 1, 120.5d));
        builder.addData(new SubTotalDemoRecord("apple", 2, 50.5d));
        builder.addData(new SubTotalDemoRecord("apple", 3, 100d));
        builder.addData(new SubTotalDemoRecord("apple", 4, 34d));
        builder.addAggregator(SubTotalDemoAggrRowId.FRUIT_TOTAL);

        builder.addData(new SubTotalDemoRecord("banana", 1, 20.119d));
        builder.addData(new SubTotalDemoRecord("banana", 3, 10d));
        builder.addAggregator(SubTotalDemoAggrRowId.FRUIT_TOTAL);
        builder.addAggregator(SubTotalDemoAggrRowId.TOTAL);

        List<InputRow<SubTotalDemoRecord>> data = builder.build();
        String s = formatter.applyToInput(data);
        System.out.println(s);

        TableFormatter<SubTotalDemoRecord> csvFormatter = new CsvTableFormatterBuilder<SubTotalDemoRecord>()
                .withDelimiter(';')
                .fromTableFormatter(formatter)
                .build();

        System.out.println(csvFormatter.applyToInput(data));

    }

}
