package org.clueminer.xcalibour.demo;

import java.io.IOException;
import org.clueminer.fixtures.XCalibourFixture;
import org.clueminer.xcalibour.data.MassSpectrum;
import org.clueminer.xcalibour.files.MyOrthoGrid;
import org.clueminer.xcalibour.data.SpectrumDataset;
import org.clueminer.xcalibour.files.SpectrumMapper;
import org.clueminer.xcalibour.files.XCalibourImporter;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class DelaunayDemo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
               XCalibourFixture tf = new XCalibourFixture();
        ProgressHandle ph = ProgressHandleFactory.createHandle("Importing dataset");
        XCalibourImporter importer = null;
        try {
            importer = new XCalibourImporter(tf.testData());            
            importer.setProgressHandle(ph);
            importer.run();

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        SpectrumDataset<MassSpectrum> dataset = importer.getDataset();
        SpectrumMapper mapper = new SpectrumMapper();
        mapper.setDataset(dataset);



// Define range and precision for the function to plot
        Range xrange = new Range(0, 4000);
        int xsteps = 1500;

        Range yrange = new Range(30, 200);
        int ysteps = 170;

// Create a surface drawing that function
        MyOrthoGrid grid = new MyOrthoGrid(xrange, xsteps, yrange, ysteps);        
        final Shape surface = Builder.buildDelaunay(grid.apply(mapper));
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(false);       
        surface.setWireframeColor(Color.BLACK); // set polygon border in black
        //surface.setFace(new FaceColorbar(surface)); // attach a 2d panel
        //surface.setFace2dDisplayed(true);         

// Create a chart and add the surface
        Chart chart = new Chart(Quality.Advanced, "newt");
        chart.getScene().getGraph().add(surface);
        //chart.setViewPoint( Coord3d.ORIGIN);
        chart.setViewPoint( new Coord3d(0.5f, 0.5f, 0.5f));
        ChartLauncher.openChart(chart);
    }
}
