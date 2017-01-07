/*
 * Copyright (C) 2011-2017 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.chart.plots;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.clueminer.chart.api.Axis;
import org.clueminer.chart.api.AxisPosition;
import org.clueminer.chart.api.AxisRenderer;
import org.clueminer.chart.api.ChartType;
import org.clueminer.chart.api.Label;
import org.clueminer.chart.api.Legend;
import org.clueminer.chart.api.Plot;
import org.clueminer.chart.base.AbstractPlot;
import org.clueminer.chart.base.Grid;
import org.clueminer.chart.data.DataSource;
import org.clueminer.chart.ui.BaseLabel;
import org.clueminer.chart.util.Insets2D;
import org.clueminer.chart.util.Orientation;

/**
 *
 * @author Tomas Barton
 */
public class ScatterPlot extends AbstractPlot implements Plot {

    private static final long serialVersionUID = 1450179727270901601L;
    protected Map<AxisPosition, Axis> axes;
    protected Grid grid;
    protected Insets2D insets = new Insets2D.Double(10, 10, 10, 10);
    private BaseLabel title;

    public ScatterPlot() {
        title = new BaseLabel();
    }

    public ScatterPlot(int width, int height) {
        super();
        initComponents(width, height);
    }

    private void initComponents(int width, int height) {
        grid = new Grid(this);
        setBounds(0, 0, width, height);
        axes = new HashMap<>(2);
        axes.put(AxisPosition.X, createAxis(false, Orientation.HORIZONTAL));
        axes.put(AxisPosition.Y, createAxis(false, Orientation.VERTICAL));
        setInsets(insets);
        add(grid);
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

        //Dimension2D size = comp.getPreferredSize();

        Shape shape;
        if (orientation == Orientation.HORIZONTAL) {
            shape = new Line2D.Double(
                    plotBounds.getX(), plotBounds.getY(),
                    plotBounds.getWidth(), plotBounds.getY()
            );
        } else {
            shape = new Line2D.Double(plotBounds.getX(), plotBounds.getHeight(),
                    plotBounds.getX(), 0.0
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
        return title;
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
    public ChartType getChartType() {
        return ChartType.Scatter;
    }
}
