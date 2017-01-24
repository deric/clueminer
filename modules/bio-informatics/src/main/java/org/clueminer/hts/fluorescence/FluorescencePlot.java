package org.clueminer.hts.fluorescence;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.List;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.DataType;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.dataset.api.Timeseries;
import org.math.plot.Plot2DPanel;
import org.math.plot.plotObjects.BaseLabel;

/**
 *
 * @author Tomas Barton
 */
public class FluorescencePlot<E extends Instance> extends Plot2DPanel implements Plotter<E> {

    private static final long serialVersionUID = 9134124279294818651L;

    public FluorescencePlot() {
        super();
    }

    @Override
    public void addInstance(E instance) {
        ContinuousInstance inst = (ContinuousInstance) instance;
        Timeseries dataset = (Timeseries) inst.getParent();
        this.addLinePlot(instance.getName(), instance.getColor(), dataset.getTimePointsArray(), instance.arrayCopy());
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

    @Override
    public void prepare(DataType type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addInstance(E instance, String clusterName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSupported(DataType type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<E> instanceAt(double[] coord, int maxK) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void focus(E instance, MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
