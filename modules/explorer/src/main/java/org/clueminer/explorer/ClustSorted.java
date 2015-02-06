package org.clueminer.explorer;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionListener;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.api.Pair;
import org.clueminer.evolution.api.Population;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 * Doesn't work with Children.SortedMap<>
 *
 * @author Tomas Barton
 */
public class ClustSorted extends Children.SortedArray implements EvolutionListener {

    private Lookup.Result<Clustering> result;
    private static final Logger logger = Logger.getLogger(ClustSorted.class.getName());
    private final Object2IntOpenHashMap<ClusteringNode[]> map = new Object2IntOpenHashMap<>();
    //private Set<Clustering> all = new HashSet<Clustering>(5);

    public ClustSorted() {

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
        logger.log(Level.INFO, "best in generation {0}: {1} ext: {2}", new Object[]{generationNum, population.getAvgFitness(), external});
        addClustering(population.getBestIndividual().getClustering());
    }

    public void addClustering(Clustering<? extends Cluster> clustering) {
        final ClusteringNode[] nodesAry = new ClusteringNode[1];
        nodesAry[0] = new ClusteringNode((Clustering<Cluster>) clustering);
        map.put(nodesAry, clustering.hashCode());

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
        //worst case hash set size
        ObjectOpenHashSet<Clustering> toKeep = new ObjectOpenHashSet<>(result.length);
        int hash;

        Clustering<? extends Cluster> c;
        for (Individual ind : result) {
            c = ind.getClustering();
            hash = c.hashCode();
            //new clustering
            if (!map.containsValue(hash)) {
                addClustering(c);
            }
            if (!toKeep.contains(c)) {
                toKeep.add(c);
            }
        }
        System.out.println("map: " + map.size() + ", to keep: " + toKeep.size());
        //go through all current nodes and remove old nodes
    /*    ObjectOpenHashSet<ClusteringNode[]> toRemove = new ObjectOpenHashSet<>(result.length);
         for (final ClusteringNode[] n : map.keySet()) {
         if (!toKeep.contains(n[0].getClustering())) {
         System.out.println("want to remove: " + n[0].getClustering().getName() + " precision " + n[0].evaluationTable(n[0].getClustering()).getScore("Precision"));
         toRemove.add(n);
         }
         }
         for (final ClusteringNode[] n : toRemove) {
         SwingUtilities.invokeLater(new Runnable() {

         @Override
         public void run() {
         remove(n);
         }
         });
         map.remove(n);
         }*/

    }

    public void clearAll() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                for (final ClusteringNode[] n : map.keySet()) {
                    remove(n);
                }
                map.clear();
            }
        });
    }

}
