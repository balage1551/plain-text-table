package hu.vissy.texttable.wiki.dataconverter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hu.vissy.texttable.TableFormatter;
import hu.vissy.texttable.column.ColumnDefinition;
import hu.vissy.texttable.dataconverter.SimpleDurationDataConverter;

public class DurationExample {

    public static void main(String[] args) {

        TableFormatter<Duration> formatter = new TableFormatter.Builder<Duration>()
                .withColumn(new ColumnDefinition.StatelessBuilder<Duration, Duration>()
                        .withTitle("Duration")
                        .withDataConverter(new SimpleDurationDataConverter())
                        .withDataExtractor(d -> d)
                        .build())
                .build();

        List<Duration> data = new ArrayList<>(Arrays.asList(
                new Duration[] { Duration.ZERO, Duration.of(12, ChronoUnit.HOURS), Duration.of(1000000, ChronoUnit.SECONDS) }));

        System.out.println(formatter.apply(data));
    }

}
