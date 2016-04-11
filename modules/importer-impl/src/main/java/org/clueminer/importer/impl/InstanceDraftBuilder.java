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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.impl.AbstractRowFactory;
import static org.clueminer.dataset.impl.AbstractRowFactory.string2Double;
import org.clueminer.dataset.impl.DoubleArrayFactory;
import org.clueminer.dataset.row.Tools;
import org.clueminer.dataset.row.XYInstance;
import org.clueminer.exception.EscapeException;
import org.clueminer.importer.Issue;
import org.clueminer.io.importer.api.ContainerLoader;

/**
 *
 * @author deric
 */
public class InstanceDraftBuilder<E extends Instance> extends AbstractRowFactory<E> implements InstanceBuilder<E> {

    private final ContainerLoader container;

    public InstanceDraftBuilder(Dataset<E> dataset, ContainerLoader container) {
        super(dataset);
        this.container = container;
    }

    public InstanceDraftBuilder(Dataset<E> dataset, char decimalSeparator, ContainerLoader container) {
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

    @Override
    public E create(String[] strings, Attribute[] attributes) {
        XYInstance dataRow = (XYInstance) create(strings.length);
        for (int i = 0; i < strings.length; i++) {
            if (strings[i] != null) {
                strings[i] = strings[i].trim();
            }
            if ((strings[i] != null) && (strings[i].length() > 0) && (!strings[i].equals("?"))) {
                if (attributes[i].isNominal()) {
                    try {
                        String unescaped = Tools.unescape(strings[i]);
                        dataRow.set(attributes[i].getIndex(), attributes[i].getMapping().mapString(unescaped));
                    } catch (EscapeException ex) {
                        Logger.getLogger(DoubleArrayFactory.class.getName()).log(Level.SEVERE, null, ex);
                        container.getReport().logIssue(new Issue(attributes[i].getName() + ": " + ex.getMessage(), Issue.Level.INFO, ex));
                    }
                } else {
                    dataRow.set(attributes[i].getIndex(), string2Double(strings[i], this.decimalFormat));
                }
            } else {
                dataRow.set(attributes[i].getIndex(), Double.NaN);
            }
        }
        return (E) dataRow;
    }

}
