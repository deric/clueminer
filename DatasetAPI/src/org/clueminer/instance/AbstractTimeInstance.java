package org.clueminer.instance;

import org.clueminer.dataset.Timeseries;
import org.clueminer.interpolation.LinearInterpolator;
import org.clueminer.math.Numeric;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractTimeInstance<E extends Numeric> extends AbstractInstance implements Instance, ContinuousInstance, Iterable<E> {

    private static final long serialVersionUID = 7144673261130372477L;
    protected double max = Double.MIN_VALUE;
    protected double min = Double.MAX_VALUE;
    protected Timeseries<? extends ContinuousInstance> parent;
    //real count
    protected int last = 0;
    protected long startTime;
    
    public AbstractTimeInstance(){
        
    }

    protected void checkMinMax(double val) {
        if (val > this.max) {
            this.max = val;
        }
        if (val < min) {
            min = val;
        }
    }

    @Override
    public int size() {
        return last;
    }

    @Override
    public boolean isEmpty() {
        return (last == 0);
    }

    /**
     * Data loaded from Access are not necessarily sorted by index so the actual
     * count might not correspondent with highest id number
     *
     * @param idx
     * @return
     */
    public boolean contains(int idx) {
        if ((idx >= size() || idx < 0)) {
            return false;
        }

        return true;
    }
    
    public abstract E get(int index);

    public void resetMinMax() {
        this.min = Double.MAX_VALUE;
        this.max = Double.MIN_VALUE;
        E dc;
        for (int i = 0; i < size(); i++) {
            dc = get(i);
            checkMinMax(dc.getValue());
        }
    }
    
        @Override
    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long time) {
        startTime = time;
    }

    /**
     * @TODO maybe store array copy to a variable, but it might use too much
     * memory, this way it can be deallocated quickly
     *
     * @return
     */
    @Override
    public double[] arrayCopy() {
        double[] res = new double[size()];
        for (int i = 0; i < size(); i++) {
            res[i] = this.value(i);
        }
        return res;
    }

    @Override
    public double valueAt(double x) {
        return valueAt(x, new LinearInterpolator());
    }

    @Override
    public double getMax() {
        return max;
    }

    @Override
    public double getMin() {
        return min;
    }
    
    @Override
    public Timeseries<? extends ContinuousInstance> getParent(){
        return parent;
    }
    
    @Override
    public void setParent(Timeseries<? extends ContinuousInstance> parent){
        this.parent = parent;
    }
}
