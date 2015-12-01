package org.clueminer.dataset.plugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.clueminer.algorithm.BinarySearch;
import org.clueminer.attributes.AttributeFactoryImpl;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.colors.PaletteGenerator;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.row.TimeRowFactory;
import org.clueminer.math.Interpolator;
import org.clueminer.types.TimePoint;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public class TimeseriesDataset<E extends ContinuousInstance> extends AbstractDataset<E> implements Timeseries<E>, Dataset<E> {

    private static final long serialVersionUID = 1599199299678410906L;
    protected TimePointAttribute[] timePoints;
    protected double max = Double.MIN_VALUE;
    protected double min = Double.MAX_VALUE;
    private double[] timepointPosition;
    protected InstanceBuilder builder;
    protected AttributeFactoryImpl attributeBuilder;
    protected TreeSet<Object> classes = new TreeSet<>();
    private static final Logger logger = Logger.getLogger(TimeseriesDataset.class.getName());

    /**
     * Creates dataset with given initial capacity
     *
     * @param capacity
     */
    public TimeseriesDataset(int capacity) {
        super(capacity);
        colorGenerator = new PaletteGenerator();
    }

    public TimeseriesDataset(int capacity, TimePointAttribute[] tp) {
        super(capacity);
        colorGenerator = new PaletteGenerator();
        setTimePoints(tp);
    }

    @Override
    public int attributeCount() {
        if (timePoints != null) {
            return timePoints.length;
        }
        return 0;
    }

    /**
     * Get name of i-th attribute
     *
     * @param i
     * @return
     */
    @Override
    public Attribute getAttribute(int i) {
        if (i < 0 || i >= timePoints.length) {
            throw new RuntimeException("invalid attribute index, timepoints length is " + timePoints.length);
        }
        return timePoints[i];
    }

    /**
     * Set i-th attribute (column)
     *
     * @param i
     * @param attr
     */
    @Override
    public void setAttribute(int i, Attribute attr) {
        timePoints[i] = (TimePointAttribute) attr;
    }

    @Override
    public void setAttributes(Map<Integer, Attribute> attr) {
        for (Entry<Integer, Attribute> next : attr.entrySet()) {
            timePoints[next.getKey()] = (TimePointAttribute) next.getValue();
        }
    }

    @Override
    public void setAttributes(Attribute[] attributes) {
        int i = attributeCount();
        for (Attribute attr : attributes) {
            timePoints[i++] = (TimePointAttribute) attr;
        }
    }

    /**
     * Adds new instance and checks consistency with the dataset. We don't have
     * to check min/mix is will be computed when min/max is requested (lazy
     * approach)
     *
     * @param instance
     * @return
     */
    @Override
    public boolean add(E instance) {
        if (instance.getName() == null) {
            instance.setName(this.getName() + "-" + this.size());
        }
        check(instance);
        instance.setParent(this);
        instance.setColor(colorGenerator.next());
        if (instance.getIndex() < 0) {
            instance.setIndex(size());
        }
        return super.add(instance);
    }

    @Override
    public boolean addAll(Dataset<? extends E> d) {
        boolean success = true;
        for (E instance : d) {
            check(instance);
            checkMinMax(instance);
            success &= add(instance);
        }
        return success;
    }

    /**
     * Checks number of attributes in added instance
     *
     * @param instance Instance
     */
    protected void check(Instance instance) {
        if (instance.classValue() != null) {
            classes.add(instance.classValue());
        }
        if (instance.size() > this.attributeCount()) {
            System.out.println(instance);
            throw new RuntimeException("instance contains attributes that are not "
                    + "defined in dataset! expected " + this.attributeCount() + " attributes, but got: " + instance.size());
        }
    }

    /**
     * We have attributes in an array, it's not very efficient to convert them
     * If you really need this, use different dataset
     */
    @Override
    public Map<Integer, Attribute> getAttributes() {
        Map<Integer, Attribute> map = new HashMap<>(timePoints.length);
        for (int i = 0; i < timePoints.length; i++) {
            map.put(i, timePoints[i]);
        }
        return map;
    }

    @Override
    public Attribute[] copyAttributes() {
        return timePoints.clone();
    }

    @Override
    public TimePointAttribute[] getTimePoints() {
        return timePoints;
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
    public final void setTimePoints(TimePoint[] tp) {
        timePoints = (TimePointAttribute[]) tp;
        checkAttributes(timePoints);
    }

    /**
     * Check if array contains a null attribute
     *
     * @param tps
     */
    private void checkAttributes(TimePointAttribute[] tps) {
        int i = 0;
        for (TimePointAttribute timep : tps) {
            i++;
            if (timep == null) {
                //debuging output
                for (int j = 0; j < tps.length; j++) {
                    System.out.println("time point[" + j + "]: " + tps[j]);
                }
                throw new RuntimeException(i + "th is null!!!");
            }
            timep.setDataset(this);
        }
    }

    @Override
    public void crop(int begin, int end, org.netbeans.api.progress.ProgressHandle ph) {
        int size = end - begin;

        TimePointAttribute[] pointsNew = new TimePointAttribute[size];
        //hardlink references from source array to destination array
        System.arraycopy(timePoints, begin, pointsNew, 0, size);

        long startTime = pointsNew[0].getTimestamp();
        for (int i = 0; i < size; i++) {
            pointsNew[i].setTimestamp(pointsNew[i].getTimestamp() - startTime);
        }

        //relative time
        double time, endTime = pointsNew[pointsNew.length - 1].getTimestamp();
        //precomputed value for faster chart rendering
        for (int i = 0; i < size; i++) {
            //we do care how far are items from each other, but not absolute time
            time = pointsNew[i].getTimestamp();
            pointsNew[i].setPosition(time / endTime);
        }

        resetMinMax();
        for (int i = 0; i < size(); i++) {
            ContinuousInstance inst = this.get(i);
            inst.crop(begin, size);
            checkMinMax(inst);
            ph.progress(i);
        }
        setTimePoints(pointsNew);
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
            val = elem.getMin();
            if (val < min) {
                min = val;
            }
        }
    }

    /**
     * *
     * Computes position on axis Y for
     *
     * @param index
     * @param x
     * @param interpolator
     * @return
     */
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

        if (!interpolator.hasData()) {
            interpolator.setX(timePoints);
            interpolator.setY(curr.arrayCopy());
        }
        return interpolator.value(x, low, up);
    }

    /**
     * Minimum Y value in the dataset
     *
     * @return
     */
    @Override
    public double getMin() {
        if (min == Double.MAX_VALUE) {
            resetMinMax();
        }
        return min;
    }

    /**
     * Maximum Y value in the dataset
     *
     * @return
     */
    @Override
    public double getMax() {
        if (max == Double.MIN_VALUE) {
            resetMinMax();
        }
        return max;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof TimeseriesDataset)) {
            return false;
        }
        TimeseriesDataset<ContinuousInstance> that = (TimeseriesDataset<ContinuousInstance>) obj;
        return that.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        super.hashCode();
        int hash = 5;
        hash = 79 * hash + Arrays.deepHashCode(this.timePoints);
        //hash of
        hash += super.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TimeseriesDataset [");
        for (Instance i : this) {
            sb.append(i.toString()).append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public SortedSet<Object> getClasses() {
        return classes;
    }

    @Override
    public int classIndex(Object clazz) {
        if (clazz != null) {
            return this.getClasses().headSet(clazz).size();
        } else {
            return -1;
        }

    }

    @Override
    public Object classValue(int index) {
        int i = 0;
        for (Object o : this.classes) {
            if (i == index) {
                return o;
            }
            i++;
        }
        return null;
    }

    @Override
    public void changedClass(Object orig, Object current, Object source) {
        if (current != null) {
            if (!classes.contains(current)) {
                classes.add(current);
            }
        }
    }

    @Override
    public InstanceBuilder builder() {
        if (builder == null) {
            builder = new TimeRowFactory(this, attributeCount());
        }
        return builder;
    }

    @Override
    public AttributeFactoryImpl attributeBuilder() {
        if (attributeBuilder == null) {
            attributeBuilder = new AttributeFactoryImpl<>(this);
        }
        return attributeBuilder;
    }

    @Override
    public Dataset<E> copy() {
        TimeseriesDataset<ContinuousInstance> out = new TimeseriesDataset<>(this.size());
        out.setTimePoints(timePoints);
        for (ContinuousInstance i : this) {
            out.add(i.copy());
        }
        return (Dataset<E>) out;
    }

    @Override
    public E instance(int index) {
        if (hasIndex(index)) {
            return get(index);
        } else if (index == size()) {
            int attrs = attributeCount() == 0 ? timePoints.length : attributeCount();
            E inst = (E) builder().create(attrs);
            return inst;
        }
        throw new ArrayIndexOutOfBoundsException("can't get instance at position: " + index);
    }

    @Override
    public E getRandom(Random rand) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Searching by a string name is not preferred way of implementing time
     * series datasets, this shouldn't implemented here
     *
     * @param attributeName
     * @param instanceIdx
     * @return
     */
    @Override
    public double getAttributeValue(String attributeName, int instanceIdx) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getAttributeValue(Attribute attribute, int instanceIdx) {
        return get(instanceIdx).value(attribute.getIndex());
    }

    /**
     * {@inheritDoc}
     *
     * @param instanceIdx
     * @param attributeIndex
     * @return
     */
    @Override
    public double get(int instanceIdx, int attributeIndex) {
        return instance(instanceIdx).value(attributeIndex);
    }

    @Override
    public void setAttributeValue(String attributeName, int instanceIdx, double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JComponent getPlotter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Highly ineffective, complexity O(n), it is expected to use access to time
     * points by index
     *
     * @param attributeName
     * @return attribute with given name
     */
    @Override
    public Attribute getAttribute(String attributeName) {
        for (TimePointAttribute tp : timePoints) {
            if (tp.getName().equals(attributeName)) {
                return (Attribute) tp;
            }
        }
        throw new RuntimeException("attribute " + attributeName + " was not found");
    }

    @Override
    public Dataset<E> duplicate() {
        TimeseriesDataset<E> copy = new TimeseriesDataset<>(this.size());
        copy.timePoints = this.timePoints;
        return copy;
    }

    @Override
    public double[] getTimestampsArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addAttribute(Attribute attr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double min() {
        return getMin();
    }

    @Override
    public double max() {
        return getMax();
    }

    @Override
    public void resetStats() {
        for (TimePointAttribute t : timePoints) {
            t.resetStats();
        }
    }

    @Override
    public Collection<? extends Number> attrCollection(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<? extends Date> getTimePointsCollection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
