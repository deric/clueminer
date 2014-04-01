package org.clueminer.clustering.seed;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.clueminer.clustering.api.SeedSelection;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public class RandomMedoidsSelection extends AbstractSelection implements SeedSelection {

    private static final String name = "random selection";

    @Override
    public String getName() {
        return name;
    }

    /**
     * Select k random indexes from dataset <0, |dataset|>
     *
     * @param dataset
     * @param k
     * @return
     */
    @Override
    public int[] selectIntIndices(Dataset<? extends Instance> dataset, int k) {
        //select k medoids
        int[] indices = new int[k];
        IntSet indicies = new IntOpenHashSet(k);
        int index;

        if (k > dataset.size()) {
            throw new RuntimeException("k (= " + k + ") can't be bigger that |dataset| == " + dataset.size());
        }

        //Keep sampling, we can't use the same point twice.
        while (indicies.size() < k) {
            index = rand.nextInt(dataset.size());
            //TODO create method to do uniform sampling for a select range
            indicies.add(index);
        }

        int j = 0;
        for (Integer i : indicies) {
            indices[j++] = i;
        }
        return indices;
    }

}
