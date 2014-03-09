package org.clueminer.fit;

import cz.cvut.felk.cig.jcool.core.StopCondition;
import cz.cvut.felk.cig.jcool.solver.OptimizationResults;
import cz.cvut.felk.cig.jcool.solver.Solver;
import cz.cvut.felk.cig.jcool.solver.SolverFactory;
import cz.cvut.felk.cig.jcool.solver.Statistics;
import cz.cvut.felk.cig.jcool.solver.demo.TestFunction;
import cz.cvut.felk.cig.jcool.solver.demo.TestMethod;

/**
 *
 * @author Tomas Barton
 */
public class GaussianFit {

    public void fit() {
        // a solver allowing maximum of 50 iterations
        Solver solver = SolverFactory.getNewInstance(50);

        // solver.init() and solver.solve() can throw any kind of exception and we must react on that.
        try {
            // the test method randomly calls valueAt, gradientAt and hessianAt
            // methods on the test function
            solver.init(new TestFunction(), new TestMethod());

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
}
