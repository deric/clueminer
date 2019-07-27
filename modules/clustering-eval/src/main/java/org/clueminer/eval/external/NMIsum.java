/*
 * Copyright (C) 2011-2019 clueminer.org
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
package org.clueminer.eval.external;

import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.openide.util.lookup.ServiceProvider;

/**
 * Normalized Mutual Information as defined by Kvalseth (1987). Mutual information
 * is normalized by an average of entropies.
 *
 * T. O. Kvalseth. Entropy and correlation: Some comments. Systems, Man and
 * Cybernetics, IEEE Transactions on, 17(3):517–519, 1987.
 *
 * Fred, Ana L.N., Jain, Anil K.: Robust Data Clustering. Proceedings of
 * IEEE Computer Society Conference on Computer Vision and Pattern
 * Recognition, CVPR, (3):128-136, 2003.
 *
 * @author deric
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class NMIsum extends NMIbase implements ClusterEvaluation {

    private static final String NAME = "NMI-avg";
    private static final long serialVersionUID = -8838355537225622273L;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double countNMI(double mutualInformation, double ent1, double ent2) {
        return 2 * mutualInformation / (ent1 + ent2);
    }

}
