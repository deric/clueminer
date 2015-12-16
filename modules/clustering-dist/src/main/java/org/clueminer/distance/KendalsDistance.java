package org.clueminer.distance;

import org.clueminer.distance.api.AbstractDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Vector;
import org.openide.util.lookup.ServiceProvider;

/**
 * The Kendall tau rank distance is a metric that counts the number of pairwise
 * disagreements between two ranking lists
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Distance.class)
public class KendalsDistance extends AbstractDistance {

    private static final String NAME = "Kendals Tau";
    private static final long serialVersionUID = 4874965236165548677L;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y) {
        double TINY = Double.MIN_VALUE;
        int n = x.size();
        int n2 = 0;
        int n1 = 0;
        int is = 0;
        double aa, a2, a1;
        for (int j = 0; j < n - 1; j++) {               //Loop over rst member of pair,
            for (int k = (j + 1); k < n; k++) {         //and second member.
                a1 = x.get(j) - x.get(k);
                a2 = y.get(j) - y.get(k);
                aa = a1 * a2;
                if (aa != 0.0) {                    // Neither array has a tie.
                    ++n1;
                    ++n2;
                    if (aa > 0.0) {
                        ++is;
                    } else {
                        --is;
                    }
                } else {                          // One or both arrays have ties.
                    if (a1 != 0.0) {
                        ++n1;             // An \extra x" event.
                    }
                    if (a2 != 0.0) {
                        ++n2;             // An \extra y" event.
                    }
                }
            }
        }
        return (is / (Math.sqrt((double) n1) * Math.sqrt((double) n2) + TINY));
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y, double[] weights) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * The larger the distance, the more dissimilar the two lists are
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public boolean compare(double x, double y) {
        //number of disagreements is divided by (n1 * n2)
        return x > y;
    }

    @Override
    public double getMinValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getMaxValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isSymmetric() {
        return true;
    }

    @Override
    public boolean isSubadditive() {
        return true;
    }

    @Override
    public boolean isIndiscernible() {
        return false;
    }

    @Override
    public double measure(double[] x, double[] y) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
