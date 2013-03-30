package org.clueminer.approximation;

import org.clueminer.approximation.api.Approximator;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Approximator.class)
public class ChebyshevApproximator4 extends ChebyshevApproximator {

    public ChebyshevApproximator4() {
        super(4);
        name = "chebyshev-4";
    }
}