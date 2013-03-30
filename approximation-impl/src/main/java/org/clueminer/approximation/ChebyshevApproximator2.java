package org.clueminer.approximation;

import org.clueminer.approximation.api.Approximator;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Approximator.class)
public class ChebyshevApproximator2 extends ChebyshevApproximator {

    public ChebyshevApproximator2() {
        super(2);
        name = "chebyshev-2";
    }
}
