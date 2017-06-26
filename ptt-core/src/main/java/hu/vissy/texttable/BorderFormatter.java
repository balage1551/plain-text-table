package hu.vissy.texttable;

import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

public class BorderFormatter {


    public static class LineSpec {
        private char leftEdge;
        private char internal;
        private char body;
        private char rightEdge;
        private char padding;
        private boolean hidden;

        public LineSpec(char leftEdge, char internal, char padding, char body, char rightEdge) {
            super();
            this.leftEdge = leftEdge;
            this.internal = internal;
            this.body = body;
            this.rightEdge = rightEdge;
            this.padding = padding;
        }

        public LineSpec(char leftEdge, char internal, char body, char rightEdge) {
            this(leftEdge, internal, body, body, rightEdge);
        }

        public LineSpec(char edge, char internal, char body) {
            this(edge, internal, body, body, edge);
        }

        public LineSpec(char edge, char body) {
            this(edge, edge, body, body, edge);
        }

        LineSpec() {
            hidden = true;
        }

        public char getLeftEdge() {
            return leftEdge;
        }

        public char getInternal() {
            return internal;
        }

        public char getBody() {
            return body;
        }

        public char getRightEdge() {
            return rightEdge;
        }

        public char getPadding() {
            return padding;
        }

        public boolean isHidden() {
            return hidden;
        }
    }

    public static final LineSpec HIDDEN = new LineSpec();

    public static class RowSpec extends LineSpec {

        private static final char UNSUPPORTED = '\0';

        public RowSpec(char leftEdge, char internal, char padding, char rightEdge) {
            super(leftEdge, internal, padding, UNSUPPORTED, rightEdge);
        }

        public RowSpec(char edge, char internal, char padding) {
            this(edge, internal, padding, edge);
        }

        public RowSpec(char edge, char padding) {
            this(edge, edge, padding, edge);
        }

        public RowSpec(char edge) {
            this(edge, edge, ' ', edge);
        }
    }


    public enum LineType {
        TOP_EDGE, BOTTOM_EDGE, HEADING_LINE, HEADER_LINE, INTERNAL_LINE, SEPARATOR_LINE, AGGREGATE_LINE;
    }

    public enum RowType {
        HEADING, HEADER, DATA, AGGREGATE;
    }


    public enum DefaultFormatters {
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

    public static class Builder {
        EnumMap<LineType, LineSpec> lineSpecifications = new EnumMap<>(LineType.class);
        EnumMap<RowType, RowSpec> rowSpecifications = new EnumMap<>(RowType.class);
        private int leftPaddingWidth = 1;
        private int rightPaddingWidth = 1;
        private boolean drawVerticalEdge = true;
        private boolean drawVerticalSeparator = true;

        public Builder(DefaultFormatters def) {
            def.populate(this);
        }


        public Builder withLeftPaddingWidth(int leftPaddingWidth) {
            this.leftPaddingWidth = leftPaddingWidth;
            return this;
        }

        public Builder withRightPaddingWidth(int rightPaddingWidth) {
            this.rightPaddingWidth = rightPaddingWidth;
            return this;
        }

        public Builder withPaddingWidth(int paddingWidth) {
            withLeftPaddingWidth(paddingWidth);
            withRightPaddingWidth(paddingWidth);
            return this;
        }

        public Builder withDrawVerticalEdge(boolean drawVerticalEdge) {
            this.drawVerticalEdge = drawVerticalEdge;
            return this;
        }

        public Builder withDrawVerticalSeparator(boolean drawVerticalSeparator) {
            this.drawVerticalSeparator = drawVerticalSeparator;
            return this;
        }

        public Builder withLineSpecifications(EnumMap<LineType, LineSpec> lineSpecifications) {
            this.lineSpecifications = lineSpecifications;
            return this;
        }

        public Builder withRowSpecifications(EnumMap<RowType, RowSpec> rowSpecifications) {
            this.rowSpecifications = rowSpecifications;
            return this;
        }


        public Builder withLine(LineSpec spec, LineType lineType) {
            lineSpecifications.put(lineType, spec);
            return this;
        }

        public Builder withLine(LineSpec spec, LineType... lineTypes) {
            for (LineType t : lineTypes) {
                lineSpecifications.put(t, spec);
            }
            return this;
        }

        public Builder withUniformLine(LineSpec spec) {
            for (LineType t : LineType.values()) {
                lineSpecifications.put(t, spec);
            }
            return this;
        }

        public Builder withRow(RowSpec spec, RowType rowType) {
            rowSpecifications.put(rowType, spec);
            return this;
        }

        public Builder withRow(RowSpec spec, RowType... rowTypes) {
            for (RowType t : rowTypes) {
                rowSpecifications.put(t, spec);
            }
            return this;
        }

        public Builder withUniformRow(RowSpec spec) {
            for (RowType t : RowType.values()) {
                rowSpecifications.put(t, spec);
            }
            return this;
        }


        public BorderFormatter build() {
            return new BorderFormatter(this);
        }
    }



    public static final DefaultFormatters DEFAULT_TYPE = DefaultFormatters.UNICODE_LINEDRAW;

    EnumMap<LineType, LineSpec> lineSpecifications = new EnumMap<>(LineType.class);
    EnumMap<RowType, RowSpec> rowSpecifications = new EnumMap<>(RowType.class);

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


    public String repeate(char c, int w) {
        return new String(new byte[w]).replace('\0', c);
    }

    public int calculateOneColumnWidth(List<Integer> widths) {
        return widths.stream().mapToInt(i -> i).sum() +
                (widths.size() - 1) * (leftPaddingWidth + rightPaddingWidth) +
                (drawVerticalSeparator ? widths.size() - 1 : 0);
    }

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

