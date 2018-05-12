package org.clueminer.math.matrix;

import org.clueminer.math.Matrix;
import org.clueminer.math.SingularValueDecomposition;

/**
 * Singular Value Decomposition. <P> For an m-by-n matrix A with m >= n, the
 * singular value decomposition is an m-by-n orthogonal matrix U, an n-by-n
 * diagonal matrix S, and an n-by-n orthogonal matrix V so that A = U*S*V'. <P>
 * The singular values, sigma[k] = S[k][k], are ordered so that sigma[0] >=
 * sigma[1] >= ... >= sigma[n-1]. <P> The singular value decompostion always
 * exists, so the constructor will never fail. The matrix condition number and
 * the effective numerical rank can be computed from this decomposition.
 */
public class SingularValueDecompositionImpl implements SingularValueDecomposition, java.io.Serializable {

    private static final long serialVersionUID = -6181015217774350802L;

    /* ------------------------
     Class variables
     * ------------------------ */
    /**
     * Arrays for internal storage of U and V.
     *
     * @serial internal storage of U.
     * @serial internal storage of V.
     */
    private double[][] U, V;
    /**
     * Array for internal storage of singular values.
     *
     * @serial internal storage of singular values.
     */
    private double[] s;
    /**
     * Row and column dimensions.
     *
     * @serial row dimension.
     * @serial column dimension.
     */
    private int m, n;

    /* ------------------------
     Constructor
     * ------------------------ */
    /**
     * Construct the singular value decomposition
     *
     * @param A Rectangular matrix
     * @return Structure to access U, S and V.
     */
    public SingularValueDecompositionImpl(Matrix Arg) {

        // Derived from LINPACK code.
        // Initialize.
        double[][] A = Arg.getArrayCopy();
        m = Arg.rowsCount();
        n = Arg.columnsCount();

        /* Apparently the failing cases are only a proper subset of (m<n), 
         so let's not throw error.  Correct fix to come later?
         if (m<n) {
         throw new IllegalArgumentException("Jama SVD only works for m >= n"); }
         */
        int nu = Math.min(m, n);
        s = new double[Math.min(m + 1, n)];
        U = new double[m][nu];
        V = new double[n][n];
        double[] e = new double[n];
        double[] work = new double[m];
        boolean wantu = true;
        boolean wantv = true;

        // Reduce A to bidiagonal form, storing the diagonal elements
        // in s and the super-diagonal elements in e.

        int nct = Math.min(m - 1, n);
        int nrt = Math.max(0, Math.min(n - 2, m));
        for (int k = 0; k < Math.max(nct, nrt); k++) {
            if (k < nct) {

                // Compute the transformation for the k-th column and
                // place the k-th diagonal in s[k].
                // Compute 2-norm of k-th column without under/overflow.
                s[k] = 0;
                for (int i = k; i < m; i++) {
                    s[k] = AbstractMatrix.hypot(s[k], A[i][k]);
                }
                if (s[k] != 0.0) {
                    if (A[k][k] < 0.0) {
                        s[k] = -s[k];
                    }
                    for (int i = k; i < m; i++) {
                        A[i][k] /= s[k];
                    }
                    A[k][k] += 1.0;
                }
                s[k] = -s[k];
            }
            for (int j = k + 1; j < n; j++) {
                if ((k < nct) & (s[k] != 0.0)) {

                    // Apply the transformation.

                    double t = 0;
                    for (int i = k; i < m; i++) {
                        t += A[i][k] * A[i][j];
                    }
                    t = -t / A[k][k];
                    for (int i = k; i < m; i++) {
                        A[i][j] += t * A[i][k];
                    }
                }

                // Place the k-th row of A into e for the
                // subsequent calculation of the row transformation.

                e[j] = A[k][j];
            }
            if (wantu & (k < nct)) {

                // Place the transformation in U for subsequent back
                // multiplication.

                for (int i = k; i < m; i++) {
                    U[i][k] = A[i][k];
                }
            }
            if (k < nrt) {

                // Compute the k-th row transformation and place the
                // k-th super-diagonal in e[k].
                // Compute 2-norm without under/overflow.
                e[k] = 0;
                for (int i = k + 1; i < n; i++) {
                    e[k] = AbstractMatrix.hypot(e[k], e[i]);
                }
                if (e[k] != 0.0) {
                    if (e[k + 1] < 0.0) {
                        e[k] = -e[k];
                    }
                    for (int i = k + 1; i < n; i++) {
                        e[i] /= e[k];
                    }
                    e[k + 1] += 1.0;
                }
                e[k] = -e[k];
                if ((k + 1 < m) & (e[k] != 0.0)) {

                    // Apply the transformation.

                    for (int i = k + 1; i < m; i++) {
                        work[i] = 0.0;
                    }
                    for (int j = k + 1; j < n; j++) {
                        for (int i = k + 1; i < m; i++) {
                            work[i] += e[j] * A[i][j];
                        }
                    }
                    for (int j = k + 1; j < n; j++) {
                        double t = -e[j] / e[k + 1];
                        for (int i = k + 1; i < m; i++) {
                            A[i][j] += t * work[i];
                        }
                    }
                }
                if (wantv) {

                    // Place the transformation in V for subsequent
                    // back multiplication.

                    for (int i = k + 1; i < n; i++) {
                        V[i][k] = e[i];
                    }
                }
            }
        }

        // Set up the final bidiagonal matrix or order p.

        int p = Math.min(n, m + 1);
        if (nct < n) {
            s[nct] = A[nct][nct];
        }
        if (m < p) {
            s[p - 1] = 0.0;
        }
        if (nrt + 1 < p) {
            e[nrt] = A[nrt][p - 1];
        }
        e[p - 1] = 0.0;

        // If required, generate U.

        if (wantu) {
            for (int j = nct; j < nu; j++) {
                for (int i = 0; i < m; i++) {
                    U[i][j] = 0.0;
                }
                U[j][j] = 1.0;
            }
            for (int k = nct - 1; k >= 0; k--) {
                if (s[k] != 0.0) {
                    for (int j = k + 1; j < nu; j++) {
                        double t = 0;
                        for (int i = k; i < m; i++) {
                            t += U[i][k] * U[i][j];
                        }
                        t = -t / U[k][k];
                        for (int i = k; i < m; i++) {
                            U[i][j] += t * U[i][k];
                        }
                    }
                    for (int i = k; i < m; i++) {
                        U[i][k] = -U[i][k];
                    }
                    U[k][k] = 1.0 + U[k][k];
                    for (int i = 0; i < k - 1; i++) {
                        U[i][k] = 0.0;
                    }
                } else {
                    for (int i = 0; i < m; i++) {
                        U[i][k] = 0.0;
                    }
                    U[k][k] = 1.0;
                }
            }
        }

        // If required, generate V.

        if (wantv) {
            for (int k = n - 1; k >= 0; k--) {
                if ((k < nrt) & (e[k] != 0.0)) {
                    for (int j = k + 1; j < nu; j++) {
                        double t = 0;
                        for (int i = k + 1; i < n; i++) {
                            t += V[i][k] * V[i][j];
                        }
                        t = -t / V[k + 1][k];
                        for (int i = k + 1; i < n; i++) {
                            V[i][j] += t * V[i][k];
                        }
                    }
                }
                for (int i = 0; i < n; i++) {
                    V[i][k] = 0.0;
                }
                V[k][k] = 1.0;
            }
        }

        // Main iteration loop for the singular values.

        int pp = p - 1;
        int iter = 0;
        double eps = Math.pow(2.0, -52.0);
        double tiny = Math.pow(2.0, -966.0);
        while (p > 0) 
    }

    /* ------------------------
     Public Methods
     * ------------------------ */
    /**
     * Return the left singular vectors
     *
     * @return U
     */
    @Override
    public Matrix getU() {
        return new JamaMatrix(U, m, Math.min(m + 1, n));
    }

    /**
     * Return the right singular vectors
     *
     * @return V
     */
    @Override
    public Matrix getV() {
        return new JamaMatrix(V, n, n);
    }

    /**
     * Return the one-dimensional array of singular values
     *
     * @return diagonal of S.
     */
    @Override
    public double[] getSingularValues() {
        return s;
    }

    /**
     * Return the diagonal matrix of singular values
     *
     * @return S
     */
    @Override
    public Matrix getS() {
        JMatrix X = new JMatrix(n, n);
        double[][] S = X.getArray();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                S[i][j] = 0.0;
            }
            S[i][i] = this.s[i];
        }
        return X;
    }

    /**
     * Two norm
     *
     * @return max(S)
     */
    @Override
    public double norm2() {
        return s[0];
    }

    /**
     * Two norm condition number
     *
     * @return max(S)/min(S)
     */
    @Override
    public double cond() {
        return s[0] / s[Math.min(m, n) - 1];
    }

    /**
     * Effective numerical matrix rank
     *
     * @return Number of nonnegligible singular values.
     */
    @Override
    public int rank() {
        double eps = Math.pow(2.0, -52.0);
        double tol = Math.max(m, n) * s[0] * eps;
        int r = 0;
        for (int i = 0; i < s.length; i++) {
            if (s[i] > tol) {
                r++;
            }
        }
        return r;
    }
}
