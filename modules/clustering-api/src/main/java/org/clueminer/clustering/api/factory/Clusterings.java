/*
 * Copyright (C) 2011-2015 clueminer.org
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
package org.clueminer.clustering.api.factory;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.openide.util.Lookup;

/**
 *
 * @author deric
 */
public class Clusterings {

    /**
     * Allows creating new clusterings without declaring direct dependency on
     * the class implementing underlying structure
     *
     * NOTE: in case that we have many classes providing structures we can not
     * influence which one will be chosen
     *
     * @return
     */
    public static Clustering<? extends Cluster> newList() {
        return Lookup.getDefault().lookup(Clustering.class);
    }
}
