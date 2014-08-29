package org.clueminer.dataset.row;

import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.InstanceBuilder;

/**
 *
 * @author Tomas Barton
 */
public class TimeRowFactory implements InstanceBuilder<TimeRow> {

    private int capacity = 50;

    public TimeRowFactory() {
    }

    public TimeRowFactory(int capacity) {
        if (capacity > 0) {
            this.capacity = capacity;
        }
    }

    @Override
    public TimeRow create() {
        return new TimeRow(Double.class, capacity);
    }

    @Override
    public TimeRow createCopyOf(TimeRow orig) {
        TimeRow copy = new TimeRow(Double.class, orig.getCapacity());
        copy.timePoints = orig.timePoints;
        return copy;
    }

    @Override
    public TimeRow createCopyOf(TimeRow orig, Dataset<TimeRow> parent) {
        return createCopyOf(orig);
    }

    @Override
    public TimeRow create(int capacity) {
        return new TimeRow(Double.class, capacity);
    }

    @Override
    public TimeRow create(double[] values) {
        if (values.length != capacity) {
            throw new RuntimeException("expected " + capacity + " but got " + values.length);
        }
        TimeRow inst = new TimeRow(Double.class, capacity);
        for (int i = 0; i < values.length; i++) {
            inst.set(i, values[i]);
        }
        return inst;
    }

    @Override
    public TimeRow create(double[] values, Object classValue) {
        TimeRow inst = create(values);
        inst.setClassValue(classValue);
        return inst;
    }

    @Override
    public TimeRow create(String[] strings, Attribute[] attributes) {
        double val[] = new double[strings.length];
        int i = 0;
        for (String str : strings) {
            val[i++] = Double.valueOf(str);
        }
        return create(val);
    }
}
