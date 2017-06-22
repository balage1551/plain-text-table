package hu.vissy.texttable.tester;

public class TestObject {

    private long id;
    private String name;
    private double quantity;

    public TestObject(long id, String name, double quantity) {
        super();
        this.id = id;
        this.name = name;
        this.quantity = quantity;
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

}
