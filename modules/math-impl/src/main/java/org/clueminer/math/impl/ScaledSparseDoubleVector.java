package org.clueminer.math.impl;

import org.clueminer.math.SparseDoubleVector;

/**
 * A decorator for {@link SparseDoubleVector}s that scales every value in a
 * given {@link DoubleVector} by some non zero scale.
 *
 * </p>
 *
 * Note that this automatically computes the scaling of a {@link
 * ScaledDoubleVector} so that backing vector is scaled only once, thus
 * preventing any recursive calls to scaling.
 *
 * @author Keith Stevens
 */
public class ScaledSparseDoubleVector extends ScaledDoubleVector implements SparseDoubleVector {

    /**
    * The original vector.
    */
    private SparseDoubleVector vector;

    /**
    * Creates a new {@link ScaledSparseDoubleVector} that decorates a given
    * {@link SparseDoubleVector} by scaling each value in {@code vector} by
    * {@code scale}.
    */
    public ScaledSparseDoubleVector(SparseDoubleVector vector, double scale) {
        super(vector, scale);

        // If the vector we are to orthonormalize is already scaled, get its
        // backing data and create a new instance that is rescaled by the
        // product of both scalars.  This avoids unnecessary recursion to
        // multiply all the values together for heavily scaled vectors.
        if (vector instanceof ScaledSparseDoubleVector) {
            ScaledSparseDoubleVector ssdv = (ScaledSparseDoubleVector) vector;
            this.vector = ssdv.vector;
        } else {
            this.vector = vector;
        }
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int[] getNonZeroIndices() {
        return vector.getNonZeroIndices();
    }
}
