package org.clueminer.distance;

import org.clueminer.distance.api.AbstractDistance;
import org.clueminer.math.Matrix;
import org.clueminer.math.Vector;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = AbstractDistance.class)
public class KendalsDistance extends AbstractDistance {

    private static final String name = "Kendals Tau";
    private static float similarityFactor = -1.0f;
    private static int offset = -1; //should be minNodeHeight
    private static final long serialVersionUID = 4874965236165548677L;

    @Override
    public String getName() {
        return name;
    }

    /**
     * Calculate distance between 2 columns in given matrix
     *
     * @param matrix
     * @param col1
     * @param col2
     * @return
     */
    public double columns(Matrix matrix, int e1, int e2) {
        double TINY = Double.MIN_VALUE;
        int n = matrix.rowsCount();
        int n2 = 0;
        int n1 = 0;
        int is = 0;
        double aa, a2, a1;
        for (int j = 0; j < n - 1; j++) {               //Loop over rst member of pair,
            for (int k = (j + 1); k < n; k++) {         //and second member.
                a1 = matrix.get(j, e1) - matrix.get(k, e1);
                a2 = matrix.get(j, e2) - matrix.get(k, e2);
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

    public double rows(Matrix A, Matrix B, int g1, int g2) {
        double TINY = Double.MIN_VALUE;
        int n = A.columnsCount();
        int n2 = 0;
        int n1 = 0;
        int is = 0;
        double aa, a2, a1;
        for (int j = 0; j < n - 1; j++) {               //Loop over rst member of pair,
            for (int k = (j + 1); k < n; k++) {         //and second member.
                a1 = A.get(g1, j) - A.get(g1, k);
                a2 = B.get(g2, j) - B.get(g2, k);
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

    @Override
    public float getSimilarityFactor() {
        return similarityFactor;
    }

    @Override
    public int getNodeOffset() {
        return offset;
    }

    @Override
    public boolean useTreeHeight() {
        return true;
    }

    @Override
    public boolean compare(double x, double y) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSubadditive() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isIndiscernible() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
