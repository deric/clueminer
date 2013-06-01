package org.clueminer.posturomed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.utils.progress.ProgressTicket;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class PosturomedImporter implements LongTask, Runnable {

    private File file;
    private int timesCount = 0;
    private int workUnits = 0;
    private ProgressHandle ph;
    private PosturomedDataset dataset;

    public PosturomedImporter(File file) {
        this.file = file;
    }

    public PosturomedImporter(File file, ProgressHandle ph) {
        this.file = file;
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

    public void setProgressHandle(ProgressHandle ph) {
        this.ph = ph;
    }

    @Override
    public void run() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            try {

                parseHeader(br);
                parseData(br);

            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                br.close();
                ph.finish();
            }
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /**
     * @see Posturomed_Commander-exp.m line 8863
     * @param br
     * @throws IOException 
     */
    private void parseHeader(BufferedReader br) throws IOException {        
        dataset = new PosturomedDataset();
        String surname = br.readLine();
        String firstname = br.readLine();
        //two empty lines
        br.readLine(); //should some data be here?
        br.readLine();
        String date = br.readLine();
        String time = br.readLine();
        int numMeasurements = Integer.valueOf(br.readLine());
        int frequency = Integer.valueOf(br.readLine());
        ArrayList<Integer> measurementLength = new ArrayList<Integer>(10);
        String daten = "Daten";
        String line;        
        while((line = br.readLine()).equals(daten)){
            measurementLength.add(Integer.valueOf(line));
        }
        
        dataset.setName(parseName(file));

    }

    private String parseName(File f) {
        String name = f.getName();
        //remove extension
        int pos = name.indexOf('.');
        if (pos != -1) {
            name = name.substring(0, pos);
        }
        //remove folders names, if any before filename
        pos = name.lastIndexOf("/");
        if (pos != -1) {
            name = name.substring(pos, name.length());
        }
        return name;
    }

    private void parseData(BufferedReader br) throws IOException {
        String current = br.readLine();
        if (!current.equals("@data")) {
            throw new RuntimeException("Unexpected line: " + current);
        }

        current = br.readLine();
        PosturomedInstance inst;
        String[] measurements;
        int j = 0;
        int row, col;
        while (!current.isEmpty()) {
            inst = new PosturomedInstance(dataset, timesCount);
            measurements = current.split(",");
            for (int i = 0; i < timesCount; i++) {
                inst.put(Integer.valueOf(measurements[i]));
            }
            //last one is name
            inst.setName(measurements[measurements.length - 1]);
            inst.setId(String.valueOf(j));

            dataset.add(inst);
            //update progress bar
            ph.progress(workUnits++);
            current = br.readLine();
            j++;
        }
    }

    public int translatePosition(int ord, int col, int colCnt) throws IOException {
        int res = ord * colCnt + col - 1;
        return res;
    }

    public PosturomedDataset getDataset() {
        return dataset;
    }
}
