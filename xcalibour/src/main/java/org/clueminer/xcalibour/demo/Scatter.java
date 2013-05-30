package org.clueminer.xcalibour.demo;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.ScatterMultiColor;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 *
 * @author deric
 */
public class Scatter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int size = 100000;
        float x;
        float y;
        float z;
        Coord3d[] points = new Coord3d[size];

// Create scatter points
        for (int i = 0; i < size; i++) {
            x = (float) Math.random() - 0.5f;
            y = (float) Math.random() - 0.5f;
            z = (float) Math.random() - 0.5f;
            points[i] = new Coord3d(x, y, z);
        }

// Create a drawable scatter with a colormap
        ScatterMultiColor scatter = new ScatterMultiColor(points, new ColorMapper(new ColorMapRainbow(), -0.5f, 0.5f));

// Create a chart and add scatter
        Chart chart = new Chart(Quality.Advanced, "newt");
        chart.getAxeLayout().setMainColor(Color.WHITE);
        chart.getView().setBackgroundColor(Color.BLACK);
        chart.getScene().add(scatter);
        ChartLauncher.openChart(chart);
    }
}
