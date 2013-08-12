package org.clueminer.posturomed;

import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.row.IntegerDataRow;
import org.clueminer.math.Interpolator;

/**
 *
 * @author Tomas Barton
 */
public class PosturomedInstance extends IntegerDataRow implements Instance<Integer>, ContinuousInstance<Integer> {

    private static final long serialVersionUID = -7474451177647880896L;
    private int row;
    private int col;
    private Timeseries<? extends ContinuousInstance> parent;
    private int min = Integer.MAX_VALUE;
    private int max = Integer.MIN_VALUE;

    public PosturomedInstance(int size) {
        super(size);
    }

    public PosturomedInstance(Timeseries<? extends ContinuousInstance> plate, int capacity) {
        super(capacity);
        parent = plate;
    }

    @Override
    public double valueAt(double x) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double valueAt(double x, Interpolator interpolator) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getMin() {
        return min;
    }

    @Override
    public double getMax() {
        return max;
    }

    @Override
    public void set(int index, Number value) {
        checkMinMax(value.intValue());
        super.set(index, value);
    }

    private void checkMinMax(int value) {
        if (value < min) {
            min = value;
        }
        if (value > max) {
            max = value;
        }
    }

    @Override
    public int put(int value) {
        checkMinMax(value);
        return super.put(value);
    }

    @Override
    protected void setValue(int index, double value, double defaultValue) {
        checkMinMax((int) value);
        super.setValue(index, value, defaultValue);
    }

    @Override
    public int put(double value) {
        checkMinMax((int) value);
        return super.put(value);
    }

    @Override
    public void crop(int begin, int size) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void normalize(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getStartTime() {
        if (parent.attributeCount() == 0) {
            throw new RuntimeException("no attributes in dataset!");
        }
        return ((TimePointAttribute) parent.getAttribute(0)).getTimestamp();
    }

    @Override
    public ContinuousInstance copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void setParent(Timeseries parent) {
        this.parent = parent;
    }

    @Override
    public Timeseries<? extends ContinuousInstance> getParent() {
        return parent;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int col) {
        this.col = col;
    }

    @Override
    public Plotter getPlotter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String[] toArray() {
        String[] res = new String[size() + 2];
        res[0] = getName();
        res[1] = getId();
        for (int i = 0; i < res.length; i++) {
            res[i + 2] = String.valueOf(intValue(i));

        }
        return res;
    }
}
