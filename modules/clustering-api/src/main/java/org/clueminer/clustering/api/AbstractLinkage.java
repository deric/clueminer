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
package org.clueminer.clustering.api;

import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;

/**
 * The method to use when comparing the similarity of two clusters. See <a
 * href="http://home.dei.polimi.it/matteucc/Clustering/tutorial_html/hierarchical.html">
 * here </a> for an example of how the different linkages operate.
 *
 * @author Tomas Barton
 * @param <E>
 */
public abstract class AbstractLinkage<E extends Instance> implements ClusterLinkage<E> {

    private static final long serialVersionUID = -4024535548255187696L;
    protected Distance distanceMeasure;

    public AbstractLinkage(Distance dm) {
        this.distanceMeasure = dm;
    }

    public Distance getDistanceMeasure() {
        return distanceMeasure;
    }

    @Override
    public void setDistanceMeasure(Distance distanceMeasure) {
        this.distanceMeasure = distanceMeasure;
    }
}
