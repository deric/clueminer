package org.clueminer.dataset.row;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import org.clueminer.exception.EscapeException;

/**
 *
 * @author Tomas Barton
 */
public class Tools {

    /**
     * Number of post-comma digits needed to distinguish between display of
     * numbers as integers or doubles.
     */
    private static final double IS_DISPLAY_ZERO = 1E-8;
    /**
     * @TODO load this setting from OS
     */
    private static final Locale FORMAT_LOCALE = Locale.US;
    /**
     * Used for formatting values in the {@link #formatNumber(double)} method.
     */
    private static NumberFormat INTEGER_FORMAT = NumberFormat.getIntegerInstance(FORMAT_LOCALE);
    /**
     * Used for determining the symbols used in decimal formats.
     */
    private static DecimalFormatSymbols FORMAT_SYMBOLS = new DecimalFormatSymbols(FORMAT_LOCALE);
    /**
     * Used for formatting values in the {@link #formatNumber(double)} method.
     */
    private static NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(FORMAT_LOCALE);

    /**
     * Escapes quotes, newlines, and backslashes
     *
     * @param unescaped
     * @return
     */
    public static String escape(String unescaped) {
        StringBuilder result = new StringBuilder();
        for (char c : unescaped.toCharArray()) {
            switch (c) {
                case '"':
                    result.append("\\\"");
                    break;
                case '\\':
                    result.append("\\\\");
                    break;
                case '\n':
                    result.append("\\n");
                    break;
                default:
                    result.append(c);
                    break;
            }
        }
        return result.toString();
    }

    public static String unescape(String escaped) throws EscapeException {
        StringBuilder result = new StringBuilder();
        for (int index = 0; index < escaped.length(); index++) {
            char c = escaped.charAt(index);
            switch (c) {
                case '\\':
                    if (index < escaped.length() - 1) {
                        index++;
                        char next = escaped.charAt(index);
                        switch (next) {
                            case 'n':
                                result.append('\n');
                                break;
                            case '\\':
                                result.append('\\');
                                break;
                            case '"':
                                result.append('"');
                                break;
                            default:
                                throw new EscapeException("undefined escape character: " + next);
                        }
                    } else {
                        result.append('\\');
                    }
                    break;
                default:
                    result.append(c);
                    break;
            }
        }
        return result.toString();
    }

    public static String formatIntegerIfPossible(double value, int numberOfDigits, boolean groupingCharacter) {
        if (Double.isNaN(value)) {
            return "?";
        }
        if (Double.isInfinite(value)) {
            if (value < 0) {
                return "-" + FORMAT_SYMBOLS.getInfinity();
            } else {
                return FORMAT_SYMBOLS.getInfinity();
            }
        }

        long longValue = Math.round(value);
        if (Math.abs(longValue - value) < IS_DISPLAY_ZERO) {
            INTEGER_FORMAT.setGroupingUsed(groupingCharacter);
            return INTEGER_FORMAT.format(longValue);
        } else {
            return formatNumber(value, numberOfDigits, groupingCharacter);
        }
    }

    /**
     * Returns a formatted string of the given number if the given number of
     * digits is smaller than 0
     */
    public static String formatNumber(double value, int numberOfDigits, boolean groupingCharacters) {
        if (Double.isNaN(value)) {
            return "?";
        }
        int numberDigits = numberOfDigits;
        if (numberDigits < 0) {
            numberDigits = 3;

        }
        NUMBER_FORMAT.setMaximumFractionDigits(numberDigits);
        NUMBER_FORMAT.setMinimumFractionDigits(numberDigits);
        NUMBER_FORMAT.setGroupingUsed(groupingCharacters);
        return NUMBER_FORMAT.format(value);
    }
}
