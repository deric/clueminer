package org.clueminer.xcalibour.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.clueminer.fixtures.XCalibourFixture;
import org.clueminer.xcalibour.files.LineInterpolator;
import org.clueminer.xcalibour.data.MassSpectrum;
import org.clueminer.xcalibour.data.SpectrumDataset;
import org.clueminer.xcalibour.files.SpectrumMapper;
import org.clueminer.xcalibour.files.XCalibourImporter;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.maths.algorithms.interpolation.IInterpolator;
import org.jzy3d.maths.algorithms.interpolation.algorithms.BernsteinInterpolator;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class InterpolateDemo {

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


        Chart chart = new Chart(Quality.Advanced, "newt");

// Define range and precision for the function to plot
        Range xrange;
        int xsteps = 2;

        Range yrange = new Range(30, 200);
        int ysteps = 170;
        IInterpolator interpol = new BernsteinInterpolator();
// Create a surface drawing that function
        for (int i = 0; i < 500; i+=5) {

            double ystep = yrange.getRange() / (double) (ysteps - 1);

            MassSpectrum inst = dataset.get(i);

            List<Coord3d> output = new ArrayList<Coord3d>(ysteps);
            double filter = 1e3;
            for (int yi = 0; yi < ysteps; yi++) {
                double y = yrange.getMin() + yi * ystep;
                double z = inst.zValueAt(y, dataset.getTimePoints());
                if (z > filter) {
                    output.add(new Coord3d(i, y, z));
                }
            }

            LineInterpolator ls1 = new LineInterpolator(interpol, output, 30);
            chart.getScene().getGraph().add(ls1);
        }


        //OrthonormalGrid grid = new OrthonormalGrid(xrange, xsteps, yrange, ysteps);
      /*  Shape surface = Builder.buildOrthonormal(grid, mapper);
         surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
         surface.setFaceDisplayed(true);
         surface.setWireframeDisplayed(false);
         surface.setWireframeColor(Color.BLACK);*/ // set polygon border in black
        //surface.setFace(new FaceColorbar(surface)); // attach a 2d panel
        //surface.setFace2dDisplayed(true);         


        //chart.getScene().getGraph().add(surface);

        //chart.setViewPoint( Coord3d.ORIGIN);
        chart.setViewPoint(new Coord3d(0.5f, 0.5f, 0.5f));
        ChartLauncher.openChart(chart);
    }
}
