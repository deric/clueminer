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
package org.clueminer.explorer;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionListener;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.api.Pair;
import org.clueminer.evolution.api.Population;
import org.clueminer.project.api.Project;
import org.clueminer.project.api.ProjectController;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Doesn't work with Children.SortedMap<>
 *
 * @author Tomas Barton
 * @param <E> data row representation
 * @param <C> cluster structure
 */
public class ClustSorted<E extends Instance, C extends Cluster<E>> extends Children.SortedArray implements EvolutionListener {

    //private Lookup.Result<Clustering> result;
    private static final Logger LOG = LoggerFactory.getLogger(ClustSorted.class);
    private final Object2IntOpenHashMap<ClusteringNode[]> map = new Object2IntOpenHashMap<>();
    private final Project project;

    public ClustSorted() {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        project = pc.getCurrentProject();

    }

    @Override
    public void started(Evolution evolution) {
    }

    @Override
    protected void addNotify() {

    }

    /**
     *
     * @param generationNum
     * @param external
     */
    @Override
    public void bestInGeneration(int generationNum, Population<? extends Individual> population, double external) {
        LOG.info("best in generation {}: {} ext: {}", generationNum, population.getAvgFitness(), external);
        addClustering(population.getBestIndividual().getClustering());
    }

    public void addUniqueClustering(Clustering<E, C> clustering) {
        int hash = clustering.hashCode();
        //new clustering
        if (!map.containsValue(hash)) {
            addClustering(clustering);
        } else {
            LOG.info("ignoring {} clust: {}", hash, clustering.getName());
        }
    }

    public void addClustering(Clustering<E, C> clustering) {
        final ClusteringNode[] nodesAry = new ClusteringNode[1];
        nodesAry[0] = new ClusteringNode<>((Clustering<E, C>) clustering);
        map.put(nodesAry, clustering.hashCode());
        project.add(clustering);
        nodesAry[0].createSheet();

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                add(nodesAry);
            }
        });
    }

    @Override
    public void finalResult(Evolution evolution, int g, Individual best, Pair<Long, Long> time, Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness, double external) {
        //nothing to do
    }

    @Override
    public void resultUpdate(Individual[] result) {
        LOG.debug("received {} new individuals", result.length);
        //worst case hash set size
        ObjectOpenHashSet<Clustering> toKeep = new ObjectOpenHashSet<>(result.length);
        int hash;

        Clustering<E, C> c;
        for (Individual ind : result) {
            c = ind.getClustering();
            addUniqueClustering(c);
            if (!toKeep.contains(c)) {
                toKeep.add(c);
            }
        }
        System.out.println("map: " + map.size() + ", to keep: " + toKeep.size());
        //go through all current nodes and remove old nodes
        /* ObjectOpenHashSet<ClusteringNode[]> toRemove = new ObjectOpenHashSet<>(result.length);
         * for (final ClusteringNode[] n : map.keySet()) {
         * if (!toKeep.contains(n[0].getClustering())) {
         * System.out.println("want to remove: " + n[0].getClustering().getName() + " precision " + n[0].evaluationTable(n[0].getClustering()).getScore("Precision"));
         * toRemove.add(n);
         * }
         * }
         * for (final ClusteringNode[] n : toRemove) {
         * SwingUtilities.invokeLater(new Runnable() {
         *
         * @Override
         * public void run() {
         * remove(n);
         * }
         * });
         * map.remove(n);
         * } */

    }

    public void clearAll() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                for (final ClusteringNode[] n : map.keySet()) {
                    remove(n);
                    project.remove(n[0].getClustering());
                }
                map.clear();
            }
        });
    }

}
