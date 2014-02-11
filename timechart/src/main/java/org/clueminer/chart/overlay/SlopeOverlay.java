package org.clueminer.chart.overlay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.LinkedHashMap;
import org.clueminer.chart.api.ChartConfig;
import org.clueminer.chart.api.Overlay;
import org.clueminer.chart.base.AbstractOverlay;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.events.DatasetEvent;
import org.clueminer.types.TimePoint;
import org.openide.nodes.AbstractNode;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Overlay.class)
public class SlopeOverlay extends AbstractOverlay implements Overlay {

    private static final String name = "Slope";
    private Timeseries<? extends ContinuousInstance> dataset;

    public SlopeOverlay() {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLabel() {
        return "slope";
    }

    @Override
    public Timeseries<? extends ContinuousInstance> getDataset() {
        return dataset;
    }

    @Override
    public void paint(Graphics2D g, ChartConfig cf, Rectangle bounds) {
        double x1, x2, y1, y2, d;
        TimePoint[] tp = dataset.getTimePoints();
        for (ContinuousInstance inst : dataset) {
            x1 = tp[0].getPosition();
            y1 = inst.value(0);
            for (int i = 1; i < inst.size(); i++) {
                x2 = tp[i].getPosition();
                y2 = inst.value(i);
                d = (y2 - y1) / (x2 - x1);
                System.out.println(i + ": " + d);
                //move to next point
                x1 = x2;
                y1 = y2;
            }
        }
    }

    @Override
    public LinkedHashMap getHTML(ChartConfig cf, int i) {
        LinkedHashMap<String, String> ht = new LinkedHashMap<String, String>();

        ht.put(getLabel(), " ");

        return ht;
    }

    @Override
    public void calculate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AbstractOverlay newInstance() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Color[] getColors() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[] getValues(ChartConfig cf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[] getValues(ChartConfig cf, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getMarkerVisibility() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AbstractNode getNode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void datasetOpened(DatasetEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void datasetClosed(DatasetEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void datasetCropped(DatasetEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
