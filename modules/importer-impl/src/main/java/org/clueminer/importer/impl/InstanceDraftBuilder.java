/*
 * Copyright (C) 2011-2016 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.importer.impl;

import java.text.DecimalFormat;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.api.TypeHandler;
import org.clueminer.dataset.impl.AbstractRowFactory;
import org.clueminer.exception.ParserError;
import org.clueminer.importer.Issue;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.InstanceDraft;

/**
 *
 * @author deric
 */
public class InstanceDraftBuilder<E extends Instance> extends AbstractRowFactory<E> implements InstanceBuilder<E> {

    private final Container container;

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
                String val = value.toString();
                if (missing.contains(val)) {
                    row.set(attr.getIndex(), Double.NaN);
                } else {
                    switch (at) {
                        case NUMERICAL:
                        case NUMERIC:
                        case REAL:
                            try {
                                //row.set(attr.getIndex(), string2Double(value.toString(), df));
                                ((InstanceDraft) row).setObject(attr.getIndex(), value);
                            } catch (RuntimeException ex) {
                                ((InstanceDraft) row).setObject(attr.getIndex(), value);
                                // container.getReport().logIssue(new Issue("could not convert " + value.getClass().getName() + " to " + attr.getType(), Issue.Level.CRITICAL));
                            }
                            break;
                        case STRING:
                            ((InstanceDraft) row).setObject(attr.getIndex(), value);
                            break;
                    }
                }
            }
        });

    }

    public InstanceDraftBuilder(Dataset<E> dataset, Container container) {
        super(dataset);
        this.container = container;
    }

    public InstanceDraftBuilder(Dataset<E> dataset, char decimalSeparator, Container container) {
        super(dataset, decimalSeparator);
        this.container = container;
    }

    @Override
    public E createCopyOf(E orig) {
        InstanceDraftImpl row = new InstanceDraftImpl(container, orig.size());
        row.setId(orig.getId());
        row.setIndex(orig.getIndex());
        row.setClassValue(orig.classValue());
        return (E) row;
    }

    @Override
    public E build(int capacity) {
        return (E) new InstanceDraftImpl(container, capacity);
    }

    @Override
    public E build(double[] values) {
        InstanceDraftImpl row = new InstanceDraftImpl(container, values.length);
        for (int i = 0; i < values.length; i++) {
            row.set(i, values[i]);
        }
        return (E) row;
    }

    /**
     * Generic type convertor. Supported types should be initialized in
     * <code>dispatch</code> variable in child class.
     *
     * @param value
     * @param attr
     * @param row
     */
    @Override
    public void set(Object value, Attribute attr, E row) {
        if (attr.isNominal()) {
            row.set(attr.getIndex(), attr.getMapping().mapString((String.valueOf(value).trim())));
        } else {
            TypeHandler h = dispatch.get(value.getClass());
            if (h == null) {
                //put all problems into report
                container.getReport().logIssue(new Issue("could not convert " + value.getClass().getName() + " to " + attr.getType(), Issue.Level.CRITICAL));
            }
            try {
                h.handle(value, attr, row, decimalFormat);
            } catch (ParserError ex) {
                container.getReport().logIssue(new Issue(ex, Issue.Level.SEVERE));
            }
        }
    }

}
