package org.clueminer.magic;

/**
 *
 * @author Tomas Barton
 */
public class Separator {

    private int prevCount;
    private char symbol;

    public Separator(char symbol) {
        this.symbol = symbol;
    }

    public int getPrevCount() {
        return prevCount;
    }

    public void setPrevCount(int prevCount) {
        this.prevCount = prevCount;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }
}
