package org.clueminer.dataset.row;

import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;

/**
 *
 * @author Tomas Barton
 */
public class TimeRowFactory implements InstanceBuilder<TimeRow> {

    private int capacity = 50;
    private Dataset<Instance> dataset;

    public TimeRowFactory(Dataset<? extends Instance> dataset) {
        this.dataset = (Dataset<Instance>) dataset;
    }

    public TimeRowFactory(Dataset<? extends Instance> dataset, int capacity) {
        this.dataset = (Dataset<Instance>) dataset;
        if (capacity > 0) {
            this.capacity = capacity;
        }
    }

    @Override
    public TimeRow create() {
        TimeRow inst = build();
        dataset.add(inst);
        return inst;
    }

    @Override
    public TimeRow build() {
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
        TimeRow inst = build(capacity);
        dataset.add(inst);
        return inst;
    }

    @Override
    public TimeRow build(int capacity) {
        return new TimeRow(Double.class, capacity);
    }

    @Override
    public TimeRow create(double[] values) {
        TimeRow inst = build(values);
        dataset.add(inst);
        return inst;
    }

    @Override
    public TimeRow build(double[] values) {
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
        TimeRow inst = build(values, (String) classValue);
        dataset.add(inst);
        return inst;
    }

    @Override
    public TimeRow create(double[] values, String classValue) {
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

    @Override
    public TimeRow build(double[] values, String classValue) {
        TimeRow inst = create(values);
        inst.setClassValue(classValue);
        return inst;
    }
}
