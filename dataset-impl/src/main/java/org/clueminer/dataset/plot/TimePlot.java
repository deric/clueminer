package org.clueminer.dataset.plot;

import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.math.plot.Plot2DPanel;

/**
 *
 * @author Tomas Barton
 */
public class TimePlot extends Plot2DPanel implements Plotter {

    private static final long serialVersionUID = 9134124279294818651L;

    public TimePlot() {
        super();
    }

    @Override
    public void addInstance(Instance instance) {
        this.addLinePlot(instance.getName(), instance.arrayCopy());
    }

    @Override
    public void clearAll() {
        this.removeAllPlots();
    }
}
