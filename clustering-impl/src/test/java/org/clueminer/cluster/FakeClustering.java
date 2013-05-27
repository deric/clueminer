package org.clueminer.cluster;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.attributes.AttributeType;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.exception.UnsupportedAttributeType;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.ARFFHandler;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class FakeClustering {

    private static Clustering<Cluster> irisClusters;
    private static Clustering<Cluster> irisWrong;
    private static Clustering<Cluster> irisWrong2;
    private static Clustering<Cluster> simpleClustering;
    private static Clustering<Cluster> simpleResponse;
    private static Dataset<Instance> irisData;
    private static Dataset<Instance> wine;

    public static Clustering iris() {
        if (irisClusters == null) {
            irisDataset();
            /**
             * fictive clustering, create iris cluster based on class labels
             * (the dataset is sorted)
             */
            irisClusters = new ClusterList(3);
            Cluster a = new BaseCluster(50);
            a.setName("cluster 1");
            a.setAttributes(irisData.getAttributes());
            Cluster b = new BaseCluster(50);
            b.setName("cluster 2");
            b.setAttributes(irisData.getAttributes());
            Cluster c = new BaseCluster(50);
            c.setName("cluster 3");
            c.setAttributes(irisData.getAttributes());
            for (int i = 0; i < 50; i++) {
                a.add(irisData.instance(i));
                b.add(irisData.instance(i + 50));
                c.add(irisData.instance(i + 100));
            }

            irisClusters.add(a);
            irisClusters.add(b);
            irisClusters.add(c);
        }
        return irisClusters;
    }

    public static Dataset<Instance> irisDataset() {
        if (irisData == null) {
            CommonFixture tf = new CommonFixture();
            irisData = new SampleDataset();
            ARFFHandler arff = new ARFFHandler();
            try {
                arff.load(tf.irisArff(), irisData, 4);
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (UnsupportedAttributeType ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return irisData;
    }

    public static Clustering irisWrong() {
        if (irisWrong == null) {
            irisWrong = new ClusterList(3);
            Cluster a = new BaseCluster(50);
            a.setName("cluster 1");
            a.setAttributes(irisData.getAttributes());
            //add few instances to first cluster
            a.add(irisData.instance(0));
            a.add(irisData.instance(1));
            a.add(irisData.instance(2));
            a.add(irisData.instance(149));

            Cluster b = new BaseCluster(50);
            b.setName("cluster 2");
            b.setAttributes(irisData.getAttributes());
            b.add(irisData.instance(3));
            b.add(irisData.instance(4));
            b.add(irisData.instance(5));
            b.add(irisData.instance(6));
            Cluster c = new BaseCluster(50);
            c.setName("cluster 3");
            c.setAttributes(irisData.getAttributes());
            //rest goes to the last cluster
            for (int i = 7; i < 149; i++) {
                c.add(irisData.instance(i));
            }

            irisWrong.add(a);
            irisWrong.add(b);
            irisWrong.add(c);
        }
        return irisWrong;
    }

    /**
     * Pretty bad clustering result, one class contained in two clusters
     *
     * @return
     */
    public static Clustering irisWrong2() {
        if (irisWrong2 == null) {
            irisWrong2 = new ClusterList(3);
            Cluster a = new BaseCluster(50);
            a.setName("cluster 1");
            //will contain 30 elements of first class
            a.setAttributes(irisData.getAttributes());
            for (int i = 0; i < 30; i++) {
                a.add(irisData.instance(i));
            }

            Cluster b = new BaseCluster(50);
            b.setName("cluster 2");
            //will contain 20 elements of first class
            b.setAttributes(irisData.getAttributes());
            for (int i = 30; i < 50; i++) {
                b.add(irisData.instance(i));
            }
            Cluster c = new BaseCluster(50);
            c.setName("cluster 3");
            c.setAttributes(irisData.getAttributes());
            //the rest (100) goes to the last cluster
            for (int i = 50; i < 150; i++) {
                c.add(irisData.instance(i));
            }

            irisWrong2.add(a);
            irisWrong2.add(b);
            irisWrong2.add(c);
        }
        return irisWrong2;
    }

    public static Dataset<Instance> wine() {
        if (wine == null) {

            wine = new SampleDataset(27);
            try {
                wine.setAttribute(0, wine.attributeBuilder().create("x", AttributeType.INTEGER));
            } catch (UnsupportedAttributeType ex) {
                Exceptions.printStackTrace(ex);
            }

            String klass = "cabernet";
            for (int i = 0; i < 12; i++) {
                wine.add(wine.builder().create(new double[]{i}, klass));
            }

            String klass2 = "syrah";
            for (int i = 0; i < 9; i++) {
                wine.add(wine.builder().create(new double[]{i*3+12}, klass2));
            }

            String klass3 = "pinot";
            for (int i = 0; i < 6; i++) {
                wine.add(wine.builder().create(new double[]{i*4+50}, klass3));
            }
        }

        return wine;
    }

    public static Clustering simple() {
        if (simpleClustering == null) {
            try {
                simpleClustering = new ClusterList(3);
                Cluster a = new BaseCluster(12);
                a.setName("cabernet");
                a.setAttribute(0, a.attributeBuilder().create("x", AttributeType.INTEGER));
                Cluster b = new BaseCluster(9);
                b.setName("syrah");
                b.setAttribute(0, b.attributeBuilder().create("x", AttributeType.INTEGER));
                Cluster c = new BaseCluster(6);
                c.setName("pinot");
                c.setAttribute(0, c.attributeBuilder().create("x", AttributeType.INTEGER));

                Dataset<Instance> data = wine();
                for (int i = 0; i < 12; i++) {
                    a.add(data.instance(i));
                }

                for (int i = 12; i < 21; i++) {
                    b.add(data.instance(i));
                }

                for (int i = 21; i < 27; i++) {
                    c.add(data.instance(i));                    
                }

                simpleClustering.add(a);
                simpleClustering.add(b);
                simpleClustering.add(c);
            } catch (UnsupportedAttributeType ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        return simpleClustering;
    }

    /**
     * @see http://alias-i.com/lingpipe/docs/api/com/aliasi/classify/PrecisionRecallEvaluation.html
     * @return 
     */
    public static Clustering simpleResponse() {

        if (simpleResponse == null) {
            try {
                simpleResponse = new ClusterList(3);
                Cluster a = new BaseCluster(12);
                a.setName("cluster A");
                a.setAttribute(0, a.attributeBuilder().create("x", AttributeType.INTEGER));
                Cluster b = new BaseCluster(12);
                b.setName("cluster B");
                b.setAttribute(0, b.attributeBuilder().create("x", AttributeType.INTEGER));

                Cluster c = new BaseCluster(12);
                c.setName("cluster C");
                c.setAttribute(0, c.attributeBuilder().create("x", AttributeType.INTEGER));

                Dataset<Instance> data = wine();
                System.out.println("dataset size "+data.size());
                // cabernet 9x -> a
                for (int i = 0; i < 9; i++) {
                    a.add(data.instance(i));
                    System.out.println("A: " + data.instance(i).classValue());
                }

                // cabernet 2x => b
                for (int i = 9; i < 11; i++) {
                    b.add(data.instance(i));
                }
                // cabernet 1x => c
                c.add(data.instance(11));

                // syrah 2x -> a
                for (int i = 12; i < 14; i++) {
                    a.add(data.instance(i));
                }
                
                // syrah 2x -> c
                for (int i = 14; i < 15; i++) {
                    c.add(data.instance(i));
                }
                a.add(data.instance(15));
                

                // syrah 5x -> b
                for (int i = 16; i < 21; i++) {
                    b.add(data.instance(i));
                    System.out.println("B: " + data.instance(i).classValue());
                }

                // pinot 4x -> c
                for (int i = 21; i < 25; i++) {
                    c.add(data.instance(i));
                }
                
                // pinot -> cabernet cluster
                for (int i = 25; i < 26; i++) {
                    a.add(data.instance(i));                    
                }
                b.add(data.instance(26));

                simpleResponse.add(a);
                simpleResponse.add(b);
                simpleResponse.add(c);
            } catch (UnsupportedAttributeType ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        return simpleResponse;
    }
}
