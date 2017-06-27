package hu.vissy.texttable.contentformatter;

import hu.vissy.texttable.BorderFormatter;

/**
 * The cell content formatter responsible to convert the string value of a cell
 * to the exact width of the column. It may align and pad the value or shorten
 * it.
 *
 * <p>
 * This class is immutable and follows the builder pattern.
 * </p>
 *
 * @author Balage
 *
 */
public class CellContentFormatter {


    /**
     * The builder for {@linkplain CellContentFormatter}.
     *
     * @author Balage
     *
     */
    public static class Builder {
        private EllipsisDecorator ellipsesDecorator = new EllipsisDecorator.Builder().build();
        private String nullValue = "";
        private CellAlignment cellAlignment = new LeftCellAlignment();
        private int minWidth = 0;
        private int maxWidth = Integer.MAX_VALUE;

        /**
         * Constructor.
         */
        public Builder() {
        }

        /**
         * Sets the elipsis decorator instance. This decorator will shorten and
         * decorate the cell when the value is longer than the column width.
         *
         * @param ellipsesDecorator
         *            The elipsis decorator instance.
         * @return The builder instance.
         */
        public Builder withEllipsesDecorator(EllipsisDecorator ellipsesDecorator) {
            this.ellipsesDecorator = ellipsesDecorator;
            return this;
        }

        /**
         * The value used for null cell values. Default is empty string.
         *
         * @param nullValue
         *            The value used when cell value is null.
         * @return The builder instance.
         */
        public Builder withNullValue(String nullValue) {
            this.nullValue = nullValue;
            return this;
        }

        /**
         * The cell alignment used when internal padding is required because the
         * value is shorter than the column width. Default is
         * {@linkplain LeftCellAlignment}.
         *
         * @param cellAlignment
         *            The cell alignment value.
         * @return The builder instance.
         */
        public Builder withCellAlignment(CellAlignment cellAlignment) {
            this.cellAlignment = cellAlignment;
            return this;
        }

        /**
         * Minimal cell width constraint. This will lower bound the cell width
         * even when the data within would require less space.
         * <p>
         * The width doesn't contain the padding width of the cell specified in
         * {@linkplain BorderFormatter}.
         * </p>
         *
         * @param minWidth
         *            The minimal width of the cell.
         * @return The builder instance.
         */
        public Builder withMinWidth(int minWidth) {
            this.minWidth = minWidth;
            return this;
        }

        /**
         * Maximal cell width constraint. This will upper bound the cell width
         * even when the data within would require more space.
         * <p>
         * The width doesn't contain the padding width of the cell specified in
         * {@linkplain BorderFormatter}.
         * </p>
         *
         * @param maxWidth
         *            The maximal width of the cell.
         * @return The builder instance.
         */
        public Builder withMaxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        /**
         * @return The constructed {@linkplain CellContentFormatter} instance.
         */
        public CellContentFormatter build() {
            return new CellContentFormatter(this);
        }


    }


    /**
     * @return A convenient method for left aligned cells with default
     *         behaviour.
     */
    public static CellContentFormatter leftAlignedCell() {
        return new CellContentFormatter.Builder().build();
    }

    /**
     * @return A convenient method for right aligned cells with default
     *         behaviour.
     */
    public static CellContentFormatter rightAlignedCell() {
        return new CellContentFormatter.Builder().withCellAlignment(new RightCellAlignment()).build();
    }

    /**
     * @return A convenient method for centered cells with default behaviour.
     */
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


    /**
     * Formats a value to the specified width.
     * <p>
     * It first checks if the value is null and replaces it with the value
     * returned by {@linkplain #getNullValue()}. If the value is longer than the
     * given <code>width</code>, the elipsis decorator
     * ({@linkplain #getEllipsesDecorator()}) is called and the value is updated
     * by it. If the value is still shorter than the <code>width</code>, the
     * value is aligned and padded by the implementation from
     * {@linkplain #getCellAlignment()}. The result value has always the width
     * equal to the <code>width</code> value.
     * </p>
     *
     * @param value
     *            The value to convert.
     * @param width
     *            The required width to convert the value to.
     * @return The converted, decorated value which will always have the given
     *         width.
     */
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



    /**
     * @return The string used for null values.
     */
    public String getNullValue() {
        return nullValue;
    }

    /**
     * @return The cell alignment and padding implementation.
     */
    public CellAlignment getCellAlignment() {
        return cellAlignment;
    }

    /**
     * @return The value shortening and elipsis decorator implementation.
     */
    public EllipsisDecorator getEllipsesDecorator() {
        return ellipsesDecorator;
    }



    /**
     * @return The minimal column width allowed.
     */
    public int getMinWidth() {
        return minWidth;
    }

    /**
     * @return The maximal column width allowed.
     */
    public int getMaxWidth() {
        return maxWidth;
    }

    /**
     * Checks the with and bounds it to be within the
     * {@linkplain #getMinWidth()} and {@linkplain #getMaxWidth()}.
     * 
     * @param width
     *            The width to check.
     * @return The width which isn't lower than the minimal and isn't higher
     *         than the maximal width.
     */
    public int boundWidth(int width) {
        return Math.min(maxWidth, Math.max(width, minWidth));
    }

}
