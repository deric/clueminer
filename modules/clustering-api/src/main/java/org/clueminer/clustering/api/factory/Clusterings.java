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
package org.clueminer.clustering.api.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public class Clusterings<E extends Instance, C extends Cluster<E>> {

    /**
     * Allows creating new clusterings without declaring direct dependency on
     * the class implementing underlying structure
     *
     * NOTE: in case that we have many classes providing structures we can not
     * influence which one will be chosen
     *
     * @return
     */
    public static Clustering<? extends Instance, ? extends Cluster> newList() {
        //simple lookup would return an existing instance on any clustering
        Class c = Lookup.getDefault().lookup(Clustering.class).getClass();
        Clustering<? extends Instance, ? extends Cluster> res = null;
        try {
            res = (Clustering<? extends Instance, ? extends Cluster>) c.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        }
        return res;
    }

    /**
     * Create new instance of clustering structure (if any implementation
     * available)
     *
     * @param size expected size of clusterings
     * @return
     */
    public static Clustering<? extends Instance, ? extends Cluster> newList(int size) {
        Class c = Lookup.getDefault().lookup(Clustering.class).getClass();
        Clustering<? extends Instance, ? extends Cluster> res = null;
        try {
            Constructor<?> ctor = c.getConstructor(Integer.class);
            res = (Clustering<? extends Instance, ? extends Cluster>) ctor.newInstance(new Object[]{size});
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            Exceptions.printStackTrace(ex);
        }
        return res;
    }

}
