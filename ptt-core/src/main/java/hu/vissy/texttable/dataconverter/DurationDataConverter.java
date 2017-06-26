package hu.vissy.texttable.dataconverter;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DurationDataConverter implements DataConverter<Duration> {

    DateTimeFormatter formatter;

    public DurationDataConverter() {
        this.formatter = DateTimeFormatter.ISO_LOCAL_TIME;
    }


    public DurationDataConverter(DateTimeFormatter formatter) {
        super();
        this.formatter = formatter;
    }

    @Override
    public String convert(Duration data) {
        if (data == null)
            return null;

        long s = data.getSeconds();
        LocalTime t = LocalTime.of((int) (s / 3600), (int) ((s % 3600) / 60), (int) (s % 60));

        return formatter.format(t);
    }

}
