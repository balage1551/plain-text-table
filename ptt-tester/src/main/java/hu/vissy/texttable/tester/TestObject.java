package hu.vissy.texttable.tester;

import java.time.Duration;
import java.time.LocalDateTime;

public class TestObject {

    private long id;
    private String name;
    private double quantity;
    private LocalDateTime date;
    private Duration duration;
    private boolean valid;
    private int length;

    public TestObject(long id, String name, double quantity, LocalDateTime date, Duration duration, boolean valid, int length) {
        super();
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.date = date;
        this.duration = duration;
        this.valid = valid;
        this.length = length;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Duration getDuration() {
        return duration;
    }

    public boolean isValid() {
        return valid;
    }

    public int getLength() {
        return length;
    }



}
