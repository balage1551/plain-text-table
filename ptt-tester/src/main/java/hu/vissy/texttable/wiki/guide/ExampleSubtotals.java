package hu.vissy.texttable.wiki.guide;

import hu.vissy.texttable.BorderFormatter;
import hu.vissy.texttable.BorderFormatter.DefaultFormatters;
import hu.vissy.texttable.InputBuilder;
import hu.vissy.texttable.TableFormatter;
import hu.vissy.texttable.column.ColumnDefinition;
import hu.vissy.texttable.contentformatter.CellContentFormatter;
import hu.vissy.texttable.dataconverter.NumberDataConverter;
import hu.vissy.texttable.dataextractor.StatefulDataExtractor;

public class ExampleSubtotals {

    private static class SubTotalDemoRecord {
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

    private static class SubTotalDemoAggregator {
        public String actFruit;
        public double fruitSum;
        public double totalSum;
    }

    private enum SubTotalDemoAggrRowId {
        FRUIT_TOTAL, TOTAL
    }


    public static void main(String[] args) {
        TableFormatter<SubTotalDemoRecord> formatter = new TableFormatter.Builder<SubTotalDemoRecord>()
                .withHeading("Subtotal demo")
                .withBorderFormatter(new BorderFormatter.Builder(DefaultFormatters.ASCII_LINEDRAW_DOUBLE).build())
                .withColumn(new ColumnDefinition.StatefulBuilder<SubTotalDemoRecord, SubTotalDemoAggregator, String>()
                        .withTitle("Fruit")
                        .withAggregateRowConstant(SubTotalDemoAggrRowId.TOTAL, "GRAND TOTAL")
                        .withDataExtractor(new StatefulDataExtractor<>(
                                (o, s) -> {
                                    s.actFruit = o.getFruit();
                                    return o.getFruit();
                                },
                                () -> new SubTotalDemoAggregator(),
                                (k, s) -> {
                                    if (k == SubTotalDemoAggrRowId.FRUIT_TOTAL) {
                                        return s.actFruit + " total";
                                    } else {
                                        return null;
                                    }
                                }))
                        .withCellContentFormatter(new CellContentFormatter.Builder().withMinWidth(8).build())
                        .build())
                .withColumn(new ColumnDefinition.StatelessBuilder<SubTotalDemoRecord, Integer>()
                        .withTitle("Quarter")
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
                                return null;
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

        String s = formatter.applyToInput(builder.build());
        System.out.println(s);
    }
}
