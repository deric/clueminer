package org.clueminer.clustering.aggl;

/**
 *
 * @author Tomas Barton
 */
public class Element implements Comparable {

    private double value;
    private int row;
    private int column;

    public Element(double value, int row, int column) {
        this.value = value;
        this.row = row;
        this.column = column;
    }

    @Override
    public int compareTo(Object o) {
        Element other = (Element) o;
        double diff = this.value - other.value;
        if (diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        }
        return 0;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Element [ ");
        sb.append(value).append(" (").append(row).append(", ").append(column);
        sb.append(")]");
        return sb.toString();
    }

}
