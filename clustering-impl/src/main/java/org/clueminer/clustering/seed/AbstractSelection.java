package org.clueminer.clustering.seed;

import java.util.Random;

/**
 *
 * @author Tomas Barton
 */
public class AbstractSelection {

    protected Random rand = new Random();

    public void setRandom(Random rand) {
        this.rand = rand;
    }

    public Random getRandom() {
        return rand;
    }

}
