package org.clueminer.clustering.benchmark.evolve;

import com.beust.jcommander.Parameter;
import org.clueminer.clustering.benchmark.AbsParams;

/**
 *
 * @author Tomas Barton
 */
public class EvolveParams extends AbsParams {

    @Parameter(names = "--external", description = "reference criterion for comparing with internal criterion (Precision, Accuracy, NMI)")
    public String external = "AUC";

    @Parameter(names = "--test", description = "test only on one dataset")
    public boolean test = false;

}
