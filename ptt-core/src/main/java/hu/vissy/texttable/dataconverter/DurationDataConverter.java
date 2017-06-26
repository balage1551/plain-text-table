package hu.vissy.texttable.dataconverter;

import java.time.Duration;

public class DurationDataConverter implements DataConverter<Duration> {

    public DurationDataConverter() {
    }


    @Override
    public String convert(Duration data) {
        if (data == null) {
            return null;
        }

        long s = data.getSeconds();
        return String.format("%d:%02d:%02d", s / 3600, (int) ((s % 3600) / 60), (int) (s % 60));
    }

}
