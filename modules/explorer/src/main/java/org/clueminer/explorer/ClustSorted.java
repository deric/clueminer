package org.clueminer.explorer;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.clustering.api.evolution.EvolutionListener;
import org.clueminer.clustering.api.evolution.Individual;
import org.clueminer.clustering.api.evolution.Pair;
import org.clueminer.clustering.api.evolution.Population;
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
        final ClusteringNode[] nodesAry = new ClusteringNode[1];
        nodesAry[0] = new ClusteringNode(population.getBestIndividual().getClustering());

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                add(nodesAry);
            }
        });

    }

    public void addClustering(Clustering<? extends Cluster> clustering) {
        final ClusteringNode[] nodesAry = new ClusteringNode[1];
        nodesAry[0] = new ClusteringNode((Clustering<Cluster>) clustering);

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                add(nodesAry);
            }
        });
    }

    @Override
    public void finalResult(Evolution evolution, int g, Individual best, Pair<Long, Long> time, Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness, double external) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resultUpdate(Collection<Clustering<? extends Cluster>> result) {
        //TODO: implement
    }

}
