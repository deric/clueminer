package org.clueminer.xcalibour.data;

import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import javax.swing.JComponent;
import org.clueminer.algorithm.BinarySearch;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.AttributeBuilder;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.plugin.AbstractDataset;
import org.clueminer.math.Interpolator;
import org.clueminer.types.TimePoint;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Tomas Barton
 */
public class SpectrumDataset<E extends ContinuousInstance> extends AbstractDataset<E> implements Timeseries<E>, Dataset<E> {

    private static final long serialVersionUID = -5124177426971291877L;
    private int max_attributes = 0;
    private double min = Double.POSITIVE_INFINITY;
    private double max = Double.NEGATIVE_INFINITY;
    private TimePointAttribute[] timePoints;
    private double[] timepointPosition;

    public SpectrumDataset(int capacity) {
        super(capacity);
    }

    @Override
    public SortedSet<Object> getClasses() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(Dataset<E> d) {
        return addAll(d);
    }

    @Override
    public E instance(int index) {
        return get(index);
    }

    @Override
    public boolean add(E instance) {
        check(instance);
        //instance might not contain any data yet
        checkMinMax(instance);
        return super.add(instance);
    }

    /**
     * Checks number of attributes in added instance
     *
     * @param instance Instance
     */
    protected void check(E instance) {
        if (instance.size() > this.yAttrCount()) {
            max_attributes = instance.size();
        }
    }

    @Override
    public E getRandom(Random rand) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int attributeCount() {
        return xAttrCount();

    }

    /**
     *
     * @return number of timepoints
     */
    public int xAttrCount() {
        if (timePoints == null) {
            return 0;
        }
        return timePoints.length;
    }

    /**
     * Mass spectrum measurements
     *
     * @return max number of measurements in all dataset
     */
    public int yAttrCount() {
        return max_attributes;
    }

    @Override
    public int classIndex(Object clazz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object classValue(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Attribute[] copyAttributes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Integer, Attribute> getAttributes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Attribute getAttribute(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getAttributeValue(String attributeName, int instanceIdx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getAttributeValue(Attribute attribute, int instanceIdx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getAttributeValue(int attributeIndex, int instanceIdx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAttributeValue(String attributeName, int instanceIdx, double value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAttribute(int index, Attribute attr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAttributes(Map<Integer, Attribute> attributes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public InstanceBuilder builder() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AttributeBuilder attributeBuilder() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dataset<E> copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JComponent getPlotter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void crop(int begin, int end, ProgressHandle ph) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double interpolate(int index, double x, Interpolator interpolator) {
        int idx = BinarySearch.search(timePoints, x);
        int low = 0, up = 0;
        Instance curr = get(index);
        if (curr == null) {
            throw new RuntimeException("instance " + index + " not found");
        }
        if (timePoints[idx].getValue() > x) {
            up = idx;
            low = idx - 1;
        } else if (timePoints[idx].getValue() < x) {
            low = idx;
            up = idx + 1;
        }

        return interpolator.getValue(getTimePointsArray(), curr.arrayCopy(), x, low, up);
    }

    @Override
    public TimePoint[] getTimePoints() {
        return timePoints;
    }
    
    public TimePoint getTimePoint(int i){
        return timePoints[i];
    }

    @Override
    public double[] getTimePointsArray() {
        /**
         * @TODO @FIXME this is ugly however could be quite fast. if there would
         * be some way how to solve Numeric[] to double[] conversion...
         * java.lang.Number is AbstractClass not an interface we can't inherite
         * TimePoint from that one :(
         */
        if (timepointPosition == null) {
            timepointPosition = new double[timePoints.length];
            for (int i = 0; i < timePoints.length; i++) {
                timepointPosition[i] = timePoints[i].getPosition();
            }
        }
        return timepointPosition;
    }

    @Override
    public void setTimePoints(TimePoint[] tp) {
        timePoints = (TimePointAttribute[]) tp;
    }

    @Override
    public double getMin() {
        return min;
    }

    @Override
    public double getMax() {
        return max;
    }

    /**
     * @TODO not safe for removing items!
     *
     * @param item
     */
    public void checkMinMax(ContinuousInstance item) {
        double v = item.getMax();
        if (v > this.max) {
            max = v;
        }
        v = item.getMin();
        if (v < this.min) {
            min = v;
        }
    }

    public void resetMinMax() {
        max = Double.MIN_VALUE;
        min = Double.MAX_VALUE;
        double val;
        for (E elem : this) {
            val = elem.getMax();
            if (val > max) {
                max = val;
            }
            if (val < min) {
                min = val;
            }
        }
    }

    @Override
    public Attribute getAttribute(String attributeName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dataset<E> duplicate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
