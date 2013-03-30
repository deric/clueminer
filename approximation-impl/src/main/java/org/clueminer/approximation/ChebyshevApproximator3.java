package org.clueminer.approximation;

import org.clueminer.approximation.api.Approximator;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Approximator.class)
public class ChebyshevApproximator3 extends ChebyshevApproximator {

    public ChebyshevApproximator3() {
        super(3);
        name = "chebyshev-3";
    }
}
