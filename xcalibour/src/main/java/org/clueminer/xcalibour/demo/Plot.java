package org.clueminer.xcalibour.demo;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 *
 * @author deric
 */
public class Plot {

public static void main(String[] args) {
        
        // Define a function to plot
        Mapper mapper = new Mapper() {
            public double f(double x, double y) {
                return /*10 * */Math.sin(x / 10) /** Math.cos(y / 20) * x*/;
            }
        };

        // Define range and precision for the function to plot
        Range range = new Range(-150, 150);
        int steps = 50;

        // Create a surface drawing that function
        Shape surface = Builder.buildOrthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(false);
        surface.setWireframeColor(Color.BLACK);

        // Create a chart and add the surface
        //Chart chart = new Chart(Quality.Advanced, "newt");
    Chart chart = new Chart(Quality.Fastest, "awt");
        chart.getScene().getGraph().add(surface);
        ChartLauncher.openChart(chart);
    }
}
