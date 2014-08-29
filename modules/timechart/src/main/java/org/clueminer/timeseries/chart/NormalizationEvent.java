package org.clueminer.timeseries.chart;

import java.util.EventObject;

/**
 *
 * @author Tomas Barton
 */
public class NormalizationEvent extends EventObject {
    private Long time;
    private int index;
    
    public NormalizationEvent(Object source, Long time, int index) {
        super(source);
        this.time = time;
        this.index = index;
    }
    
    public Long getTime(){
        return this.time;
    }
    
    public int getIndex(){
        return index;
    }
}
