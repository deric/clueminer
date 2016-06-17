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
package org.clueminer.bio;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.impl.ArrayDataset;

/**
 *
 * @author deric
 */
public class GenomeSet<E extends Instance> extends ArrayDataset<E> implements Dataset<E> {

    private static final long serialVersionUID = 2123058901652874266L;

    public GenomeSet(int instancesCapacity, int attributesCnt) {
        super(instancesCapacity, attributesCnt);
    }

    @Override
    public InstanceBuilder builder() {
        if (builder == null) {
            builder = new SequenceFactory(this);
        }
        return builder;
    }

}
