package org.clueminer.timeseries.chart;

import java.util.EventObject;

/**
 *
 * @author Tomas Barton
 */
public class SelectionEvent extends EventObject {
    private int start, end;
    
    public SelectionEvent(Object source, int start, int end) {
        super(source);
        this.start = start;
        this.end = end;
    }

    /**
     * @return the start timepoint's index of selection
     */
    public int getStart() {
        return start;
    }

    /**
     * @return the end timepoint's index of selection
     */
    public int getEnd() {
        return end;
    }
    
}

