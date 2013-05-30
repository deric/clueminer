package org.clueminer.xcalibour.files;

import org.clueminer.xcalibour.data.SpectrumDataset;
import org.clueminer.xcalibour.data.MassSpectrum;
import org.clueminer.xcalibour.data.MassItem;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.utils.progress.ProgressTicket;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;
import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

/**
 * Variables:
 *
 * [resolution] - all values are 0
 *
 * [scan_acquisition_time] - time of capturing an instance
 *
 * [scan_duration] - all values are 0
 *
 * [point_count] - number of points captured in each instance (usually between
 * 100 - 200)
 *
 * @author Tomas Barton
 */
public class XCalibourImporter implements LongTask, Runnable {

    private ProgressHandle ph;
    private File file;
    private SpectrumDataset<MassSpectrum> dataset;
    private static final Logger logger = Logger.getLogger(XCalibourImporter.class.getName());

    public XCalibourImporter(File file) {
        this.file = file;
    }

    public void setProgressHandle(ProgressHandle ph) {
        this.ph = ph;
    }

    @Override
    public boolean cancel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public File getFile() {
        return file;
    }

    @Override
    public void run() {
        if (!file.exists()) {
            throw new RuntimeException("file " + file.getAbsolutePath() + " not found");
        }
        logger.log(Level.FINE, "importing data");

        NetcdfFile ncfile = null;
        String filename = file.getAbsolutePath();
        logger.log(Level.FINE, "opening file: {0}", filename);

        try {
            ncfile = NetcdfDataset.openFile(filename, null);
            //System.out.println("ncfile: " + ncfile);

            //List<Variable> variables = ncfile.getVariables();

            Attribute attr = ncfile.findGlobalAttribute("number_of_scans");
            int num_measurements = attr.getNumericValue().intValue();
            /**
             * index tells us where is the start of next measurement segment
             */
            // int[] scan_indexes = null;
            Variable scan_var = ncfile.findVariable("scan_index");
            if (scan_var == null) {
                throw new RuntimeException("scan var is null!");
            }

            Array scan_indexes = scan_var.read();

            Array mass = null, intensity = null, total_intensity = null, scan_time = null;
            try {
                String var = "mass_values";
                logger.log(Level.FINE, "variable: {0}", var);
                mass = ncfile.readSection(var);

                //System.out.println(mass.toString());

                var = "intensity_values";
                logger.log(Level.FINE, "variable: {0}", var);
                intensity = ncfile.readSection(var);

                //System.out.println(intensity.toString());

                var = "total_intensity";
                logger.log(Level.FINE, "variable: {0}", var);
                total_intensity = ncfile.readSection(var);

                //System.out.println(total_intensity.toString());

                var = "scan_acquisition_time";
                logger.log(Level.FINE, "variable: {0}", var);
                scan_time = ncfile.readSection(var);

            } catch (InvalidRangeException ex) {
                Exceptions.printStackTrace(ex);
            }

            if (mass == null) {
                throw new RuntimeException("mass var is null!");
            }

            if (intensity == null) {
                throw new RuntimeException("intensity var is null!");
            }

            if (total_intensity == null) {
                throw new RuntimeException("total_intensity var is null!");
            }

            if (scan_time == null) {
                throw new RuntimeException("scan_time var is null!");
            }

            int curr = 0;
            int next, size;
            int end;
            dataset = new SpectrumDataset<MassSpectrum>(num_measurements);
            TimePointAttribute[] timepoints = new TimePointAttribute[num_measurements];
            for (int i = 0; i < num_measurements; i++) {
                //time is stored as a double value
                timepoints[i] = new TimePointAttribute(i, 0, scan_time.getDouble(i));
                if ((i + 1) == num_measurements) {
                    // size of last segment is unknown, we read till end of array
                    next = (int) intensity.getSize();
                } else {
                    next = scan_indexes.getInt(i + 1);
                }
                size = next - curr;
                MassSpectrum<MassItem> inst = new MassSpectrum<MassItem>(size);
                end = curr + size;
                for (int j = curr; j < end; j++) {
                    MassItem value = new MassItem(intensity.getLong(j), mass.getDouble(j));
                    inst.put(value);
                }
                dataset.add(inst);
                curr = next;
            }
            dataset.setTimePoints(timepoints);

            logger.log(Level.FINE, "dataset size = {0}", dataset.size());
            logger.log(Level.FINE, "dataset max attr = {0}", dataset.attributeCount());


            // List<Array> arry = ncfile.readArrays(variables);

            /*
             for (Variable v : variables) {
             System.out.println("variable: " + v.getName());
             System.out.println(v.toString());


             for (Dimension d : v.getDimensions()) {
             System.out.println("d: " + d.getName());
             System.out.println("d: " + d.toString());
             System.out.println("attributes: ");

             Group g = d.getGroup();
             System.out.println("group " + g.getName());
             //  System.out.println("group " + g.getNameAndAttributes());

             for (Attribute a : g.getAttributes()) {
             DataType dt = a.getDataType();
             if (dt.isNumeric()) {
             System.out.println(a.getName() + ": " + a.getNumericValue());
             } else if (dt.isString()) {
             System.out.println(a.getName() + ": " + a.getStringValue());
             } else {
             System.out.println("WTF: " + a.getName() + a.getStringValue());
             }



             //if(a.isArray()){
             //System.out.println("array: " + a.getValues());
             //}

             }
             int[] shape = v.getShape();
             for (int i = 0; i < shape.length; i++) {
             System.out.println(shape[i]);
             }

             }
             System.out.println("variable: "+v.getName());
             try {
             Array ary = ncfile.readSection(v.getName());
             System.out.println(ary.toString());
             } catch (InvalidRangeException ex) {
             Exceptions.printStackTrace(ex);
             }

             System.out.println("variable size: " + v.getSize());
             }*/

            //process(ncfile);
        } catch (IOException ioe) {
            // log("trying to open " + filename, ioe);
            Exceptions.printStackTrace(ioe);
        } finally {
            if (null != ncfile) {
                try {
                    ncfile.close();
                } catch (IOException ioe) {
                    // log("trying to close " + filename, ioe);
                    Exceptions.printStackTrace(ioe);
                }
            }
        }
    }

    public SpectrumDataset<MassSpectrum> getDataset() {
        return dataset;
    }
}
