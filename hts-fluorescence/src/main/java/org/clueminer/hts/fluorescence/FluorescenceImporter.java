package org.clueminer.hts.fluorescence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
public class FluorescenceImporter implements LongTask, Runnable {

    private File file;
    private int timesCount = 0;
    private int workUnits = 0;
    private ProgressHandle ph;
    private FluorescenceDataset plate;

    public FluorescenceImporter(File file) {
        this.file = file;
    }

    public FluorescenceImporter(File file, ProgressHandle ph) {
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
                parseVersion(br);                
                parseData(parseAttributes(br), br);

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

    private void parseVersion(BufferedReader br) throws IOException {
        String current = br.readLine();
        if (!current.equals("% Fluorescence version 1.0")) {
            throw new RuntimeException("Version '" + current + "' is not supported");
        }
        current = br.readLine();
        Pattern p = Pattern.compile("% plate (\\d+) = (\\d+)x(\\d+) \\((\\d+) times\\)");
        Matcher m = p.matcher(current);
        if (m.matches()) {
            //int plateSize = Integer.valueOf(m.group(1));
            int rowCount = Integer.valueOf(m.group(2));
            int columnCount = Integer.valueOf(m.group(3));
            timesCount = Integer.valueOf(m.group(4));
            plate = new FluorescenceDataset(rowCount, columnCount);
            plate.setName(parseName(file));
            //set work units to do
            ph.start(rowCount * columnCount);
        } else {
            throw new RuntimeException("Unexpected line in header: '" + current + "'");
        }
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

    private void parseData(String current, BufferedReader br) throws IOException {        
        if (!current.equals("@data")) {
            throw new RuntimeException("Unexpected line: " + current);
        }

        current = br.readLine();
        FluorescenceInstance inst;
        String[] measurements;
        int j = 0;
        int row, col;
        while (!current.isEmpty()) {
            inst = new FluorescenceInstance(plate, timesCount);
            measurements = current.split(",");
            for (int i = 0; i < timesCount; i++) {
                inst.put(Integer.valueOf(measurements[i]));
            }
            //last one is name
            inst.setName(measurements[measurements.length - 1]);
            inst.setId(String.valueOf(j));
            //id of row
            row = j / plate.getColumnsCount();
            inst.setRow(row);
            col = j - (row * plate.getColumnsCount());
            inst.setColumn(col);
            plate.add(inst);
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

    private String parseAttributes(BufferedReader br) throws IOException {
        String current = br.readLine();
        Pattern p = Pattern.compile("@relation (.*)");
        Matcher m;
        m = p.matcher(current);
        while (!m.matches()) {
            current = br.readLine();
            m = p.matcher(current);
        }
        Pattern attr = Pattern.compile("@attribute ([0-9]+) (\\w+)(.*)");
        TimePointAttribute[] timePoints = new TimePointAttribute[timesCount];
        int i = 0;
        long time;
        do {
            current = readLineWithoutComments(br);
            m = attr.matcher(current);
            if (m.matches()) {
                if (!m.group(1).equals("name")) {
                    time = Integer.valueOf(m.group(1));
                    timePoints[i] = new TimePointAttribute(i++, time);
                }
            }
        } while (m.matches());
        plate.setTimePoints(timePoints);
        return current;
    }

    private String readLineWithoutComments(BufferedReader br) throws IOException {
        String line = br.readLine();
        int pos = line.indexOf('%');
        if (pos != -1) {
            line = line.substring(0, pos);
        }
        return line;
    }

    public FluorescenceDataset getDataset() {
        return plate;
    }
}
