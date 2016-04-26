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

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import java.util.HashMap;
import java.util.Map;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.DataType;
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
 * @param <E>
 */
public class InstanceDraftBuilder<E extends Instance> extends AbstractRowFactory<E> implements InstanceBuilder<E> {

    private final Container container;

    // Make a map that translates a Class object to a Handler
    private static final Map<Class, TypeHandler> dispatch = new HashMap<>();

    static {
        dispatch.put(Double.class, new TypeHandler() {
            @Override
            public void handle(Object value, Attribute attr, Instance row, InstanceBuilder builder) {
                row.set(attr.getIndex(), (Double) value);
            }
        });
        dispatch.put(Float.class, new TypeHandler() {
            @Override
            public void handle(Object value, Attribute attr, Instance row, InstanceBuilder builder) {
                row.set(attr.getIndex(), (Float) value);
            }
        });
        dispatch.put(Integer.class, new TypeHandler() {
            @Override
            public void handle(Object value, Attribute attr, Instance row, InstanceBuilder builder) {
                row.set(attr.getIndex(), (Integer) value);
            }
        });
        dispatch.put(Boolean.class, new TypeHandler() {
            @Override
            public void handle(Object value, Attribute attr, Instance row, InstanceBuilder builder) {
                row.set(attr.getIndex(), (boolean) value ? 1.0 : 0.0);
            }
        });
        dispatch.put(JsonPrimitive.class, new TypeHandler() {
            @Override
            public void handle(Object value, Attribute attr, Instance row, InstanceBuilder builder) {
                JsonPrimitive primitive = (JsonPrimitive) value;
                BasicAttrType at = (BasicAttrType) attr.getType();
                InstanceDraft draft = (InstanceDraft) row;
                switch (at) {
                    case NUMERICAL:
                    case NUMERIC:
                    case REAL:
                        draft.setObject(attr.getIndex(), primitive.getAsDouble());
                        break;
                    case STRING:
                        draft.setObject(attr.getIndex(), primitive.getAsString());
                        break;
                    default:
                        InstanceDraftBuilder b = (InstanceDraftBuilder) builder;
                        b.container.getReport().logIssue(new Issue("could not convert " + value.getClass().getName() + " to " + attr.getType(), Issue.Level.SEVERE));
                }
            }
        });
        dispatch.put(JsonArray.class, new TypeHandler() {
            @Override
            public void handle(Object value, Attribute attr, Instance row, InstanceBuilder builder) {
                JsonArray ary = (JsonArray) value;
                BasicAttrType at = (BasicAttrType) attr.getType();
                InstanceDraft draft = (InstanceDraft) row;
                InstanceDraftBuilder b = (InstanceDraftBuilder) builder;
                switch (at) {
                    case MD_DATA: //TODO: we're not trying to validate multi-dimensional data here
                        draft.setObject(attr.getIndex(), ary);
                        //TODO: consider more advanced checks
                        b.container.setDataType(DataType.XY_CONTINUOUS);
                        break;
                    default:
                        b.container.getReport().logIssue(new Issue("could not convert " + value.getClass().getName() + " to " + attr.getType(), Issue.Level.SEVERE));
                }
            }
        });

        dispatch.put(String.class, new TypeHandler() {
            @Override
            public void handle(Object value, Attribute attr, Instance row, InstanceBuilder builder) {
                BasicAttrType at = (BasicAttrType) attr.getType();
                String val = value.toString();
                if (missing.contains(val)) {
                    row.set(attr.getIndex(), (double) attr.getMissingValue());
                } else {
                    switch (at) {
                        case NUMERICAL:
                        case NUMERIC:
                        case REAL:
                            try {
                                //row.set(attr.getIndex(), string2Double(value.toString(), df));
                                ((InstanceDraft) row).setObject(attr.getIndex(), value);
                            } catch (RuntimeException ex) {
                                InstanceDraftBuilder b = (InstanceDraftBuilder) builder;
                                b.container.getReport().logIssue(new Issue("could not convert " + value.getClass().getName()
                                        + " to " + attr.getType() + ": " + value, Issue.Level.SEVERE));
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
        if (value == null || value.toString().equals("null")) {
            if (attr.allowMissing()) {
                set(attr.getMissingValue(), attr, row);
            } else {
                container.getReport().logIssue(new Issue("missing value not allowed for " + attr.getName(), Issue.Level.WARNING));
            }
        } else if (attr.isNominal()) {
            row.set(attr.getIndex(), attr.getMapping().mapString((String.valueOf(value).trim())));
        } else {
            TypeHandler h = dispatch.get(value.getClass());
            if (h == null) {
                //put all problems into report
                container.getReport().logIssue(new Issue("could not convert " + value.getClass().getName() + " to " + attr.getType(), Issue.Level.WARNING));
                //convert to String
                set(value.toString(), attr, row);
            } else {
                try {
                    h.handle(value, attr, row, this);
                } catch (ParserError ex) {
                    container.getReport().logIssue(new Issue(ex, Issue.Level.SEVERE));
                }
            }
        }
    }

    @Override
    protected void dispatch(Object value, Attribute attr, E row) throws ParserError {
        TypeHandler h = dispatch.get(value.getClass());
        if (h == null) {
            // Throw an exception: unknown type
            throw new RuntimeException("could not convert " + value.getClass().getName() + " to " + attr.getType());
        }
        h.handle(value, attr, row, this);
    }

}
