package org.clueminer.dataset.row;

import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.impl.AbstractRowFactory;

/**
 *
 * @author Tomas Barton
 */
public class TimeRowFactory<E extends TimeRow> extends AbstractRowFactory<E> implements InstanceBuilder<E> {

    private int capacity = 50;

    public TimeRowFactory(Dataset<E> dataset) {
        super(dataset);
    }

    public TimeRowFactory(Dataset<E> dataset, int capacity) {
        super(dataset);
        if (capacity > 0) {
            this.capacity = capacity;
        }
    }

    @Override
    public TimeRow createCopyOf(TimeRow orig) {
        TimeRow copy = new TimeRow(Double.class, orig.getCapacity());
        copy.timePoints = orig.timePoints;
        return copy;
    }

    @Override
    public E create(int capacity) {
        TimeRow inst = build(capacity);
        dataset.add((E) inst);
        return (E) inst;
    }

    @Override
    public E build(int capacity) {
        return (E) new TimeRow(Double.class, capacity);
    }

    @Override
    public E create(double[] values) {
        TimeRow inst = build(values);
        dataset.add((E) inst);
        return (E) inst;
    }

    @Override
    public E build(double[] values) {
        if (values.length != capacity) {
            throw new RuntimeException("expected " + capacity + " but got " + values.length);
        }
        TimeRow inst = new TimeRow(Double.class, capacity);
        for (int i = 0; i < values.length; i++) {
            inst.set(i, values[i]);
        }
        return (E) inst;
    }

    @Override
    public E create(double[] values, Object classValue) {
        E inst = build(values, (String) classValue);
        dataset.add(inst);
        return inst;
    }

    @Override
    public E create(double[] values, String classValue) {
        TimeRow inst = create(values);
        inst.setClassValue(classValue);
        return (E) inst;
    }

    @Override
    public E create(String[] strings, Attribute[] attributes) {
        double val[] = new double[strings.length];
        int i = 0;
        for (String str : strings) {
            val[i++] = Double.valueOf(str);
        }
        return (E) create(val);
    }

    @Override
    public E build(double[] values, String classValue) {
        TimeRow inst = create(values);
        inst.setClassValue(classValue);
        return (E) inst;
    }

    @Override
    public void set(String value, Attribute attr, E row) {
        row.set(attr.getIndex(), string2Double(value, this.decimalFormat));
    }

    @Override
    public void set(double value, Attribute attr, E row) {
        row.set(attr.getIndex(), value);
    }
}
