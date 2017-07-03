package hu.vissy.texttable.wiki.dataconverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import hu.vissy.texttable.TableFormatter;
import hu.vissy.texttable.column.ColumnDefinition;
import hu.vissy.texttable.dataconverter.DateDataConverter;
import hu.vissy.texttable.dataconverter.DateTimeDataConverter;
import hu.vissy.texttable.dataconverter.TimeDataConverter;

public class DateTimeExample {

    private static class Data {
        LocalDate date;
        LocalTime time;
        LocalDateTime dateTime;

        public Data(LocalDate date, LocalTime time, LocalDateTime dateTime) {
            super();
            this.date = date;
            this.time = time;
            this.dateTime = dateTime;
        }

        public LocalDate getDate() {
            return date;
        }

        public LocalTime getTime() {
            return time;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }
    }

    public static void main(String[] args) {
        TableFormatter<Data> formatter = new TableFormatter.Builder<Data>()
                .withColumn(new ColumnDefinition.StatelessBuilder<Data, LocalDate>()
                        .withTitle("date (ISO)")
                        .withDataConverter(new DateDataConverter())
                        .withDataExtractor(d -> d.getDate())
                        .build())
                .withColumn(new ColumnDefinition.StatelessBuilder<Data, LocalTime>()
                        .withTitle("time (ISO)")
                        .withDataConverter(new TimeDataConverter())
                        .withDataExtractor(d -> d.getTime())
                        .build())
                .withColumn(new ColumnDefinition.StatelessBuilder<Data, LocalDateTime>()
                        .withTitle("date/time (ISO)")
                        .withDataConverter(new DateTimeDataConverter())
                        .withDataExtractor(d -> d.getDateTime())
                        .build())
                .withColumn(new ColumnDefinition.StatelessBuilder<Data, LocalDate>()
                        .withTitle("date (c)")
                        .withDataConverter(new DateDataConverter(DateTimeFormatter.ofPattern("dd-MMM-yy", Locale.UK)))
                        .withDataExtractor(d -> d.getDate())
                        .build())
                .withColumn(new ColumnDefinition.StatelessBuilder<Data, LocalTime>()
                        .withTitle("time (c)")
                        .withDataConverter(new TimeDataConverter(DateTimeFormatter.ofPattern("hh:mm a", Locale.UK)))
                        .withDataExtractor(d -> d.getTime())
                        .build())
                .withColumn(new ColumnDefinition.StatelessBuilder<Data, LocalDateTime>()
                        .withTitle("date/time (c)")
                        .withDataConverter(new DateTimeDataConverter(DateTimeFormatter.ofPattern("DDD HH:mm (EEE)", Locale.UK)))
                        .withDataExtractor(d -> d.getDateTime())
                        .build())
                .build();

        List<Data> data = Collections.singletonList(new Data(LocalDate.now(), LocalTime.now(), LocalDateTime.now()));

        System.out.println(formatter.apply(data));
    }

}
