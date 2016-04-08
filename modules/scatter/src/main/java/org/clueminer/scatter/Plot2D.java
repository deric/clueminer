/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.scatter;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.StyleManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.clueminer.colors.ColorBrewer;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.DataType;
import org.clueminer.dataset.api.Plotter;
import org.openide.util.lookup.ServiceProvider;

/**
 * Typically used for visualization of low-dimensional data.
 *
 * @author deric
 * @param <E> backing data structure
 * @param <T> number type
 */
@ServiceProvider(service = Plotter.class)
public class Plot2D<E extends Instance, T extends Number> extends JPanel implements Plotter<E> {

    private Chart chart;
    private String title;
    private boolean simple;
    private int markerSize = 8;
    private Series data;
    private int attrX = 0;
    private int attrY = 1;
    private Collection<Double> xData;
    private Collection<Double> yData;
    private Collection<Double> eData;
    private static final Logger LOG = Logger.getLogger(Plot2D.class.getName());
    private ColorGenerator cg;

    public Plot2D() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setSize(new Dimension(400, 400));
        cg = new ColorBrewer();
    }

    private Chart createChart() {

        Chart ch = new Chart(getWidth(), getHeight());
        if (title != null) {
            ch.setChartTitle(title);
        }
        StyleManager sm = ch.getStyleManager();
        sm.setChartType(StyleManager.ChartType.Scatter);

        sm.setChartTitleVisible(false);
        if (simple) {
            // Customize Chart
            sm.setLegendVisible(false);
            sm.setAxisTitlesVisible(false);
            sm.setAxisTitlePadding(0);
            sm.setChartBackgroundColor(Color.WHITE);
            sm.setPlotBorderVisible(false);
            sm.setAxisTicksVisible(false);
        } else {
            //sm.setLegendPosition(StyleManager.LegendPosition.OutsideE);
            sm.setLegendVisible(false);
        }

        sm.setMarkerSize(markerSize);

        //wrap chart in JPanel and add to this component
        //XChartPanel xchart = new XChartPanel(ch);
        //add(xchart);
        return ch;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension dim = getSize();
        Graphics2D g2d = (Graphics2D) g.create();
        chart.paint(g2d, dim.width, dim.height);
        g2d.dispose();
    }

    /**
     * At this point we have no idea how many points we're going to visualize.
     *
     * @param inst
     */
    @Override
    public void addInstance(E inst) {
        addInstance(inst, "data");
    }

    @Override
    public void addInstance(E inst, String clusterName) {
        if (inst == null) {
            LOG.warning("null instance for plotting. skipping");
            return;
        }
        if (chart == null) {
            chart = createChart();
            xData = new ArrayList<>();
            yData = new ArrayList<>();
            eData = new ArrayList<>();
            addDataPoint(inst);
            //TODO: find appropriate name for the series
            data = chart.addSeries(clusterName, xData, yData);
            data.setMarkerColor(cg.next());
        } else {
            addDataPoint(inst);
            data.replaceData(xData, yData, eData);
        }
        revalidate();
        repaint();
    }

    private void addDataPoint(E instance) {
        xData.add(instance.get(attrX));
        yData.add(instance.get(attrY));
        eData.add(Double.MIN_VALUE);
    }

    @Override
    public void clearAll() {
        chart = null;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setXBounds(double min, double max) {
        //TODO: not much to do
    }

    @Override
    public void setYBounds(double min, double max) {
        //TODO: not much to do
    }

    @Override
    public void prepare(DataType type) {
        if (!isSupported(type)) {
            throw new RuntimeException("plot type " + type.name() + " is not supported");
        }

    }

    @Override
    public boolean isSupported(DataType type) {
        return type == DataType.DISCRETE;
    }

}
