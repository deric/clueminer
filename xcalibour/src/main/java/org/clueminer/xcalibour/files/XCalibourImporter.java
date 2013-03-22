package org.clueminer.xcalibour.files;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.plugin.TimeseriesDataset;
import org.clueminer.dataset.row.TimeInstance;
import org.clueminer.longtask.spi.LongTask;
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
 *
 * @author Tomas Barton
 */
public class XCalibourImporter implements LongTask, Runnable {

    private ProgressHandle ph;
    private File file;

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

            List<Variable> variables = ncfile.getVariables();

            Variable v = ncfile.findVariable("scan_index");
            System.out.println("n of scans: "+v);
            if(v != null){
                System.out.println("scan_index: "+ v.read().toString());
            }
            Attribute attr = ncfile.findGlobalAttribute("number_of_scans");
            System.out.println("attr = "+attr.getNumericValue());
            
            /**
             * index tells us where is the start of next measurement
             */
            int[] scan_indexes = null;
            Variable scan_var = ncfile.findVariable("scan_index");
            if(scan_var != null){
                Array val = scan_var.read();                
                scan_indexes = val.getShape();
            }
            
            Array mass = null, intensity = null, total_intensity;
            try {
                 String var = "mass_values";
                 System.out.println("variable: "+var);
                 mass = ncfile.readSection(var);                 
                 //System.out.println(mass.toString());

                 var = "intensity_values";
                 System.out.println("variable: "+var);
                 intensity = ncfile.readSection(var);
                 //System.out.println(intensity.toString());

                 var = "total_intensity";
                 System.out.println("variable: "+var);
                 total_intensity = ncfile.readSection(var);
                 //System.out.println(total_intensity.toString());

             } catch (InvalidRangeException ex) {
                 Exceptions.printStackTrace(ex);
             }

            int curr = 0;
            int next, size;
            TimeseriesDataset<ContinuousInstance> dataset = new TimeseriesDataset<ContinuousInstance>(scan_indexes.length);
            for (int i = 0; i < scan_indexes.length; i++) {
                next = scan_indexes[i];
                size = next - curr;
                MassSpectrum<MassItem> inst = new MassSpectrum<MassItem>(size);
                
                for (int j = curr; j < size; j++) {
                    MassItem value = new MassItem(intensity.getLong(j), mass.getDouble(j));
                    inst.put(value);
                    
                }
                
                
                curr = next;
                
            }
            
            
            
            
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
                
                

            

            System.out.println("scan_index");
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
}
