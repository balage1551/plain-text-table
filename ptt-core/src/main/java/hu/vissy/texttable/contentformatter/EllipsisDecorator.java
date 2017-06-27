package hu.vissy.texttable.contentformatter;

/**
 * This decorator shortens a string and adds elipsis to it.
 *
 * @author Balage
 *
 */
public class EllipsisDecorator {

    /**
     * Defines which part of the value should be kept.
     *
     * @author Balage
     *
     */
    public enum TextSegment {
        /**
         * The start of the data is kept.
         */
        START(1),
        /**
         * The center part of the data is kept.
         */
        CENTER(2),
        /**
         * The end of the data is kept.
         */
        END(1);

        private int numberOfEllipsises;

        private TextSegment(int numberOfEllipsises) {
            this.numberOfEllipsises = numberOfEllipsises;
        }

        /**
         * @return The number of elipsises to be added.
         */
        public int getNumberOfEllipsises() {
            return numberOfEllipsises;
        }
    }


    /**
     * Builder for the {@linkplain EllipsisDecorator} class.
     *
     * @author Balage
     *
     */
    public static class Builder {
        private boolean trimToWord = false;
        private TextSegment keptPart = TextSegment.START;
        private String ellipsisSign = "...";

        /**
         * Constructor of the builder.
         */
        public Builder() {

        }

        /**
         * If this set to true, the shortening process tries to find a word
         * boundary (space). This keeps only whole words in the result. When not
         * even one word fits the result, this option will be ignored.
         *
         * @param trimToWord
         *            If true, the shortening tries to keep only whole words.
         * @return The builder instance.
         */
        public Builder withTrimToWord(boolean trimToWord) {
            this.trimToWord = trimToWord;
            return this;
        }

        /**
         * Chooses the part of the value to keep. Default value is
         * {@linkplain TextSegment#START}.
         *
         * @param keptPart
         *            The part to keep.
         * @return The builder instance.
         */
        public Builder withKeptPart(TextSegment keptPart) {
            this.keptPart = keptPart;
            return this;
        }

        /**
         * Sets the character to use as elipsis sign. Default value is three
         * dots.
         *
         * @param ellipsisSign
         *            The elipsis sign to use.
         * @return The builder instance.
         */
        public Builder withEllipsisSign(String ellipsisSign) {
            if (ellipsisSign == null || ellipsisSign.isEmpty()) {
                throw new IllegalArgumentException("Ellipses sign can't be empty or null.");
            }
            this.ellipsisSign = ellipsisSign;
            return this;
        }

        /**
         * @return The constructed {@linkplain EllipsisDecorator} instance.
         */
        public EllipsisDecorator build() {
            return new EllipsisDecorator(this);
        }
    }

    private TextSegment keptPart;
    private String ellipsisSign;
    private boolean trimToWord;


    private EllipsisDecorator(Builder builder) {
        trimToWord = builder.trimToWord;
        keptPart = builder.keptPart;
        ellipsisSign = builder.ellipsisSign;
    }

    /**
     * Shortens the value if needed and decorates the result string with the
     * elipsis string.
     *
     * <p>
     * If the <code>value</code> is shorter or equal in length than the
     * <code>width</code> value, nothing is done and the <code>value</code> is
     * returned.
     * </p>
     * <p>
     * If the <code>value</code> is longer, and the {@linkplain #isTrimToWord()}
     * is false, the result will be the exact length of <code>width</code>. If
     * the {@linkplain #isTrimToWord()} is true, the returned string may be
     * shorter. The result is <b>never</b> longer than the <code>width</code>
     * value.
     * </p>
     *
     * @param value
     *            The value to decorate.
     * @param width
     *            The maximum width allowed.
     * @return The optionally shortened and decorated value.
     */
    public String decorate(String value, int width) {
        if (value.length() <= width) {
            return value;
        }

        int usefulWidth = Math.max(0, width - keptPart.getNumberOfEllipsises() * ellipsisSign.length());
        String keptText = "";
        if (usefulWidth == 0) {
            keptText = "";
        } else {
            int i;
            switch (keptPart) {
            case START:
                i = usefulWidth;
                if (trimToWord) {
                    while (i >= 0 && value.charAt(i) != ' ') {
                        i--;
                    }
                    if (i < 0) {
                        // No words
                        i = usefulWidth;
                    }
                }
                i--;
                keptText = value.substring(0, i + 1);
                break;

            case CENTER:
                int sp = (value.length() - usefulWidth) / 2;
                i = sp + usefulWidth;
                if (trimToWord) {
                    while (i >= 0 && value.charAt(i) != ' ') {
                        i--;
                    }
                    if (i < 0) {
                        // No words
                        i = sp + usefulWidth;
                    }
                }
                i--;
                keptText = value.substring(sp, i + 1);
                break;


            case END:
                i = value.length() - usefulWidth - 1;
                if (trimToWord) {
                    while (i < value.length() && value.charAt(i) != ' ') {
                        i++;
                    }
                    if (i >= value.length()) {
                        // No words
                        i = value.length() - usefulWidth - 1;
                    }
                }
                i++;
                keptText = value.substring(i);
                break;
            }
        }

        String decorated = "";
        switch (keptPart) {
        case START:
            decorated = (keptText + ellipsisSign);
            break;
        case CENTER:
            decorated = (ellipsisSign + keptText + ellipsisSign);
            break;
        case END:
            decorated = (ellipsisSign + keptText);
            break;
        }

        return decorated.substring(0, Math.min(width, decorated.length()));
    }

    /**
     * @return The part kept after shortening the value.
     */
    public TextSegment getKeptPart() {
        return keptPart;
    }

    /**
     * @return The string used as elipsis.
     */
    public String getEllipsisSign() {
        return ellipsisSign;
    }

    /**
     * @return Whether to shorten to whole words or not.
     */
    public boolean isTrimToWord() {
        return trimToWord;
    }


}
