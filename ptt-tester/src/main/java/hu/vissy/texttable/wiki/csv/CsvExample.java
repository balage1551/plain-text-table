package hu.vissy.texttable.wiki.csv;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import hu.vissy.texttable.BorderFormatter;
import hu.vissy.texttable.BorderFormatter.DefaultFormatters;
import hu.vissy.texttable.BorderFormatter.RowSpec;
import hu.vissy.texttable.TableFormatter;
import hu.vissy.texttable.column.ColumnDefinition;
import hu.vissy.texttable.contentformatter.CellAlignment;
import hu.vissy.texttable.contentformatter.CellContentFormatter;
import hu.vissy.texttable.contentformatter.EllipsisDecorator;
import hu.vissy.texttable.dataconverter.DataConverter;
import hu.vissy.texttable.dataconverter.NumberDataConverter;

public class CsvExample {

    private static final char CSV_DELIMITER = ';';

    private static class Data {
        private String string;
        private LocalDateTime dateTime;
        private int anInteger;
        private double aDouble;

        public Data(String string, LocalDateTime dateTime, int anInteger, double aDouble) {
            super();
            this.string = string;
            this.dateTime = dateTime;
            this.anInteger = anInteger;
            this.aDouble = aDouble;
        }

        public String getString() {
            return string;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public int getInteger() {
            return anInteger;
        }

        public double getDouble() {
            return aDouble;
        }
    }

    private static class CsvStringDataConverter<T> implements DataConverter<T> {

        @Override
        public String convert(T d) {
            return d == null ? null : "\"" + d.toString().replaceAll(Pattern.quote("\""), "\"\"") + "\"";
        }
    }

    public static void main(String[] args) {

        NumberFormat csvDoubleFormatter = NumberFormat.getInstance();
        csvDoubleFormatter.setGroupingUsed(false);
        csvDoubleFormatter.setRoundingMode(RoundingMode.HALF_UP);
        NumberDataConverter<Double> csvDoubleConverter = new NumberDataConverter<>(Double.class, csvDoubleFormatter);

        NumberFormat csvIntegerFormatter = NumberFormat.getInstance();
        csvIntegerFormatter.setMaximumFractionDigits(0);
        csvIntegerFormatter.setMinimumFractionDigits(0);
        csvIntegerFormatter.setGroupingUsed(false);
        csvIntegerFormatter.setRoundingMode(RoundingMode.UNNECESSARY);
        NumberDataConverter<Integer> csvIntegerConverter = new NumberDataConverter<>(Integer.class, csvIntegerFormatter);

        DataConverter<LocalDateTime> csvDateTimeDataConverter = (s) -> s == null ? null : s.toString().replaceAll("T", " ");

        CellContentFormatter csvCellContentFormatter = new CellContentFormatter.Builder()
                .withEllipsesDecorator(new EllipsisDecorator.Builder().withEllipsisSign("").build())
                .withCellAlignment(new CellAlignment() {
                    @Override
                    public String align(String data, int width) {
                        return data;
                    }
                })
                .build();


        TableFormatter<Data> formatter = new TableFormatter.Builder<Data>()
                .withHeaderConverter(new CsvStringDataConverter<String>())
                .withBorderFormatter(new BorderFormatter.Builder(DefaultFormatters.EMPTY)
                        .withUniformRow(new RowSpec('\0', CSV_DELIMITER, '\0'))
                        .withDrawVerticalSeparator(true)
                        .build())
                .withColumn(new ColumnDefinition.StatelessBuilder<Data, String>()
                        .withTitle("Name")
                        .withCellContentFormatter(csvCellContentFormatter)
                        .withDataConverter(new CsvStringDataConverter<>())
                        .withDataExtractor(d -> d.getString())
                        .build())
                .withColumn(new ColumnDefinition.StatelessBuilder<Data, LocalDateTime>()
                        .withTitle("Date")
                        .withCellContentFormatter(csvCellContentFormatter)
                        .withDataConverter(csvDateTimeDataConverter)
                        .withDataExtractor(d -> d.getDateTime())
                        .build())
                .withColumn(new ColumnDefinition.StatelessBuilder<Data, Integer>()
                        .withTitle("Integer")
                        .withCellContentFormatter(csvCellContentFormatter)
                        .withDataConverter(csvIntegerConverter)
                        .withDataExtractor(d -> d.getInteger())
                        .build())
                .withColumn(new ColumnDefinition.StatelessBuilder<Data, Double>()
                        .withTitle("Double")
                        .withCellContentFormatter(csvCellContentFormatter)
                        .withDataConverter(csvDoubleConverter)
                        .withDataExtractor(d -> d.getDouble())
                        .build())
                .build();

        List<Data> data = new ArrayList<>();
        data.add(new Data("alma", LocalDateTime.now(), 42, Math.PI));
        data.add(new Data(null, null, 7000, 2d / 3));
        data.add(new Data("banana \"juice\"", LocalDateTime.now().minus(100, ChronoUnit.HOURS), -42, 5d));

        System.out.println(formatter.apply(data));
    }

}
