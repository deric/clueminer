package org.clueminer.distance;

import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.distance.api.SymmetricDistance;
import org.clueminer.math.Matrix;
import org.clueminer.math.Vector;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * CorrelationDistance[u,v] is equivalent to
 * 1-(u-Mean[u]).(v-Mean[v])/(Norm[u-Mean[u]]Norm[v-Mean[v]]).
 *
 * @see http://reference.wolfram.com/language/ref/CorrelationDistance.html
 * @author Tomas Barton
 */
@ServiceProvider(service = DistanceMeasure.class)
public class CorrelationDistance extends SymmetricDistance implements DistanceMeasure {

    private static final String name = "Correlation";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getSimilarityFactor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNodeOffset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double rows(Matrix a, Matrix b, int i, int j) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double columns(Matrix a, int i, int j) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Correlation distance is equal to CosineDistance(x - mean[x], y - mean[y])
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public double measure(Vector<Double> x, Vector<Double> y) {
        checkInput(x, y);
        double sumX = 0.0, sumY = 0.0, meanX, meanY;

        for (int i = 0; i < x.size(); i++) {
            sumX += x.get(i);
            sumY += y.get(i);
        }
        meanX = sumX / x.size();
        meanY = sumY / y.size();

        Vector<Double> u = x.minus(meanX);
        Vector<Double> v = y.minus(meanY);

        //rest is same as Cosine distance
        double denom = u.pNorm(2) * v.pNorm(2);
         if (denom == 0) {
             return 0.0;
        }
        return 1 - u.dot(v) / denom;
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y, double[] weights) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSubadditive() {
        return true;
    }

    @Override
    public boolean isIndiscernible() {
        return false;
    }

}
