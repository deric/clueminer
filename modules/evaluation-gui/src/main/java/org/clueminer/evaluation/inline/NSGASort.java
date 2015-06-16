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
package org.clueminer.evaluation.inline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;

/**
 *
 * @author deric
 */
public class NSGASort {

    private final DominanceComparator comparator;

    public NSGASort() {
        this.comparator = new DominanceComparator();
    }

    public void sort(Clustering[] clusterings, List<ClusterEvaluation> objectives) {

        int n = clusterings.length;
        List<ArrayList<Clustering>> rankedSubpopulations;

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
        // Contribution of Guillaume Jacquenot
        for (int p = 0; p < n; p++) {
            // Initialize the list of individuals that i dominate and the number
            // of individuals that dominate me
            iDominate[p] = new LinkedList<>();
            dominateMe[p] = 0;
        }

        int flagDominate;
        for (int p = 0; p < (n - 1); p++) {
            // For all q individuals , calculate if p dominates q or vice versa
            for (int q = p + 1; q < n; q++) {
                flagDominate = comparator.compare(clusterings[p], clusterings[q], objectives);
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
            rankedSubpopulations.add(j, new ArrayList<Clustering>(front[j].size()));
            it1 = front[j].iterator();
            while (it1.hasNext()) {
                rankedSubpopulations.get(j).add(clusterings[it1.next()]);
            }
        }

    }

}
