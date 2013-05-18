package org.clueminer.evaluation.external;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
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


  /*  public void calculate(Clustering clusters, Dataset dataset) {
        if (clusters.size() == 0) {
            return;
        }
        double normalizedMutualInformation = 0.0;

        final Collection<Integer> partitionSizes = Maps.transformValues(
                documentsByPartition.asMap(), new Function<Collection<Document>, Integer>() {
            public Integer apply(Collection<Document> documents) {
                return documents.size();
            }
        }).values();
        double partitionEntropy = entropy(dataset.size(), partitionSizes.toArray(new Integer[partitionSizes.size()]));

        final List<Integer> clusterSizes = Lists.transform(clusters,
                new Function<Cluster, Integer>() {
            public Integer apply(Cluster cluster) {
                return cluster.size();
            }
        });
        double clusterEntropy = entropy(documentCount, clusterSizes
                .toArray(new Integer[clusterSizes.size()]));

        double mutualInformation = 0;
        for (Cluster cluster : this.clusters) {
            final int clusterSize = cluster.size();
            for (Object partition : partitions) {
                final List<Document> clusterDocuments = cluster.getAllDocuments();
                if (cluster.isOtherTopics() || clusterDocuments.size() == 0) {
                    continue;
                }

                final Set<Document> commonDocuments = Sets.newHashSet(documentsByPartition.get(partition));
                commonDocuments.retainAll(clusterDocuments);
                int commonDocumentsCount = commonDocuments.size();

                if (commonDocumentsCount != 0) {
                    mutualInformation += (commonDocumentsCount / (double) documentCount)
                            * Math.log(documentCount
                            * commonDocumentsCount
                            / (double) (clusterSize * documentCountByPartition
                            .get(partition)));
                }
            }
        }

        normalizedMutualInformation = mutualInformation
                / ((clusterEntropy + partitionEntropy) / 2);
    }*/

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

    @Override
    public double score(Clustering clusters, Dataset dataset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
