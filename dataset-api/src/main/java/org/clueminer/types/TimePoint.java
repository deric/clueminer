package org.clueminer.types;

import org.clueminer.math.Numeric;

/**
 *
 * @author Tomas Barton
 */
public interface TimePoint extends Numeric {

    public long getTimestamp();

    public void setTimestamp(long time);

    public int getIndex();
    /**
     * 
     * @return position in chart
     */
    public double getPosition();
}
