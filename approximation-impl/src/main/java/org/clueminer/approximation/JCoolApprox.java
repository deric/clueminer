package org.clueminer.approximation;

import cz.cvut.felk.cig.jcool.benchmark.method.gradient.lm.LevenbergMarquardtMethod;
import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.Point;
import cz.cvut.felk.cig.jcool.core.StopCondition;
import cz.cvut.felk.cig.jcool.solver.OptimizationResults;
import cz.cvut.felk.cig.jcool.solver.Solver;
import cz.cvut.felk.cig.jcool.solver.SolverFactory;
import cz.cvut.felk.cig.jcool.solver.Statistics;
import java.util.HashMap;
import org.clueminer.approximation.api.Approximator;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.interpolation.LinearInterpolator;
import org.clueminer.math.Interpolator;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Approximator.class)
public class JCoolApprox extends Approximator {

    private static final int numCoeff = 5;

    @Override
    public String getName() {
        return "JCOOL";
    }

    /**
     * A wrapper around data points
     */
    private class DataFunction implements Function {

        private final ContinuousInstance inst;
        private Interpolator inter = new LinearInterpolator();

        DataFunction(ContinuousInstance instance) {
            this.inst = instance;
        }

        @Override
        public double valueAt(Point point) {
            System.out.println("point: " + point.toString());
            return inst.valueAt(point.toArray()[0], inter);
        }

        @Override
        public int getDimension() {
            return 1;
        }
    }

    @Override
    public void estimate(double[] xAxis, ContinuousInstance instance, HashMap<String, Double> coefficients) {
        DataFunction f = new DataFunction(instance);

        // a solver allowing maximum of 50 iterations
        Solver solver = SolverFactory.getNewInstance(50);

        // solver.init() and solver.solve() can throw any kind of exception and we must react on that.
        try {
            // the test method randomly calls valueAt, gradientAt and hessianAt
            // methods on the test function
            LevenbergMarquardtMethod method = new LevenbergMarquardtMethod();
            method.setMin(xAxis[0]);
            method.setMax(xAxis[xAxis.length - 1]);
            solver.init(f, method);

            // the computations is stopped on an instance of IterationStopCondition
            // after 50 iterations
            solver.solve();

            // result gathering
            OptimizationResults r = solver.getResults();

            // present the results to the world
            System.out.println(r.getSolution());

            for (StopCondition condition : r.getMetConditions()) {
                System.out.println("stopped on condition: " + condition.getClass());
            }

            Statistics stats = r.getStatistics();
            System.out.println("# of Value evaluations:    " + stats.getValueAt());
            System.out.println("# of Gradient evaluations: " + stats.getGradientAt());
            System.out.println("# of Hessian evaluations:  " + stats.getHessianAt());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String[] getParamNames() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getFunctionValue(double x, double[] coeff) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumCoefficients() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
