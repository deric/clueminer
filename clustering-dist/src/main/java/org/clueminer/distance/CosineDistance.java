package org.clueminer.distance;

import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.distance.api.SymmetricDistance;
import org.clueminer.math.Matrix;
import org.clueminer.math.Vector;
import org.openide.util.lookup.ServiceProvider;

/**
 * Cosine distance express angle between two vectors. Normally it ranges from -1
 * to 1 but we shift it to [0, 2]. Where 0 means two vectors are the same, and 2
 * means they are completely different.
 *
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
@ServiceProvider(service = DistanceMeasure.class)
public class CosineDistance extends SymmetricDistance {

    private static final String name = "Cosine";
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
     * @param e1
     * @param e2
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

        /*
         * a dot b / (2Norm(a) * 2Norm(b)) will return a value in the range -1 to 1
         * -1 means they are completly opposite
         * 1 means they are exactly the same
         *
         * by returning the result a 1 - val, we mak it so the value returns is in the range 2 to 0.
         * 2 (1 - -1 = 2) means they are completly opposite
         * 0 ( 1 -1) means they are completly the same
         */
        double denom = x.pNorm(2) * y.pNorm(2);
        if (denom == 0) {
            return 2.0;
        }
        return 1 - x.dot(y) / denom;

        /*   double sumTop = 0;
         double sumOne = 0;
        double sumTwo = 0;
        for (int i = 0; i < x.size(); i++) {
            sumTop += x.get(i) * y.get(i);
            sumOne += x.get(i) * x.get(i);
            sumTwo += y.get(i) * y.get(i);
        }
        double cosSim = sumTop / (Math.sqrt(sumOne) * Math.sqrt(sumTwo));
        return (1 - cosSim);*/
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
        return 0.0;
    }

    @Override
    public double getMaxValue() {
        return 2.0;
    }

    @Override
    public boolean isSubadditive() {
        return true;
    }

    @Override
    public boolean isIndiscernible() {
        return true;
    }
}
