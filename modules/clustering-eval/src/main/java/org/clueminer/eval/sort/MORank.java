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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.Rank;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.clustering.api.config.ConfigException;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.external.NMIsqrt;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sort an array using multiple objectives (at least 3 are needed)
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = Rank.class)
public class MORank<E extends Instance, C extends Cluster<E>> implements Rank<E, C> {

    private static final String NAME = "MO Rank";
    public static final String PROP_RANK = "mo-order";
    private final MoEvaluator comp = new MoEvaluator();
    private static final Logger LOG = LoggerFactory.getLogger(MORank.class);

    @Override
    public String getName() {
        return NAME;
    }

    public Clustering<E, C>[] sort(Clustering<E, C>[] clusterings, List<ClusterEvaluation<E, C>> objectives) {
        //last objective is used for front sorting
        ClusterEvaluation<E, C> sortObj = objectives.get(objectives.size() - 1);
        List<ClusterEvaluation<E, C>> moObj = new LinkedList<>();

        validate(objectives);
        for (int i = 0; i < objectives.size() - 1; i++) {
            moObj.add(objectives.get(i));
        }

        List<ArrayList<Clustering<E, C>>> fronts = computeRankings(clusterings, moObj, sortObj);

        //java's "natural" sorting starting from smallest value
        //should be compatible with Arrays.sort()
        // flatten array lists
        Clustering[] result = new Clustering[clusterings.length];
        int k = 0;
        Clustering clust;
        ArrayList<Clustering<E, C>> front;
        //best solution is the at the end of the array
        for (int i = 0; i < fronts.size(); i++) {
            //start from last front
            front = fronts.get(fronts.size() - 1 - i);
            for (int j = 0; j < front.size(); j++) {
                clust = front.get(j);
                clust.getParams().put(PROP_RANK, result.length - k - 1);
                result[k++] = clust;
            }
        }

        //printFronts(fronts);
        //printFlatten(result);
        return result;
    }

    protected List<ArrayList<Clustering<E, C>>> computeRankings(Clustering<E, C>[] clusterings,
            List<ClusterEvaluation<E, C>> moObj, ClusterEvaluation<E, C> sortObj) {
        comp.setObjectives(moObj);
        int n = clusterings.length;
        List<ArrayList<Clustering<E, C>>> rankedSubpopulations;

        // dominateMe[i] contains the number of solutions dominating i
        int[] dominateMe = new int[n];

        // iDominate[k] contains the list of solutions dominated by k
        List<Integer>[] iDominate = new List[n];

        // front[i] contains the list of individuals belonging to the front i
        List<Integer>[] front = new List[n + 1];

        // Initialize the fronts
        for (int i = 0; i < front.length; i++) {
            front[i] = new LinkedList<>();
        }

        // Fast non dominated sorting algorithm
        for (int p = 0; p < n; p++) {
            // Initialize the list of individuals that i dominate and the number
            // of individuals that dominate me
            iDominate[p] = new LinkedList<>();
            dominateMe[p] = 0;
        }
        DominanceComparator comparator = new DominanceComparator(moObj);

        int flagDominate;
        for (int p = 0; p < (n - 1); p++) {
            // For all q individuals , calculate if p dominates q or vice versa
            for (int q = p + 1; q < n; q++) {
                flagDominate = comparator.compare(clusterings[p], clusterings[q]);
                if (flagDominate == -1) {
                    iDominate[p].add(q);
                    dominateMe[q]++;
                } else if (flagDominate == 1) {
                    iDominate[q].add(p);
                    dominateMe[p]++;
                }
            }
        }

        for (int i = 0; i < n; i++) {
            if (dominateMe[i] == 0) {
                front[0].add(i);
                //RankingAndCrowdingAttr.getAttributes(solutionSet.get(0)).setRank(0);
                clusterings[i].getParams().put("rank", 0);
            }
        }

        //Obtain the rest of fronts
        int i = 0;
        Iterator<Integer> it1, it2; // Iterators
        while (!front[i].isEmpty()) {
            i++;
            it1 = front[i - 1].iterator();
            while (it1.hasNext()) {
                it2 = iDominate[it1.next()].iterator();
                while (it2.hasNext()) {
                    int index = it2.next();
                    dominateMe[index]--;
                    if (dominateMe[index] == 0) {
                        front[i].add(index);
                        //RankingAndCrowdingAttr.getAttributes(solutionSet.get(index)).setRank(i);
                        clusterings[index].getParams().put("rank", i);
                    }
                }
            }
        }

        rankedSubpopulations = new ArrayList<>();
        //0,1,2,....,i-1 are fronts, then i fronts
        for (int j = 0; j < i; j++) {
            rankedSubpopulations.add(j, new ArrayList<>(front[j].size()));
            it1 = front[j].iterator();
            while (it1.hasNext()) {
                rankedSubpopulations.get(j).add(clusterings[it1.next()]);
            }
            //sort front
            if (rankedSubpopulations.get(j).size() > 1) {
                try {
                    Collections.sort(rankedSubpopulations.get(j), sortObj);
                } catch (IllegalArgumentException ex) {
                    LOG.error("failed to sort using {}", sortObj.getName(), ex);
                }
            }
        }
        return rankedSubpopulations;
    }

    @Override
    public boolean isMultiObjective() {
        return true;
    }

    @Override
    public Comparator<Clustering<E, C>> getComparator() {
        return (Comparator<Clustering<E, C>>) comp;
    }

    @Override
    public ClusterEvaluation<E, C> getEvaluator() {
        return comp;
    }

    @Override
    public void validate(List<ClusterEvaluation<E, C>> objectives) throws ConfigException {
        if (objectives.size() < 3) {
            throw new ConfigException("Please provide at least two evaluation metrics. " + objectives.size() + " was given");
        }
    }

    @Override
    public int getMinObjectives() {
        return 3;
    }

    protected void printFronts(List<ArrayList<Clustering<E, C>>> fronts) {
        StringBuilder sb = new StringBuilder("mo-ranking [\n");
        ClusterEvaluation eval = new NMIsqrt();
        for (int i = 0; i < fronts.size(); i++) {
            if (i > 0) {
                sb.append(",\n");
            }
            ArrayList<Clustering<E, C>> front = fronts.get(i);
            sb.append("\t#").append(i).append("(").append(front.size()).append(") [");

            //fronts are sorted from worst value
            for (int j = front.size() - 1; j >= 0; j--) {
                try {
                    if (j < front.size() - 1) {
                        sb.append(",");
                    }
                    sb.append(front.get(j).fingerprint()).append(" = ").append(eval.score(front.get(j)));
                } catch (ScoreException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            sb.append("]");
        }
        sb.append("\n]");
        System.out.println(sb.toString());
    }

    protected void printFlatten(Clustering<E, C>[] res) {
        StringBuilder sb = new StringBuilder("mo-ranking [\n");
        ClusterEvaluation eval = new NMIsqrt();
        for (int i = 0; i < res.length; i++) {
            try {
                if (i > 0) {
                    sb.append(",\n");
                }
                int rank = res[i].getParams().getInt(PROP_RANK);
                sb.append("\t#").append(rank).append(" @ ").append(res[i].getParams().getInt("rank")).append("(")
                        .append(res[i].fingerprint()).append(") = ")
                        .append(eval.score(res[i]));

                sb.append("]");
            } catch (ScoreException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        sb.append("\n]");
        System.out.println(sb.toString());
    }

}
