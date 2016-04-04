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
package org.clueminer.processor;

import java.util.Comparator;
import org.clueminer.io.importer.api.AttributeDraft;

/**
 * Sort attributes by index (number of column)
 *
 * @author deric
 */
public class AttributeComparator implements Comparator<AttributeDraft> {

    @Override
    public int compare(AttributeDraft attr1, AttributeDraft attr2) {

        int id1 = attr1.getIndex();
        int id2 = attr2.getIndex();

        if (id1 > id2) {
            return 1;
        } else if (id1 < id2) {
            return -1;
        } else {
            return 0;
        }
    }

}
