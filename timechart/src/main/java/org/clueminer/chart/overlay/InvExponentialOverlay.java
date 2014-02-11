package org.clueminer.chart.overlay;

import org.clueminer.approximation.api.ApproximatorFactory;
import org.clueminer.chart.api.Overlay;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Overlay.class)
public class InvExponentialOverlay extends ExponentialOverlay implements Overlay {

    public InvExponentialOverlay() {
        super();
        properties = new OverlayProperties();
        ApproximatorFactory af = ApproximatorFactory.getInstance();
        approximator = af.getProvider("exp-inv");
    }

    @Override
    public String getName() {
        return "Inverse Exp overlay";
    }

    @Override
    public String getLabel() {
        return "Inv Exp";
    }

    @Override
    public Overlay newInstance() {
        return new InvExponentialOverlay();
    }

    @Override
    protected double[] fetchParams(int idx) {
        double[] params = new double[4];

        params[0] = approxData.getAttributeValue("exp-a", idx);
        params[1] = approxData.getAttributeValue("exp-t", idx);
        params[2] = approxData.getAttributeValue("exp-c", idx);
        params[3] = approxData.getAttributeValue("exp-d", idx);
        return params;
    }

}
