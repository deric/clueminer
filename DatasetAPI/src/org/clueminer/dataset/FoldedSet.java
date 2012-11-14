package org.clueminer.dataset;

import java.util.Random;

/**
 *
 * @author Tomas Barton
 */
public interface FoldedSet extends Dataset {

    /**
     * Create a number of folds from the data set and return them. The supplied
     * random generator is used to determine which instances are assigned to
     * each of the folds.
     *
     * @param numFolds - the number of folds to create
     * @param rg - the random generator
     * @return an array of data sets that contains
     * <code>numFolds</code> data sets.
     */
    public Dataset[] folds(int numFolds, Random rg);
}
