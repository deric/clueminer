package org.clueminer.clusterpreview;

import java.awt.Color;
import java.awt.GridBagLayout;
import org.math.plot.PlotPanel;
import org.math.plot.canvas.Plot2DCanvas;

/**
 *
 * @author Tomas Barton
 */
public class ScatterPlotPanel extends PlotPanel {

    private static final long serialVersionUID = -939469889909029861L;
    
    public ScatterPlotPanel(){
        super(new Plot2DCanvas());
        initComponents();
    }
    
    private void initComponents(){
        setLayout(new GridBagLayout());
        
    }

    @Override
    public int addPlot(String type, String name, Color c, double[]... v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
