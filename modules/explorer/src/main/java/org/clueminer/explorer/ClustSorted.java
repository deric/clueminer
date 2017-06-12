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
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionListener;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.api.Pair;
import org.clueminer.evolution.api.Population;
import org.clueminer.evolution.hac.SimpleIndividual;
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
    private Evolution evolution;

    public ClustSorted() {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        project = pc.getCurrentProject();

    }

    @Override
    public void started(Evolution evolution) {
        this.evolution = evolution;
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
        Individual ind = population.getBestIndividual();
        addClustering(ind.getClustering(), ind);
    }

    public void addUniqueClustering(Clustering<E, C> clustering, Individual ind) {
        int hash = clustering.hashCode();
        //new clustering
        if (!map.containsValue(hash)) {
            addClustering(clustering, ind);
        } else {
            LOG.info("ignoring {} clust: {}", hash, clustering.getName());
        }
    }

    /**
     * Individual is used when result was produced using some explorative method
     *
     * @param clustering
     * @param ind in case of evolution/automl
     */
    public void addClustering(Clustering<E, C> clustering, Individual ind) {
        final ClusteringNode[] nodesAry = new ClusteringNode[1];
        nodesAry[0] = new ClusteringNode<>((Clustering<E, C>) clustering);
        map.put(nodesAry, clustering.hashCode());
        project.add(clustering);
        nodesAry[0].setIndividual(ind);
        nodesAry[0].setParent(this);
        nodesAry[0].createSheet();

        SwingUtilities.invokeLater(() -> {
            add(nodesAry);
        });
    }

    @Override
    public void finalResult(Evolution evolution, int g, Individual best, Pair<Long, Long> time, Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness, double external) {
        //nothing to do
    }

    @Override
    public void resultUpdate(Individual[] result, boolean isExplicit) {
        LOG.debug("received {} new individuals", result.length);
        //worst case hash set size
        ObjectOpenHashSet<Clustering> toKeep = new ObjectOpenHashSet<>(result.length);

        Clustering<E, C> c;
        Dataset<E> d;
        for (Individual ind : result) {
            c = ind.getClustering();
            d = c.getLookup().lookup(Dataset.class);
            if (c.size() == 1) {
                LOG.debug("ignoring invalid clustering with single cluster, params: {}", c.getParams());
            } else if (c.instancesCount() != d.size()) {
                LOG.debug("ignoring incomplete clustering {}, params: {}", c.fingerprint(), c.getParams());
            } else {
                //TODO: Disabled duplicity filtering. Should be done is search algorithms?
                //addUniqueClustering(c, ind);
                addClustering(c, ind);
                if (!toKeep.contains(c)) {
                    toKeep.add(c);
                }
            }
        }
        if (isExplicit) {
            LOG.debug("map: {}, to keep: {}", map.size(), toKeep.size());
            //go through all current nodes and remove old nodes
            ObjectOpenHashSet<ClusteringNode[]> toRemove = new ObjectOpenHashSet<>(result.length);
            for (final ClusteringNode[] n : map.keySet()) {
                if (!toKeep.contains(n[0].getClustering())) {
                    LOG.debug("want to remove: {} ext. score: {}", n[0].getClustering().getName(), n[0].evaluationTable(n[0].getClustering()).getScore("NMI-sqrt"));
                    toRemove.add(n);
                }
            }
            for (final ClusteringNode[] n : toRemove) {
                SwingUtilities.invokeLater(() -> {
                    remove(n);
                });
                for (ClusteringNode cn : n) {
                    map.remove(cn, cn.getClustering().hashCode());
                }
            }
        }
    }

    public void clearAll() {
        SwingUtilities.invokeLater(() -> {
            for (final ClusteringNode[] n : map.keySet()) {
                remove(n);
                project.remove(n[0].getClustering());
            }
            map.clear();
        });
    }

    public void propertiesComputed(Clustering<E, C> clustering, Individual individual) {
        if (evolution != null) {
            if (individual == null) {
                individual = new SimpleIndividual(clustering);
            }
            evolution.fireIndividualCreated(individual);
        }
    }

}
