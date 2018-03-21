/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.clustering.aggl.linkage;

import org.clueminer.clustering.aggl.HC;
import org.clueminer.clustering.aggl.HCLW;
import org.clueminer.clustering.api.AbstractLinkage;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public class AbstractLinkageTest {

    protected AbstractLinkage subject;
    protected static final HC hac = new HC();
    protected static final HCLW haclw = new HCLW();
    protected final double delta = 1e-9;

    protected HierarchicalResult naiveLinkage(Dataset<? extends Instance> dataset) {
        Props pref = new Props();
        pref.put(AlgParams.LINKAGE, subject.getName());
        pref.put(AlgParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        HierarchicalResult result = hac.hierarchy(dataset, pref);
        return result;
    }

    protected HierarchicalResult lanceWilliamsLinkage(Dataset<? extends Instance> dataset) {
        Props pref = new Props();
        pref.put(AlgParams.LINKAGE, subject.getName());
        pref.put(AlgParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        HierarchicalResult result = haclw.hierarchy(dataset, pref);
        return result;
    }

}
