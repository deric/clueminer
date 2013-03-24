package org.clueminer.xcalibour.files;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.plugin.TimeseriesDataset;
import org.clueminer.dataset.row.TimeInstance;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.types.TimePoint;
import org.clueminer.utils.progress.ProgressTicket;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;
import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.Group;
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

    public XCalibourImporter(File file) {
        if (!file.exists()) {
            throw new RuntimeException("file " + file.getAbsolutePath() + " not found");
        }
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

    @Override
    public void run() {
        System.out.println("importing data");

        NetcdfFile ncfile = null;
        String filename = file.getAbsolutePath();
        System.out.println("opening file: " + filename);

        try {
            ncfile = NetcdfDataset.openFile(filename, null);
            System.out.println("ncfile: " + ncfile);
            System.out.println("title: " + ncfile.getTitle());

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
            System.out.println("scan indexes: " + scan_indexes.getShape());


            Array mass = null, intensity = null, total_intensity = null, scan_time = null;
            try {
                String var = "mass_values";
                System.out.println("variable: " + var);
                mass = ncfile.readSection(var);

                //System.out.println(mass.toString());

                var = "intensity_values";
                System.out.println("variable: " + var);
                intensity = ncfile.readSection(var);

                //System.out.println(intensity.toString());

                var = "total_intensity";
                System.out.println("variable: " + var);
                total_intensity = ncfile.readSection(var);

                //System.out.println(total_intensity.toString());

                var = "scan_acquisition_time";
                System.out.println("variable: " + var);
                scan_time = ncfile.readSection(var);

                System.out.println(scan_time.toString());


                var = "point_count";
                System.out.println("variable: " + var);
                Array scan_duration = ncfile.readSection(var);

                System.out.println(scan_duration.toString());



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
                throw new RuntimeException("total_intensity var is null!");
            }

            int curr = 0;
            int next, size;
            int end;
            dataset = new SpectrumDataset<MassSpectrum>(num_measurements);
            TimePointAttribute[] timepoints = new TimePointAttribute[num_measurements];
            for (int i = 0; i < num_measurements; i++) {
                timepoints[i] = new TimePointAttribute(i, scan_time.getLong(i));
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

            System.out.println("dataset size = " + dataset.size());
            System.out.println("dataset max attr = " + dataset.attributeCount());


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
        } finally {
            if (null != ncfile) {
                try {
                    ncfile.close();
                } catch (IOException ioe) {
                    // log("trying to close " + filename, ioe);
                }
            }
        }

    }

    public SpectrumDataset<MassSpectrum> getDataset() {
        return dataset;
    }
}
