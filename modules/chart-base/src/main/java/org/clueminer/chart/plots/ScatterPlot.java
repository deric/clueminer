package org.clueminer.chart.plots;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.clueminer.chart.api.Axis;
import org.clueminer.chart.api.AxisPosition;
import org.clueminer.chart.api.AxisRenderer;
import org.clueminer.chart.api.Plot;
import org.clueminer.chart.base.AbstractPlot;
import org.clueminer.chart.base.BaseAxis;
import org.clueminer.chart.base.Grid;
import org.clueminer.chart.data.DataSource;
import org.clueminer.chart.graphics.Label;
import org.clueminer.chart.legends.Legend;
import org.clueminer.chart.renderer.LinearRenderer2D;
import org.clueminer.chart.util.Insets2D;
import org.clueminer.chart.util.Location;
import org.clueminer.chart.util.Orientation;

/**
 *
 * @author Tomas Barton
 */
public class ScatterPlot extends AbstractPlot implements Plot {

    private static final long serialVersionUID = 1450179727270901601L;
    private Map<AxisPosition, Axis> axes;
    private Grid grid;
    private Insets2D insets = new Insets2D.Double(10, 10, 10, 10);

    public ScatterPlot(int width, int height) {
        grid = new Grid(this);
        setBounds(0, 0, width, height);
        axes = new HashMap<>(2);
        axes.put(AxisPosition.X, createAxis(false, Orientation.HORIZONTAL));
        axes.put(AxisPosition.Y, createAxis(false, Orientation.VERTICAL));
        setInsets(insets);
        add(grid);

    }

    private Axis createAxis(boolean isLogscale, Orientation orient) {
        Axis ax;
        if (isLogscale) {
            throw new UnsupportedOperationException("not supported yet");
        } else {
            ax = new BaseAxis(new LinearRenderer2D(), orient);
        }
        add(ax);
        return ax;
    }

    @Override
    public void layout() {
        grid.setBounds(getPlotArea());
        if (axes != null) {
            for (Axis ax : axes.values()) {
                layoutAxisShape(ax, ax.getOrientation());
            }
        }
    }

    private void layoutAxisShape(Axis comp, Orientation orientation) {
        Rectangle2D plotBounds = getPlotArea();

        if (comp == null) {
            return;
        }
        AxisRenderer renderer = comp.getRenderer();

        Dimension2D size = comp.getPreferredSize();

        Shape shape;
        if (orientation == Orientation.HORIZONTAL) {
            shape = new Line2D.Double(
                    plotBounds.getX(), plotBounds.getY(),
                    plotBounds.getWidth(), plotBounds.getY()
            );
        } else {
            shape = new Line2D.Double(size.getWidth(), plotBounds.getHeight(),
                    size.getWidth(), 0.0
            );
        }
        renderer.setShape(shape);
    }

    public ScatterPlot(ChartBuilder builder) {
        this(builder.width, builder.height);
    }

    @Override
    public Axis getAxis(AxisPosition pos) {
        return axes.get(pos);
    }

    @Override
    public void setAxis(AxisPosition pos, Axis axis) {
        axes.put(pos, axis);
    }

    @Override
    public void removeAxis(AxisPosition pos) {
        axes.remove(pos);
    }

    @Override
    public Rectangle2D getPlotArea() {
        Rectangle2D b = getBounds();
        Rectangle2D rect = new Rectangle2D.Double(insets.getBottom(), insets.getTop(),
                b.getWidth() - insets.getHorizontal(),
                b.getHeight() - insets.getVertical());

        return rect;
    }

    @Override
    public Collection<AxisPosition> getAxesNames() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void autoscaleAxis(AxisPosition pos) {
        axes.get(pos).setAutoscaled(true);
    }

    @Override
    public AxisRenderer getAxisRenderer(AxisPosition axisName) {
        Axis axis = axes.get(axisName);
        if (axis != null) {
            return axis.getRenderer();
        }
        return null;
    }

    @Override
    public void setAxisRenderer(AxisPosition axisName, AxisRenderer renderer) {
        axes.get(axisName).setRenderer(renderer);
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
