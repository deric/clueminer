package org.clueminer.evaluation.external;

import java.util.Set;
import java.util.TreeSet;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.math.Matrix;

/**
 * Normalized Mutual Information
 *
 * @author Tomas Barton
 */
public class NMI extends ExternalEvaluator {

    private static final long serialVersionUID = -480979241137671097L;
    private static String name = "NMI";

    @Override
    public String getName() {
        return name;
    }

    /**
     *
     * @param count total number of elements N (in whole dataset)
     * @param elements
     * @return
     */
    private double entropy(int count, Integer... elements) {
        double entropy = 0;
        double pk;
        for (int d : elements) {
            if (d != 0) {
                pk = d / (double) count;
                entropy += pk * Math.log(pk);
            }
        }
        return -entropy;
    }

    /**
     * We want to compare two clusterings to evaluate how similar they are
     *
     * @param c1
     * @param c2
     * @return
     */
    public double score(Clustering<Cluster> c1, Clustering<Cluster> c2) {
        double nmi = 0.0;
        if (c1.size() == 0 || c2.size() == 0) {
            return nmi;
        }
        int instancesCnt = c1.instancesCount();
        
        if(c1.instancesCount() != c2.instancesCount()){
            throw new RuntimeException("clusterings have different numbers of instances");
        }
        
        double c1entropy = entropy(c1.instancesCount(), c1.clusterSizes());
        double c2entropy = entropy(c2.instancesCount(), c2.clusterSizes());
        
        double mutualInformation = 0;
        for(Cluster a : c1){
            final int clusterSize = a.size();
            for(Cluster b : c2){
                Set intersect = new TreeSet(c1);
                intersect.retainAll(b);
                int common = intersect.size();//sets intersection
                
                if(common > 0){
                    mutualInformation +=  (common / (double) instancesCnt)
                            * Math.log(instancesCnt
                            * common / (double) (clusterSize * b.size()));
                }                               
            }            
        }
        
        nmi = mutualInformation / ((c1entropy + c2entropy) / 2);

        return nmi;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset) {
        double nmi = 0.0;
        if (clusters.size() == 0) {
            return nmi;
        }
        Integer[] clusterSizes = new Integer[clusters.size()];
        for (int i = 0; i < clusters.size(); i++) {
            clusterSizes[i] = clusters.get(i).size();
        }
        double entropy = entropy(dataset.size(), clusterSizes);



        return nmi;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean compareScore(double score1, double score2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
