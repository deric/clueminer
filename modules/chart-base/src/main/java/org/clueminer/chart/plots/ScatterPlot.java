package org.clueminer.chart.plots;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.Collection;
import java.util.List;
import org.clueminer.chart.api.Axis;
import org.clueminer.chart.api.AxisPosition;
import org.clueminer.chart.api.AxisRenderer;
import org.clueminer.chart.api.Plot;
import org.clueminer.chart.api.PlotArea;
import org.clueminer.chart.base.AbstractPlot;
import org.clueminer.chart.base.BaseAxis;
import org.clueminer.chart.base.Grid;
import org.clueminer.chart.data.DataSource;
import org.clueminer.chart.graphics.Label;
import org.clueminer.chart.legends.Legend;
import org.clueminer.chart.renderer.LinearRenderer2D;
import org.clueminer.chart.util.Location;

/**
 *
 * @author Tomas Barton
 */
public class ScatterPlot extends AbstractPlot implements Plot {

    private static final long serialVersionUID = 1450179727270901601L;
    private Axis[] axes;
    private AxisRenderer[] axesRenderer;

    public ScatterPlot(int width, int height) {
        super(width, height);

        axes = new Axis[2];
        axes[AxisPosition.X.getId()] = new BaseAxis();
        axes[AxisPosition.Y.getId()] = new BaseAxis();
        axesRenderer = new AxisRenderer[2];
        axesRenderer[AxisPosition.X.getId()] = new LinearRenderer2D();
        axesRenderer[AxisPosition.Y.getId()] = new LinearRenderer2D();

        add(new Grid(this));
    }

    public ScatterPlot(ChartBuilder builder) {
        this(builder.width, builder.height);
    }

    @Override
    public Axis getAxis(AxisPosition pos) {
        return axes[pos.getId()];
    }

    @Override
    public void setAxis(AxisPosition pos, Axis axis) {
        axes[pos.getId()] = axis;
    }

    @Override
    public void removeAxis(AxisPosition pos) {
        axes[pos.getId()] = null;
    }

    @Override
    public Collection<AxisPosition> getAxesNames() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void autoscaleAxis(AxisPosition axisName) {
        axes[axisName.getId()].setAutoscaled(true);
    }

    @Override
    public AxisRenderer getAxisRenderer(AxisPosition axisName) {
        return axesRenderer[axisName.getId()];
    }

    @Override
    public void setAxisRenderer(AxisPosition axisName, AxisRenderer renderer) {
        axesRenderer[axisName.getId()] = renderer;
    }

    @Override
    public PlotArea getPlotArea() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Label getTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Legend getLegend() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void add(DataSource source) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void add(DataSource source, boolean visible) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void add(int index, DataSource source, boolean visible) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean contains(DataSource source) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DataSource get(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean remove(DataSource source) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String[] getMapping(DataSource source) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMapping(DataSource source, String... axisNames) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<DataSource> getData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<DataSource> getVisibleData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isVisible(DataSource source) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setVisible(DataSource source, boolean visible) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Paint getBackground() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setBackground(Paint background) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Stroke getBorderStroke() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setBorderStroke(Stroke border) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Paint getBorderColor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setBorderColor(Paint color) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Font getFont() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setFont(Font font) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isLegendVisible() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLegendVisible(boolean legendVisible) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Location getLegendLocation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLegendLocation(Location location) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getLegendDistance() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLegendDistance(double distance) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
