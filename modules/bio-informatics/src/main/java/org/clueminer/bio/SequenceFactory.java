/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.bio;

import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.impl.AbstractRowFactory;
import org.clueminer.exception.ParserError;

/**
 *
 * @author deric
 */
public class SequenceFactory<E extends Instance> extends AbstractRowFactory<E> implements InstanceBuilder<E> {

    public SequenceFactory(Dataset<E> dataset) {
        super(dataset);
    }

    @Override
    protected void dispatch(Object value, Attribute attr, E row) throws ParserError {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public E createCopyOf(E orig) {
        GenomeSequence row = new GenomeSequence(orig.size());
        row.setId(orig.getId());
        row.setIndex(orig.getIndex());
        row.setClassValue(orig.classValue());
        return (E) row;
    }

    @Override
    public E build(int capacity) {
        return (E) new GenomeSequence(capacity);
    }

    @Override
    public E build(double[] values) {
        GenomeSequence row = new GenomeSequence(values.length);
        for (int i = 0; i < values.length; i++) {
            row.set(i, values[i]);
        }
        return (E) row;
    }

}
