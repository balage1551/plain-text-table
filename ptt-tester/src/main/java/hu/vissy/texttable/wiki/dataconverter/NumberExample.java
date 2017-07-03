package hu.vissy.texttable.wiki.dataconverter;

import java.util.Collections;
import java.util.List;

import hu.vissy.texttable.TableFormatter;
import hu.vissy.texttable.column.ColumnDefinition;
import hu.vissy.texttable.dataconverter.NumberDataConverter;

public class NumberExample {

    private static class Data {
        Double aDouble;
        Integer anInteger;
        Long aLong;

        public Data(Double aDouble, Integer anInteger, Long aLong) {
            super();
            this.aDouble = aDouble;
            this.anInteger = anInteger;
            this.aLong = aLong;
        }

        public Double getDouble() {
            return aDouble;
        }

        public Integer getInteger() {
            return anInteger;
        }

        public Long getLong() {
            return aLong;
        }
    }

    public static void main(String[] args) {
        TableFormatter<Data> formatter = new TableFormatter.Builder<Data>()
                .withColumn(new ColumnDefinition.StatelessBuilder<Data, Double>()
                        .withTitle("double")
                        .withDataConverter(NumberDataConverter.defaultDoubleFormatter())
                        .withDataExtractor(d -> d.getDouble())
                        .build())
                .withColumn(new ColumnDefinition.StatelessBuilder<Data, Integer>()
                        .withTitle("int")
                        .withDataConverter(NumberDataConverter.defaultIntegerFormatter())
                        .withDataExtractor(d -> d.getInteger())
                        .build())
                .withColumn(new ColumnDefinition.StatelessBuilder<Data, Long>()
                        .withTitle("long")
                        .withDataConverter(NumberDataConverter.defaultLongFormatter())
                        .withDataExtractor(d -> d.getLong())
                        .build())
                .build();

        List<Data> data = Collections.singletonList(new Data(Math.PI, 1551, 123456789l));

        System.out.println(formatter.apply(data));
    }

}
