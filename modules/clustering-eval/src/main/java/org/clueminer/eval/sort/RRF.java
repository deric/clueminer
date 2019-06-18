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
package org.clueminer.eval.sort;

import java.util.Arrays;
import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.Rank;
import org.clueminer.clustering.api.config.ConfigException;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.ClusteringComparator;
import org.openide.util.lookup.ServiceProvider;

/**
 * Reciprocal Rank Fusion
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = Rank.class)
public class RRF<E extends Instance, C extends Cluster<E>> implements Rank<E, C> {

    private static final String NAME = "RRF";
    public static final String RANK_PROP = "rank";
    private final RankComparator comp;
    //a constant proposed by the authors
    private static final double EPS = 60;

    public RRF() {
        comp = new RankComparator();
        comp.setBaseName(NAME);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Clustering<E, C>[] sort(Clustering<E, C>[] clusterings, List<ClusterEvaluation<E, C>> objectives) {
        ClusteringComparator soComp = new ClusteringComparator();
        comp.setObjectives(objectives);
        double rank;
        Clustering<E, C> clust;

        for (int i = 0; i < objectives.size(); i++) {
            soComp.setEvaluator(objectives.get(i));
            //sort by single objective
            Arrays.parallelSort(clusterings, soComp);
            for (int j = 0; j < clusterings.length; j++) {
                clust = clusterings[j];
                rank = clust.getParams().getDouble(RANK_PROP, 0);
                rank += 1.0 / (EPS + clusterings.length - j);
                clust.getParams().putDouble(RANK_PROP, rank);
            }
        }

        //sort by aggregated rank
        Arrays.parallelSort(clusterings, comp);

        return clusterings;
    }

    @Override
    public boolean isMultiObjective() {
        return false;
    }

    @Override
    public RankComparator getComparator() {
        return comp;
    }

    @Override
    public ClusterEvaluation<E, C> getEvaluator() {
        return comp;
    }

    @Override
    public void validate(List<ClusterEvaluation<E, C>> objectives) throws ConfigException {
        if (objectives.size() < 2) {
            throw new ConfigException("Please provide at least three evaluation metrics. " + objectives.size() + " was given");
        }
    }

}
