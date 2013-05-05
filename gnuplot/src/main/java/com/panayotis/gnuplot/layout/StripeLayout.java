package com.panayotis.gnuplot.layout;

/**
 * Align graphs evenly on the page
 * This Layout is based on AutoGraphLayout
 */
public class StripeLayout extends AutoGraphLayout {

    /**
     * Information if rows or columns are added automatically
     */
    public static final boolean EXPANDROWS = true, EXPANDCOLUMNS = false;

    /**
     * Create a new Strip layout. Default behaviour is EXPANDROWS.
     */
    public StripeLayout() {
        setType(EXPANDROWS);
    }

    /**
     * Set the default behaviour
     * @param type Whether EXPANDROWS or EXPANDCOLUMNS is desired.
     * @see #EXPANDROWS #EXPANDCOLUMNS
     */
    public void setType(boolean type) {
       if (type==EXPANDROWS) {
           super.setRows(-1);
           super.setColumns(1);
       } else {
           super.setRows(1);
           super.setColumns(-1);
       }
    }

    /**
     * Set behaviour, depending on the number of rows. It always creates stripes and it might change to EXPANDCOLUMNS if rows are less than 2.
     * @param rows Number of desired rows
     */
    public void setRows(int rows) {
        if (rows>1)
            setType(EXPANDROWS);
        else
            setType(EXPANDCOLUMNS);
    }

    /**
     * Set behaviour, depending on the number of columns. It always creates stripes and it might change to EXPANDROWS if columns are less than 2.
     * @param cols Number of desired columns
     */
    public void setColumns(int cols) {
        if (cols>1)
            setType(EXPANDCOLUMNS);
        else
            setType(EXPANDROWS);
    }
}
