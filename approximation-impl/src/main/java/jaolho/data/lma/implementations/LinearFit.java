package jaolho.data.lma.implementations;

import jaolho.data.lma.LMAFunction;

/** An example fit which fits a straight line to some data points and prints out the resulting fit parameters. */
public class LinearFit {

    /** An example function with a form of y = a0 * x + a1 */
    public static class LinearExampleFunction extends LMAFunction {

        @Override
        public double getY(double x, double[] a) {
            return a[0] * x + a[1];
        }

        @Override
        public double getPartialDerivate(double x, double[] a, int parameterIndex) {
            switch (parameterIndex) {
                case 0:
                    return x;
                case 1:
                    return 1;
            }
            throw new RuntimeException("No such parameter index: " + parameterIndex);
        }
    }
}
