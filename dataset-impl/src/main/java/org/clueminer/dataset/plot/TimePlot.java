package org.clueminer.dataset.plot;

import java.awt.Color;
import java.awt.Font;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.dataset.api.Timeseries;
import org.math.plot.Plot2DPanel;
import org.math.plot.plotObjects.BaseLabel;

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
        ContinuousInstance inst = (ContinuousInstance) instance;
        Timeseries dataset = inst.getParent();
        this.addLinePlot(instance.getName(), dataset.getTimePointsArray(), instance.arrayCopy());
    }

    @Override
    public void clearAll() {
        this.removeAllPlots();
    }

    @Override
    public void setTitle(String title) {
        BaseLabel label = new BaseLabel(title, Color.BLACK, 0.5, 1.1);
        label.setFont(new Font("serif", Font.BOLD, 20));
        this.addPlotable(label);
    }

    @Override
    public void setXBounds(double min, double max) {
        this.setFixedBounds(0, min, max);
    }

    @Override
    public void setYBounds(double min, double max) {
        this.setFixedBounds(1, min, max);
    }
}
