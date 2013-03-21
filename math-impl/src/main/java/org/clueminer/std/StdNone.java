package org.clueminer.std;

import org.clueminer.math.Standardisation;
import org.openide.util.lookup.ServiceProvider;

/**
 * Does not standardise at all
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Standardisation.class)
public class StdNone extends Standardisation {

    private static String name = "None";

    @Override
    public String getName() {
        return name;
    }

    /**
     * Sometimes the best thing you can do is nothing. And that's exactly what
     * we're doing here.
     *
     * @param a
     * @param m
     * @param n
     * @return
     */
    @Override
    public double[][] optimize(double[][] a, int m, int n) {
        return a;
    }
}
