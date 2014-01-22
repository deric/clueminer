package org.clueminer.utils;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;

/**
 *
 * @author Tomas Barton
 */
public class Dump {

    public static void array(int[] a, String name) {
        System.out.println(stringArray(a, name));
    }

    public static String stringArray(int[] a, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" [ ");
        if (a != null) {
            for (int i = 0; i < a.length; i++) {
                sb.append(a[i]).append(" ");
            }
        }

        sb.append(" ]");
        return sb.toString();
    }

    public static void array(float[] a, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" [ ");
        if (a != null) {
            for (int i = 0; i < a.length; i++) {
                sb.append(a[i]).append(" ");
            }
        }
        sb.append(" ]");
        System.out.println(sb.toString());
    }

    public static void array(double[] a, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" [ ");
        if (a != null) {
            for (int i = 0; i < a.length; i++) {
                sb.append(a[i]).append(" ");
            }
        }
        sb.append(" ]");
        System.out.println(sb.toString());
    }

    public static void array(String[] a, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" [ ");
        if (a != null) {
            for (int i = 0; i < a.length; i++) {
                sb.append(a[i]).append(" ");
            }
        }
        sb.append(" ]");
        System.out.println(sb.toString());
    }

    /**
     * Method printVect for printing a vector <br> Based on ER Harold, "Java
     * I/O", O'Reilly, around p. 473.
     *
     * @param m input vector of length m.length
     * @param d display precision, number of decimal places
     * @param w display precision, total width of floating value
     */
    public static void vector(double[] m, int d, int w) {
        // Some definitions for handling output formating
        NumberFormat myFormat = NumberFormat.getNumberInstance();
        FieldPosition fp = new FieldPosition(NumberFormat.INTEGER_FIELD);
        myFormat.setMaximumIntegerDigits(d);
        myFormat.setMaximumFractionDigits(d);
        myFormat.setMinimumFractionDigits(d);
        int len = m.length;
        for (int i = 0; i < len; i++) {
            // Following would be nice, but doesn't format adequately
            //                  System.out.print(m[i] + "  ");
            String valString = myFormat.format(
                    m[i], new StringBuffer(), fp).toString();
            valString = getSpaces(w - fp.getEndIndex()) + valString;
            System.out.print(valString);
        }
        // Start a new line at the row end
        System.out.println();
        // Leave a gap after the entire vector
        System.out.println();
    } // printVect

    // DecimalFormat is a little disappointing coming from Fortran or C's printf.
    // Since it doesn't pad on the left, the elements will come out different
    // widths.  Consequently, we'll pass the desired column width in as an
    // argument and do the extra padding ourselves.
    /**
     * Print the matrix to the output stream. Line the elements up in columns.
     * Use the format object, and right justify within columns of width
     * characters.
     *
     * @param output the output stream.
     * @param format A formatting object to format the matrix elements
     * @param width Column width.
     */
    public static void printMatrix(PrintWriter output, NumberFormat format, float[][] A, int m, int n, int width) {
        output.println();  // start on new line.
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                String s = format.format(A[i][j]); // format the number
                int padding = Math.max(1, width - s.length()); // At _least_ 1 space
                for (int k = 0; k < padding; k++) {
                    output.print(' ');
                }
                output.print(s);
            }
            output.println();
        }
        output.println();   // end with blank line.
    }

    public static void printMatrix(PrintWriter output, NumberFormat format, double[][] A, int m, int n, int width) {
        output.println();  // start on new line.
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                String s = format.format(A[i][j]); // format the number
                int padding = Math.max(1, width - s.length()); // At _least_ 1 space
                for (int k = 0; k < padding; k++) {
                    output.print(' ');
                }
                output.print(s);
            }
            output.println();
        }
        output.println();   // end with blank line.
    }

    public static void printMatrix(PrintWriter output, NumberFormat format, int[][] A, int m, int n, int width) {
        output.println();  // start on new line.
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                String s = format.format(A[i][j]); // format the number
                int padding = Math.max(1, width - s.length()); // At _least_ 1 space
                for (int k = 0; k < padding; k++) {
                    output.print(' ');
                }
                output.print(s);
            }
            output.println();
        }
        output.println();   // end with blank line.
    }

    /**
     * Method for printing a matrix <br> Based on ER Harold, "Java I/O",
     * O'Reilly, around p. 473.
     *
     * @param n1 row dimension of matrix
     * @param n2 column dimension of matrix
     * @param m input matrix values
     * @param d display precision, number of decimal places
     * @param w display precision, total width of floating value
     */
    public static void printMatrix(int n1, int n2, double[][] m, int d, int w) {
        // Some definitions for handling output formating
        NumberFormat myFormat = NumberFormat.getNumberInstance();
        FieldPosition fp = new FieldPosition(NumberFormat.INTEGER_FIELD);
        myFormat.setMaximumIntegerDigits(d);
        myFormat.setMaximumFractionDigits(d);
        myFormat.setMinimumFractionDigits(d);
        for (int i = 0; i < n1; i++) {
            // Print each row, elements separated by spaces
            for (int j = 0; j < n2; j++) // Following unfortunately doesn't format at all
            //                  System.out.print(m[i][j] + "  ");
            {
                String valString = myFormat.format(
                        m[i][j], new StringBuffer(), fp).toString();
                valString = getSpaces(w - fp.getEndIndex()) + valString;
                System.out.print(valString);
            }
            // Start a new line at the end of a row
            System.out.println();
        }
        // Leave a gap after the entire matrix
        System.out.println();
    } // printMatrix

    public static void matrix(float[][] A, String name, int d) {
        System.out.println(name);
        DecimalFormat format = new DecimalFormat();
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);
        printMatrix(new PrintWriter(System.out, true), format, A, A.length, A[0].length, d + 5);
    }

    public static void matrix(double[][] A, String name, int d) {
        System.out.println(name);
        DecimalFormat format = new DecimalFormat();
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);
        printMatrix(new PrintWriter(System.out, true), format, A, A.length, A[0].length, d + 5);
    }

    public static void matrix(int[][] A, String name, int d) {
        System.out.println(name);
        DecimalFormat format = new DecimalFormat();
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);
        printMatrix(new PrintWriter(System.out, true), format, A, A.length, A[0].length, 6);
    }

    // Little method for helping in output formating
    public static String getSpaces(int n) {

        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(' ');
        }
        return sb.toString();
    } // getSpaces
}
