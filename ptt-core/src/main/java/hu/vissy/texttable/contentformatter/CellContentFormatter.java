package hu.vissy.texttable.contentformatter;

public class CellContentFormatter {


    public static class Builder {
        private EllipsisDecorator ellipsesDecorator = new EllipsisDecorator.Builder().build();
        private String nullValue = "";
        private CellAlignment cellAlignment = new LeftCellAlignment();
        private int minWidth = 0;
        private int maxWidth = Integer.MAX_VALUE;

        public Builder() {
        }

        public Builder withEllipsesDecorator(EllipsisDecorator ellipsesDecorator) {
            this.ellipsesDecorator = ellipsesDecorator;
            return this;
        }

        public Builder withNullValue(String nullValue) {
            this.nullValue = nullValue;
            return this;
        }

        public Builder withCellAlignment(CellAlignment cellAlignment) {
            this.cellAlignment = cellAlignment;
            return this;
        }

        public Builder withMinWidth(int minWidth) {
            this.minWidth = minWidth;
            return this;
        }

        public Builder withMaxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        public CellContentFormatter build() {
            return new CellContentFormatter(this);
        }


    }


    public static CellContentFormatter leftAlignedCell() {
        return new CellContentFormatter.Builder().build();
    }

    public static CellContentFormatter rightAlignedCell() {
        return new CellContentFormatter.Builder().withCellAlignment(new RightCellAlignment()).build();
    }

    public static CellContentFormatter centeredCell() {
        return new CellContentFormatter.Builder().withCellAlignment(new CenterCellAlignment()).build();
    }


    private String nullValue;
    private CellAlignment cellAlignment;
    private EllipsisDecorator ellipsesDecorator;
    private int minWidth;
    private int maxWidth;

    private CellContentFormatter(Builder builder) {
        ellipsesDecorator = builder.ellipsesDecorator;
        nullValue = builder.nullValue;
        cellAlignment = builder.cellAlignment;
        minWidth = builder.minWidth;
        maxWidth = builder.maxWidth;
    }


    public String formatCell(String value, int width) {
        if (value == null) {
            value = nullValue;
        }

        if (value.length() == width) {
            return value;
        }

        if (value.length() > width) {
            value = ellipsesDecorator.decorate(value, width);
        }

        if (value.length() < width) {
            value = cellAlignment.align(value, width);
        }

        return value;
    }



    public String getNullValue() {
        return nullValue;
    }

    public CellAlignment getCellAlignment() {
        return cellAlignment;
    }

    public EllipsisDecorator getEllipsesDecorator() {
        return ellipsesDecorator;
    }


    public int boundWidth(int width) {
        return Math.min(maxWidth, Math.max(width, minWidth));
    }

}
