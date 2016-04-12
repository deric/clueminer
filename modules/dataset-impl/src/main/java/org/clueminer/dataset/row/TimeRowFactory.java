package org.clueminer.dataset.row;

import java.text.DecimalFormat;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.api.TypeHandler;
import org.clueminer.dataset.impl.AbstractRowFactory;
import static org.clueminer.dataset.impl.AbstractRowFactory.string2Double;

/**
 *
 * @author Tomas Barton
 */
public class TimeRowFactory<E extends TimeRow> extends AbstractRowFactory<E> implements InstanceBuilder<E> {

    private int capacity = 50;

    static {
        dispatch.put(Double.class, new TypeHandler() {
            @Override
            public void handle(Object value, Attribute attr, Instance row, DecimalFormat df) {
                row.set(attr.getIndex(), (Double) value);
            }
        });
        dispatch.put(Float.class, new TypeHandler() {
            @Override
            public void handle(Object value, Attribute attr, Instance row, DecimalFormat df) {
                row.set(attr.getIndex(), (Float) value);
            }
        });
        dispatch.put(Integer.class, new TypeHandler() {
            @Override
            public void handle(Object value, Attribute attr, Instance row, DecimalFormat df) {
                row.set(attr.getIndex(), (Integer) value);
            }
        });
        dispatch.put(Boolean.class, new TypeHandler() {
            @Override
            public void handle(Object value, Attribute attr, Instance row, DecimalFormat df) {
                row.set(attr.getIndex(), (boolean) value ? 1.0 : 0.0);
            }
        });
        dispatch.put(String.class, new TypeHandler() {
            @Override
            public void handle(Object value, Attribute attr, Instance row, DecimalFormat df) {
                BasicAttrType at = (BasicAttrType) attr.getType();
                switch (at) {
                    case NUMERICAL:
                    case NUMERIC:
                    case REAL:
                        row.set(attr.getIndex(), string2Double(value.toString(), df));
                        break;
                    default:
                        throw new RuntimeException("conversion to " + at + " is not supported for '" + value + "'");
                }

            }
        });
    }

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
}
