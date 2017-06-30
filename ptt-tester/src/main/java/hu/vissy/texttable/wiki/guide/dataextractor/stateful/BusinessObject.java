package hu.vissy.texttable.wiki.guide.dataextractor.stateful;

public class BusinessObject {

    private int crates;
    private int position;

    public BusinessObject(int crates, int position) {
        this.crates = crates;
        this.position = position;
    }

    public int getCrates() {
        return crates;
    }

    public int getPosition() {
        return position;
    }

}
