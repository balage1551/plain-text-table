package hu.vissy.texttable.wiki.csv;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import hu.vissy.texttable.CsvTableFormatterBuilder;
import hu.vissy.texttable.TableFormatter;

public class CsvExample2 {

    private static final char CSV_DELIMITER = ';';

    private static class Data {
        private String string;
        private LocalDateTime dateTime;
        private LocalDate date;
        private LocalTime time;
        private int anInteger;
        private double aDouble;

        public Data(String string, LocalDateTime dateTime, LocalDate date, LocalTime time, int anInteger, double aDouble) {
            super();
            this.string = string;
            this.dateTime = dateTime;
            this.date = date;
            this.time = time;
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

        public LocalDate getDate() {
            return date;
        }

        public LocalTime getTime() {
            return time;
        }


    }

    public static void main(String[] args) {

        TableFormatter<Data> formatter = new CsvTableFormatterBuilder<Data>()
                .withDelimiter(CSV_DELIMITER)
                .withMaximumFractionDigits(4)
                .withStringColumn("apple", d -> d.getString())
                .withIntegerColumn("integer", d -> d.getInteger())
                .withDoubleColumn("double", d -> d.getDouble())
                .withDateTimeColumn("datetime", d -> d.getDateTime())
                .withDateColumn("date", d -> d.getDate())
                .withTimeColumn("time", d -> d.getTime())
                .build();

        List<Data> data = new ArrayList<>();
        data.add(new Data("alma", LocalDateTime.now(), LocalDate.now(), LocalTime.now(), 42, Math.PI));
        data.add(new Data(null, null, null, null, 7000, 2d / 3));
        data.add(new Data("banana \"juice\"", LocalDateTime.now().minus(100, ChronoUnit.HOURS),
                LocalDate.now().plus(100, ChronoUnit.DAYS), LocalTime.now().minus(3, ChronoUnit.HOURS), -42, 5d));

        System.out.println(formatter.apply(data));
    }

}
