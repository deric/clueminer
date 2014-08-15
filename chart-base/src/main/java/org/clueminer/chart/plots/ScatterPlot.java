package org.clueminer.chart.plots;

import java.awt.Dimension;
import java.awt.Graphics2D;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.gui.BPanel;

/**
 *
 * @author Tomas Barton
 */
public class ScatterPlot extends BPanel {

    protected Dataset<? extends Instance> dataset;

    @Override
    public void render(Graphics2D g) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sizeUpdated(Dimension size) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void recalculate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isAntiAliasing() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void setDataset(Dataset<? extends Instance> data) {
        this.dataset = data;
    }

}
