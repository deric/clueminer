package org.clueminer.distance;

import org.clueminer.distance.api.AbstractDistance;
import org.clueminer.distance.api.SymmetricDistance;
import org.clueminer.math.Matrix;
import org.clueminer.math.Vector;
import org.openide.util.lookup.ServiceProvider;

/**
 * Cosine distance express angle between two vectors. Normally it ranges from -1
 * to 1 but we shift it to [0, 2]
 *
 * <math>\text{similarity} = \cos(\theta) = {A \cdot B \over \|A\| \|B\|} =
 * \frac{ \sum\limits_{i=1}^{n}{A_i \times B_i} }{
 * \sqrt{\sum\limits_{i=1}^{n}{(A_i)^2}} \times
 * \sqrt{\sum\limits_{i=1}^{n}{(B_i)^2}} }</math>
 *
 * @see http://en.wikipedia.org/wiki/Cosine_similarity
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = AbstractDistance.class)
public class CosineDistance extends SymmetricDistance {

    private static String name = "Cosine";
    private static float similarityFactor = 1.0f;
    /**
     * should be minNodeHeight - when computing tree heights, the distances must
     * be positive, therefore we have to add the minimal distance
     */
    private static int offset = 0;
    private static final long serialVersionUID = 4102385223200185396L;

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
    @Override
    public double columns(Matrix matrix, int e1, int e2) {
        int j, k;
        double sxy = 0.0;
        double sxx = 0.0;
        double syy = 0.0;
        double tx;
        double ty;
        k = matrix.rowsCount();
        for (j = 0; j < k; j++) {
            if ((!Double.isNaN(matrix.get(j, e1))) && (!Double.isNaN(matrix.get(j, e2)))) {
                tx = matrix.get(j, e1);
                ty = matrix.get(j, e2);
                sxy += tx * ty;
                sxx += tx * tx;
                syy += ty * ty;
            }
        }
        return (1 - (sxy / (Math.sqrt(sxx) * Math.sqrt(syy))));
    }

    @Override
    public double rows(Matrix A, Matrix B, int e1, int e2) {
        double sxy = 0.0;
        double sxx = 0.0;
        double syy = 0.0;
        double tx;
        double ty;
        int k = A.columnsCount();
        int j;
        for (j = 0; j < k; j++) {
            if ((!Double.isNaN(A.get(e1, j))) && (!Double.isNaN(B.get(e2, j)))) {
                tx = A.get(e1, j);
                ty = B.get(e2, j);
                sxy += tx * ty;
                sxx += tx * tx;
                syy += ty * ty;
            }
        }
        return (1 - (sxy / (Math.sqrt(sxx) * Math.sqrt(syy))));
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

    /**
     * The value returned lies in the interval [0,2].
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public double measure(Vector<Double> x, Vector<Double> y) {
        checkInput(x, y);
        double sumTop = 0;
        double sumOne = 0;
        double sumTwo = 0;
        for (int i = 0; i < x.size(); i++) {
            sumTop += x.get(i) * y.get(i);
            sumOne += x.get(i) * x.get(i);
            sumTwo += y.get(i) * y.get(i);
        }
        double cosSim = sumTop / (Math.sqrt(sumOne) * Math.sqrt(sumTwo));
        return (1 - cosSim);
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y, double[] weights) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
}
