package org.clueminer.distance;

import org.clueminer.distance.api.AbstractDistance;
import org.clueminer.math.Matrix;
import org.clueminer.math.MatrixVector;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = AbstractDistance.class)
public class EuclideanDistance extends MinkowskiDistance {
    private static String name = "Euclidean";
    private static float similarityFactor = 1.0f;
    private static int offset = 0;
    private static final long serialVersionUID = 3142545695613722167L;
    
    public EuclideanDistance(){
        this.power = 2;
    }
            
    @Override
    public String getName(){
        return name;
    }
    
    /**
     * Calculate distance between 2 columns in given matrix
     * @param matrix
     * @param col1
     * @param col2
     * @return 
     */
    @Override
    public double columns(Matrix matrix, int col1, int col2) {
        int n = matrix.rowsCount();
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            if ((!Double.isNaN(matrix.get(i, col1))) && (!Double.isNaN(matrix.get(i, col2)))) {
                sum += Math.pow((matrix.get(i, col1) - matrix.get(i, col2)), this.power);
            }
        }
        return (Math.sqrt(sum));
    }

    
    @Override
    public double rows(Matrix A, Matrix B, int e1, int e2) {
        int k = A.columnsCount();
        double sum = 0.0;
        for (int i = 0; i < k; i++) {
            if ((!Double.isNaN(A.get(e1, i))) && (!Double.isNaN(B.get(e2, i)))) {
                sum += Math.pow((A.get(e1, i) - B.get(e2, i)), this.power);
            }
        }
        return (Math.sqrt(sum));
    }
    
    public double vector(MatrixVector A, MatrixVector B) {
        int k = A.size();
        double sum = 0.0;
        for (int i = 0; i < k; i++) {
            if ((!Double.isNaN(A.get(i))) && (!Double.isNaN(B.get(i)))) {
                sum += Math.pow((A.get(i) - B.get(i)), this.power);
            }
        }
        return (Math.sqrt(sum));
    }
    
    
    @Override
    public float getSimilarityFactor() {
        return similarityFactor;
    }

    @Override
    public int getNodeOffset() {
        return offset;
    }
}
