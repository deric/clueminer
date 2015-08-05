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
package org.clueminer.eval;

import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.distance.EuclideanDistance;
import org.openide.util.lookup.ServiceProvider;

/**
 * The only difference between this version and the standard one is applying
 * SQRT on Euclidean distances.
 *
 * @author deric
 */
@ServiceProvider(service = InternalEvaluator.class)
public class SilhoutetteSqrt extends Silhouette {

    private static final String name = "Silhouette-sqrt";

    public SilhoutetteSqrt() {
        dm = EuclideanDistance.getInstance();
    }

    @Override
    public String getName() {
        return name;
    }

}
