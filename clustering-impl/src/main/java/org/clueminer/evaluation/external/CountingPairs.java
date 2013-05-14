package org.clueminer.evaluation.external;

import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public class CountingPairs {

    /**
     * number of pairs of points with the same label in C and assigned to the
     * same cluster in K
     */
    protected int a = 0;
    /**
     * the number of pairs with the same label, but in different clusters
     */
    protected int b = 0;
    /**
     * number of pairs in the same cluster, but with different class labels
     */
    protected int c = 0;
    /**
     * number of pairs with different label and different cluster
     */
    protected int d = 0;

    /**
     * this looks at every pair of points and determines if the labels and the
     * calculated clusters are equal or different. based on the result the
     * instance variables a b c or d are incremented
     *
     * @param dataset
     */
/*   protected void countPairs(Clustering clustering) {

        int datasetSize = clustering.instancesCount();
        Instance point1;
        Instance point2;
        for (int i = 0; i < datasetSize - 1; i++) {
            for (int j = i + 1; j < datasetSize; j++) {
                point1 = clustering.get(i);
                point2 = clustering.get(j);

                if (point1.getLabel() == point2.getLabel()) {
                    //points have same label 
                    if (point1.getCalculatedClusternumber() == point2.getCalculatedClusternumber()) {
                        //points assigned to same cluster
                        a++;
                    } else {
                        b++;
                    }
                } else {
                    //different label
                    if (point1.getCalculatedClusternumber() == point2.getCalculatedClusternumber()) {
                        //same calculated cluster
                        c++;
                    } else {
                        d++; //not really used to calculate index. just a control variable

                    }
                }
            }
        }
        assert ((a + b + c + d) == datasetSize);

    }*/
}
