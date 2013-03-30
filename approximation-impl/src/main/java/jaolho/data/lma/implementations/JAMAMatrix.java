package jaolho.data.lma.implementations;

import org.clueminer.math.matrix.JMatrix;
import jaolho.data.lma.LMAMatrix;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;
import org.clueminer.math.Matrix;

public class JAMAMatrix extends JMatrix implements LMAMatrix {

    private static final long serialVersionUID = -8925816623803983503L;

    public JAMAMatrix(double[][] elements) {
        super(elements);
    }

    public JAMAMatrix(int rows, int cols) {
        super(rows, cols);
    }

    @Override
    public void invert() throws LMAMatrix.InvertException {
        try {
            Matrix m = inverse();
            setMatrix(0, this.rowsCount() - 1, 0, columnsCount() - 1, m);
        } catch (RuntimeException e) {
            StringWriter s = new StringWriter();
            PrintWriter p = new PrintWriter(s);
            p.println(e.getMessage());
            p.println("Inversion failed for matrix:");
            this.print(p, NumberFormat.getInstance(), 5);
            throw new LMAMatrix.InvertException(s.toString());
        }
    }

    @Override
    public void setElement(int row, int col, double value) {
        set(row, col, value);
    }

    @Override
    public double getElement(int row, int col) {
        return get(row, col);
    }

    @Override
    public void multiply(double[] vector, double[] result) {
        for (int i = 0; i < this.rowsCount(); i++) {
            result[i] = 0;
            for (int j = 0; j < this.columnsCount(); j++) {
                result[i] += this.getElement(i, j) * vector[j];
            }
        }
    }
}
