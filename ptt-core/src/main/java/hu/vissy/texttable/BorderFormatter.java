package hu.vissy.texttable;

import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Border formatter configuration
 *
 * <p>
 * The border formatter responsible for drawing the table decoration around the
 * data: the vertical and horizontal lines, the padding, the outer borders
 * (called edge).
 * </p>
 *
 * <p>
 * The {@linkplain DefaultFormatters} enum contains some default configurations
 * to start with. The builder is initialized by one of these presets and the
 * user can further configure, or fine tune the look and feel of the table.
 * </p>
 * <p>
 * Each table contains rows of data and horizontal lines between them. The rows
 * can be data, heading, header or aggregate rows. Each line and row is split
 * into different parts:
 * </p>
 *
 * <pre>
 * | Column data | Other column |
 *                              ^-- Right edge
 *               ^----------------- Internal separator
 * ^------------------------------- Left edge
 *
 * | Column data | Other column |
 *  ^-----------^-^------------^--- Padding
 *   ^^^^^^^^^^^---^^^^^^^^^^^^---- Body (only for lines)
 * </pre>
 *
 * <p>
 * Each of these part can be independently configured.
 * </p>
 *
 * <p>
 * This class is imutable and follows the builder pattern.
 * </p>
 *
 * @author Balage
 *
 */
public class BorderFormatter {


    /**
     * The configuration of the line.
     * <p>
     * Each line has different parts (left edge, internal separator, right edge,
     * padding zone and body) which can be individually configured.
     * </p>
     *
     * <p>
     * This class is imutable, so it can be safely used in multiple places.
     * </p>
     *
     * @author Balage
     *
     */
    public static class LineSpec {
        private char leftEdge;
        private char internal;
        private char body;
        private char rightEdge;
        private char padding;
        private boolean hidden;

        /**
         * This is the most detailed constructor. Each part types can be
         * individually configured.
         *
         * @param leftEdge
         *            The character used for left edge.
         * @param internal
         *            The character used as the internal separator between
         *            columns.
         * @param padding
         *            The character used where padding is taking place in normal
         *            rows.
         * @param body
         *            The character the column internal body part is filled
         *            with.
         * @param rightEdge
         *            The character used for right edge.
         */
        public LineSpec(char leftEdge, char internal, char padding, char body, char rightEdge) {
            super();
            this.leftEdge = leftEdge;
            this.internal = internal;
            this.body = body;
            this.rightEdge = rightEdge;
            this.padding = padding;
        }

        /**
         * This constructor uses the same character for padding and body.
         *
         * @param leftEdge
         *            The character used for left edge.
         * @param internal
         *            The character used as the internal separator between
         *            columns.
         * @param body
         *            The character the column internal body part and the
         *            padding are filled with.
         * @param rightEdge
         *            The character used for right edge.
         */
        public LineSpec(char leftEdge, char internal, char body, char rightEdge) {
            this(leftEdge, internal, body, body, rightEdge);
        }

        /**
         * This constructor uses the same character for padding and body and
         * assumes the edge characters on the two sides are the same.
         *
         * @param edge
         *            The character used for left and right edge.
         * @param internal
         *            The character used as the internal separator between
         *            columns.
         * @param body
         *            The character the column internal body part and the
         *            padding are filled with.
         */
        public LineSpec(char edge, char internal, char body) {
            this(edge, internal, body, body, edge);
        }

        /**
         * This constructor uses the same character for padding and body and
         * assumes the internal separator and edge characters on the two sides
         * are the same.
         *
         * @param vertical
         *            The character used for left and right edge, and for the
         *            internal separators.
         * @param body
         *            The character the column internal body part and the
         *            padding are filled with.
         */
        public LineSpec(char vertical, char body) {
            this(vertical, vertical, body, body, vertical);
        }

        LineSpec() {
            hidden = true;
        }

        /**
         * @return The left edge character.
         */
        public char getLeftEdge() {
            return leftEdge;
        }

        /**
         * @return The internal separator character.
         */
        public char getInternal() {
            return internal;
        }

        /**
         * @return The body filler character.
         */
        public char getBody() {
            return body;
        }

        /**
         * @return The padding filler character.
         */
        public char getPadding() {
            return padding;
        }

        /**
         * @return The right edge character.
         */
        public char getRightEdge() {
            return rightEdge;
        }


        boolean isHidden() {
            return hidden;
        }
    }

    /**
     * Special predefined line specification for marking lines which shouldn't
     * print.
     */
    public static final LineSpec HIDDEN = new LineSpec();

    /**
     * The configuration of the data row.
     * <p>
     * Each row has different parts (left edge, internal separator, right edge,
     * padding zone) which can be individually configured.
     * </p>
     *
     * <p>
     * This class is imutable, so it can be safely used in multiple places.
     * </p>
     *
     * @author Balage
     *
     */
    public static class RowSpec extends LineSpec {

        private static final char UNSUPPORTED = '\0';

        /**
         * This is the most detailed constructor. Each part types can be
         * individually configured.
         *
         * @param leftEdge
         *            The character used for left edge.
         * @param internal
         *            The character used as the internal separator between
         *            columns.
         * @param padding
         *            The character used where padding is taking place in normal
         *            rows.
         * @param rightEdge
         *            The character used for right edge.
         */
        public RowSpec(char leftEdge, char internal, char padding, char rightEdge) {
            super(leftEdge, internal, padding, UNSUPPORTED, rightEdge);
        }

        /**
         * This constructor uses the same character for the edge characters on
         * the two sides.
         *
         * @param edge
         *            The character used for left and right edge.
         * @param internal
         *            The character used as the internal separator between
         *            columns.
         * @param padding
         *            The character used where padding is taking place in normal
         *            rows.
         */
        public RowSpec(char edge, char internal, char padding) {
            this(edge, internal, padding, edge);
        }

        /**
         * This constructor uses the same character for the edge characters on
         * the two sides and for the internal separator.
         *
         * @param edge
         *            The character used for left and right edge, and for the
         *            internal separator.
         * @param padding
         *            The character used where padding is taking place in normal
         *            rows.
         */
        public RowSpec(char edge, char padding) {
            this(edge, edge, padding, edge);
        }

        /**
         * This constructor uses the same character for the edge characters on
         * the two sides and space for padding.
         *
         * @param edge
         *            The character used for left and right edge, and for the
         *            internal separator.
         */
        public RowSpec(char edge) {
            this(edge, edge, ' ', edge);
        }
    }


    /**
     * The types of lines.
     *
     * @author Balage
     *
     */
    public enum LineType {
        /**
         * The line drawn at the top of the table.
         */
        TOP_EDGE,
        /**
         * The heading is the "title bar" of the table. This line is drawn below
         * the heading.
         */
        HEADING_LINE,
        /**
         * The header line contains the names of the columns and this is drawn
         * below the header.
         */
        HEADER_LINE,
        /**
         * The internal line is drawn between the data rows.
         */
        INTERNAL_LINE,
        /**
         * The separator line is drawn whenever a null value is received from
         * input data.
         */
        SEPARATOR_LINE,
        /**
         * The aggragator row may be printed at the bottom of the table and this
         * line is above it.
         */
        AGGREGATE_LINE,
        /**
         * The line drawn at the botton of the table.
         */
        BOTTOM_EDGE,
        ;
    }

    public enum RowType {
        /**
         * The heading ("title bar") row of the table.
         */
        HEADING,
        /**
         * The row of the column names.
         */
        HEADER,
        /**
         * The data row.
         */
        DATA,
        /**
         * The row for the aggregated values at the bottom of the table.
         */
        AGGREGATE;
    }


    /**
     * These are the predefined presets for table formatting.
     *
     * <p>
     * These presets are used as the fundation of your own configurations.
     * </p>
     *
     * @author Balage
     *
     */
    public enum DefaultFormatters {
        /**
         * A table definition based on the Unicode line draw characters.
         * (https://en.wikipedia.org/wiki/Box-drawing_character)
         *
         * <pre>
         * ╔═══════════════════════╗
         * ║ Heading               ║
         * ╠═══════════╤═══════════╣
         * ║ Column #1 │ Column #2 ║
         * ╠═══════════╪═══════════╣
         * ║ apple     │      7,40 ║
         * ╟───────────┼───────────╢
         * ║ banana    │      1,70 ║
         * ╠═══════════╪═══════════╣
         * ║ date      │      3,70 ║
         * ╠═══════════╪═══════════╣
         * ║ TOTAL     │     12,80 ║
         * ╚═══════════╧═══════════╝
         * </pre>
         *
         * @author Balage
         *
         */
        UNICODE_LINEDRAW {
            @Override
            void populate(BorderFormatter.Builder bfb) {
                bfb.lineSpecifications.put(LineType.TOP_EDGE, new LineSpec('╔', '╤', '═', '╗'));
                bfb.lineSpecifications.put(LineType.HEADING_LINE, new LineSpec('╠', '╤', '═', '╣'));
                bfb.lineSpecifications.put(LineType.HEADER_LINE, new LineSpec('╠', '╪', '═', '╣'));
                bfb.lineSpecifications.put(LineType.SEPARATOR_LINE, new LineSpec('╠', '╪', '═', '╣'));
                bfb.lineSpecifications.put(LineType.INTERNAL_LINE, new LineSpec('╟', '┼', '─', '╢'));
                bfb.lineSpecifications.put(LineType.AGGREGATE_LINE, new LineSpec('╠', '╪', '═', '╣'));
                bfb.lineSpecifications.put(LineType.BOTTOM_EDGE, new LineSpec('╚', '╧', '═', '╝'));

                bfb.rowSpecifications.put(RowType.HEADING, new RowSpec('║', '│', ' '));
                bfb.rowSpecifications.put(RowType.HEADER, new RowSpec('║', '│', ' '));
                bfb.rowSpecifications.put(RowType.DATA, new RowSpec('║', '│', ' '));
                bfb.rowSpecifications.put(RowType.AGGREGATE, new RowSpec('║', '│', ' '));
            }
        },
        /**
         * A basic table definition with ASCII characters, without double lines.
         *
         * <pre>
         * +-----------------------+
         * | Heading               |
         * +-----------+-----------+
         * | Column #1 | Column #2 |
         * +-----------+-----------+
         * | apple     |      7,40 |
         * +-----------+-----------+
         * | banana    |      1,70 |
         * +-----------+-----------+
         * | date      |      3,70 |
         * +-----------+-----------+
         * | TOTAL     |     12,80 |
         * +-----------+-----------+
         * </pre>
         *
         * @author Balage
         *
         */
        ASCII_LINEDRAW {
            @Override
            void populate(BorderFormatter.Builder bfb) {
                LineSpec simpleLine = new LineSpec('+', '-');
                bfb.lineSpecifications.put(LineType.TOP_EDGE, simpleLine);
                bfb.lineSpecifications.put(LineType.HEADING_LINE, simpleLine);
                bfb.lineSpecifications.put(LineType.HEADER_LINE, simpleLine);
                bfb.lineSpecifications.put(LineType.SEPARATOR_LINE, simpleLine);
                bfb.lineSpecifications.put(LineType.INTERNAL_LINE, simpleLine);
                bfb.lineSpecifications.put(LineType.AGGREGATE_LINE, simpleLine);
                bfb.lineSpecifications.put(LineType.BOTTOM_EDGE, simpleLine);

                bfb.rowSpecifications.put(RowType.HEADING, new RowSpec('|'));
                bfb.rowSpecifications.put(RowType.HEADER, new RowSpec('|'));
                bfb.rowSpecifications.put(RowType.DATA, new RowSpec('|'));
                bfb.rowSpecifications.put(RowType.AGGREGATE, new RowSpec('|'));
            }
        },

        /**
         * A basic table definition with ASCII characters, with double lines.
         *
         * <pre>
         * +=======================+
         * | Heading               |
         * +===========+===========+
         * | Column #1 | Column #2 |
         * +===========+===========+
         * | apple     |      7,40 |
         * +-----------+-----------+
         * | banana    |      1,70 |
         * +===========+===========+
         * | date      |      3,70 |
         * +===========+===========+
         * | TOTAL     |     12,80 |
         * +===========+===========+
         * </pre>
         *
         * @author Balage
         *
         */
        ASCII_LINEDRAW_DOUBLE {
            @Override
            void populate(BorderFormatter.Builder bfb) {
                LineSpec simpleLine = new LineSpec('+', '-');
                LineSpec doubleLine = new LineSpec('+', '=');
                bfb.lineSpecifications.put(LineType.TOP_EDGE, doubleLine);
                bfb.lineSpecifications.put(LineType.HEADING_LINE, doubleLine);
                bfb.lineSpecifications.put(LineType.HEADER_LINE, doubleLine);
                bfb.lineSpecifications.put(LineType.SEPARATOR_LINE, doubleLine);
                bfb.lineSpecifications.put(LineType.INTERNAL_LINE, simpleLine);
                bfb.lineSpecifications.put(LineType.AGGREGATE_LINE, doubleLine);
                bfb.lineSpecifications.put(LineType.BOTTOM_EDGE, doubleLine);

                bfb.rowSpecifications.put(RowType.HEADING, new RowSpec('|'));
                bfb.rowSpecifications.put(RowType.HEADER, new RowSpec('|'));
                bfb.rowSpecifications.put(RowType.DATA, new RowSpec('|'));
                bfb.rowSpecifications.put(RowType.AGGREGATE, new RowSpec('|'));
            }
        },

        /**
         * An elegant table definition with ASCII characters, without vertical
         * lines.
         *
         * <pre>
         * -----------------------
         *  Heading
         * -----------------------
         *  Column #1   Column #2
         * ----------- -----------
         *  apple            7,40
         *  banana           1,70
         * ----------- -----------
         *  date             3,70
         * ----------- -----------
         *  TOTAL           12,80
         * -----------------------
         * </pre>
         *
         * @author Balage
         *
         */
        NO_VERTICAL {
            @Override
            void populate(BorderFormatter.Builder bfb) {
                LineSpec simpleLine = new LineSpec('-', ' ', '-', '-');
                LineSpec fullLine = new LineSpec('-', '-');
                bfb.lineSpecifications.put(LineType.TOP_EDGE, fullLine);
                bfb.lineSpecifications.put(LineType.HEADING_LINE, fullLine);
                bfb.lineSpecifications.put(LineType.HEADER_LINE, simpleLine);
                bfb.lineSpecifications.put(LineType.SEPARATOR_LINE, simpleLine);
                bfb.lineSpecifications.put(LineType.INTERNAL_LINE, HIDDEN);
                bfb.lineSpecifications.put(LineType.AGGREGATE_LINE, simpleLine);
                bfb.lineSpecifications.put(LineType.BOTTOM_EDGE, fullLine);

                bfb.rowSpecifications.put(RowType.HEADING, new RowSpec(' '));
                bfb.rowSpecifications.put(RowType.HEADER, new RowSpec(' '));
                bfb.rowSpecifications.put(RowType.DATA, new RowSpec(' '));
                bfb.rowSpecifications.put(RowType.AGGREGATE, new RowSpec(' '));

                bfb.withDrawVerticalEdge(false);
            }
        };
        ;

        abstract void populate(BorderFormatter.Builder bfb);
    }


    /**
     * The builder class for {@link BorderFormatter}.
     *
     * @author Balage
     *
     */
    public static class Builder {
        private EnumMap<LineType, LineSpec> lineSpecifications = new EnumMap<>(LineType.class);
        private EnumMap<RowType, RowSpec> rowSpecifications = new EnumMap<>(RowType.class);
        private int leftPaddingWidth = 1;
        private int rightPaddingWidth = 1;
        private boolean drawVerticalEdge = true;
        private boolean drawVerticalSeparator = true;

        /**
         * Initilaize the builder with one of the presets.
         *
         * @param def
         *            The preset.
         */
        public Builder(DefaultFormatters def) {
            def.populate(this);
        }


        /**
         * Sets the padding added before the data in any cell.
         *
         * @param leftPaddingWidth
         *            The width of the left padding.
         * @return The builder instance.
         */
        public Builder withLeftPaddingWidth(int leftPaddingWidth) {
            this.leftPaddingWidth = leftPaddingWidth;
            return this;
        }

        /**
         * Sets the padding added behind the data in any cell.
         *
         * @param rightPaddingWidth
         *            The width of the right padding.
         * @return The builder instance.
         */
        public Builder withRightPaddingWidth(int rightPaddingWidth) {
            this.rightPaddingWidth = rightPaddingWidth;
            return this;
        }

        /**
         * Sets both (left and right) paddings.
         *
         * @param paddingWidth
         *            The width of both the left and the right padding.
         * @return The builder instance.
         */
        public Builder withPaddingWidth(int paddingWidth) {
            withLeftPaddingWidth(paddingWidth);
            withRightPaddingWidth(paddingWidth);
            return this;
        }

        /**
         * Sets whether vertical edge lines are drawn on the two sides of the
         * table.
         *
         * @param drawVerticalEdge
         *            If true, a vertical line is drawn on the left and right
         *            side of the table. If false, this lines are omitted.
         * @return The builder instance.
         */
        public Builder withDrawVerticalEdge(boolean drawVerticalEdge) {
            this.drawVerticalEdge = drawVerticalEdge;
            return this;
        }

        /**
         * Sets whether vertical separator lines are drawn between columns.
         *
         * @param drawVerticalSeparator
         *            If true, a vertical line is drawn between each columns. If
         *            false, this lines are omitted.
         * @return The builder instance.
         */
        public Builder withDrawVerticalSeparator(boolean drawVerticalSeparator) {
            this.drawVerticalSeparator = drawVerticalSeparator;
            return this;
        }


        /**
         * Sets the specification of a given line type.
         *
         * @param spec
         *            The specification to set to the line.
         * @param lineType
         *            The line type to set the specification to.
         * @return The builder instance.
         */
        public Builder withLine(LineSpec spec, LineType lineType) {
            lineSpecifications.put(lineType, spec);
            return this;
        }


        /**
         * Sets the same specification of several line types.
         *
         * @param spec
         *            The specification to set to the line.
         * @param lineTypes
         *            The list of line types to set the specification to.
         * @return The builder instance.
         */
        public Builder withLine(LineSpec spec, LineType... lineTypes) {
            for (LineType t : lineTypes) {
                lineSpecifications.put(t, spec);
            }
            return this;
        }


        /**
         * Sets the same specification for all line types.
         *
         * @param spec
         *            The specification to set to all the lines.
         * @return The builder instance.
         */
        public Builder withUniformLine(LineSpec spec) {
            for (LineType t : LineType.values()) {
                lineSpecifications.put(t, spec);
            }
            return this;
        }

        /**
         * Sets the specification of a given row type.
         *
         * @param spec
         *            The specification to set to the row.
         * @param rowType
         *            The row type to set the specification to.
         * @return The builder instance.
         */
        public Builder withRow(RowSpec spec, RowType rowType) {
            rowSpecifications.put(rowType, spec);
            return this;
        }


        /**
         * Sets the same specification of several row types.
         *
         * @param spec
         *            The specification to set to the row.
         * @param rowTypes
         *            The list of row types to set the specification to.
         * @return The builder instance.
         */
        public Builder withRow(RowSpec spec, RowType... rowTypes) {
            for (RowType t : rowTypes) {
                rowSpecifications.put(t, spec);
            }
            return this;
        }


        /**
         * Sets the same specification for all row types.
         *
         * @param spec
         *            The specification to set to all the rows.
         * @return The builder instance.
         */
        public Builder withUniformRow(RowSpec spec) {
            for (RowType t : RowType.values()) {
                rowSpecifications.put(t, spec);
            }
            return this;
        }



        /**
         * Copies the specification from a line type to another.
         *
         * @param fromType
         *            The line type to copy the specification from.
         * @param toType
         *            The line type to copy the specification to.
         * @return The builder instance.
         */
        public Builder copyFrom(LineType fromType, LineType toType) {
            return withLine(lineSpecifications.get(fromType), toType);
        }


        /**
         * Copies the specification from a line type to several other.
         *
         * @param fromType
         *            The line type to copy the specification from.
         * @param toTypes
         *            The line types to copy the specification to.
         * @return The builder instance.
         */
        public Builder copyFrom(LineType fromType, LineType... toTypes) {
            for (LineType t : toTypes) {
                withLine(lineSpecifications.get(fromType), t);
            }
            return this;
        }

        /**
         * Copies the specification from a row type to several other.
         *
         * @param fromType
         *            The row type to copy the specification from.
         * @param toType
         *            The row types to copy the specification to.
         * @return The builder instance.
         */
        public Builder copyFrom(RowType fromType, RowType toType) {
            return withRow(rowSpecifications.get(fromType), toType);
        }


        /**
         * Copies the specification from a row type to several other.
         *
         * @param fromType
         *            The row type to copy the specification from.
         * @param toTypes
         *            The row types to copy the specification to.
         * @return The builder instance.
         */
        public Builder copyFrom(RowType fromType, RowType... toTypes) {
            for (RowType t : toTypes) {
                withRow(rowSpecifications.get(fromType), t);
            }
            return this;
        }


        /**
         * @return The constructed border formatter instance.
         */
        public BorderFormatter build() {
            return new BorderFormatter(this);
        }
    }



    public static final DefaultFormatters DEFAULT_TYPE = DefaultFormatters.ASCII_LINEDRAW_DOUBLE;

    private EnumMap<LineType, LineSpec> lineSpecifications = new EnumMap<>(LineType.class);
    private EnumMap<RowType, RowSpec> rowSpecifications = new EnumMap<>(RowType.class);

    private int leftPaddingWidth = 1;
    private int rightPaddingWidth = 1;
    private boolean drawVerticalEdge = true;
    private boolean drawVerticalSeparator = true;

    private BorderFormatter(Builder builder) {
        lineSpecifications = new EnumMap<>(builder.lineSpecifications);
        rowSpecifications = new EnumMap<>(builder.rowSpecifications);
        leftPaddingWidth = builder.leftPaddingWidth;
        rightPaddingWidth = builder.rightPaddingWidth;
        drawVerticalEdge = builder.drawVerticalEdge;
        drawVerticalSeparator = builder.drawVerticalSeparator;
    }


    private String repeate(char c, int w) {
        return new String(new byte[w]).replace('\0', c);
    }


    /**
     * Calculates the width of the heading (which spans through all the
     * columns).
     *
     * @param widths
     *            The column widths.
     * @return The body width of the heading column (excluding the padding of
     *         the heading and the edges).
     */
    public int calculateOneColumnWidth(List<Integer> widths) {
        return widths.stream().mapToInt(i -> i).sum() +
                (widths.size() - 1) * (leftPaddingWidth + rightPaddingWidth) +
                (drawVerticalSeparator ? widths.size() - 1 : 0);
    }

    /**
     * Draws a line.
     *
     * @param widths
     *            The column widths.
     * @param lineType
     *            The type of the line.
     * @param skipInternal
     *            If true, the internal junction points for column separators
     *            are omitted.
     * @return The generated line (including closing new line) or empty string
     *         if the line is hidden.
     */
    public String drawLine(List<Integer> widths, LineType lineType, boolean skipInternal) {
        LineSpec spec = lineSpecifications.get(lineType);
        if (spec.isHidden()) {
            return "";
        }

        return widths.stream().map(w -> {
            String l = repeate(spec.getPadding(), leftPaddingWidth)
                    + repeate(spec.getBody(), w)
                    + repeate(spec.getPadding(), rightPaddingWidth);
            return l;
        }).collect(Collectors.joining(drawVerticalSeparator ? "" + (skipInternal ? spec.getBody() : spec.getInternal()) : "",
                (drawVerticalEdge ? "" + spec.getLeftEdge() : ""),
                (drawVerticalEdge ? "" + spec.getRightEdge() : ""))) + "\n";
    }

    /**
     * Draws the a row.
     *
     * @param data
     *            The date to populate the columns with. <i>The width of each
     *            the column data is assumed to be equal to the width of the
     *            corresponding column.</i>
     * @param rowType
     *            The type of the row.
     * @return The formatted row (including closing new line).
     */
    public String drawData(List<String> data, RowType rowType) {
        RowSpec spec = rowSpecifications.get(rowType);

        return data.stream().map(w -> {
            String l = repeate(spec.getPadding(), leftPaddingWidth)
                    + w
                    + repeate(spec.getPadding(), rightPaddingWidth);
            return l;
        }).collect(Collectors.joining(drawVerticalSeparator ? "" + spec.getInternal() : "",
                (drawVerticalEdge ? "" + spec.getLeftEdge() : ""),
                (drawVerticalEdge ? "" + spec.getRightEdge() : ""))) + "\n";

    }


}

