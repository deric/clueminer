package org.clueminer.xcalibour.files;

import org.clueminer.xcalibour.data.SpectrumDataset;
import org.clueminer.xcalibour.data.MassSpectrum;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class Convertor {

    private static Convertor convertor;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String input = null, output = "output.csv";
        int i = 0, j;
        String arg;
        boolean vflag = false;

        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];

            // use this type of check for "wordy" arguments
            if (arg.equals("-verbose")) {
                System.out.println("verbose mode on");
                vflag = true;
            } // use this type of check for arguments that require arguments
            else if (arg.equals("-input")) {
                if (i < args.length) {
                    input = args[i++];
                } else {
                    System.err.println("-input requires a filename");
                }
                if (vflag) {
                    System.out.println("input = " + input);
                }
            } else if (arg.equals("-output")) {
                if (i < args.length) {
                    output = args[i++];
                } else {
                    System.err.println("-output requires a filename");
                }
                if (vflag) {
                    System.out.println("output = " + output);
                }
            }

        }
        if (i != args.length) {
            System.err.println("Usage: Convertor -input file -output file");
        }

        if (input != null && output != null) {
            convertor = new Convertor(input, output);
        } else {
            System.err.println("no input data");
        }

    }

    public Convertor(String input, String output) {
        SpectrumDataset<MassSpectrum> dataset = null;
        try {
            dataset = loadDataset(input);
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }

        if (dataset == null) {
            System.err.println("failed to load dataset: " + input);
        } else {
            System.out.println("dataset size: " + dataset.size());
            writeToFile(output, dataset);
        }
    }

    private synchronized SpectrumDataset<MassSpectrum> loadDataset(String input) throws InterruptedException {
        ProgressHandle ph = ProgressHandleFactory.createHandle("Importing dataset");
        XCalibourImporter importer;

        File file = new File(input);
        if (!file.exists()) {
            throw new RuntimeException("File '" + input + "' does not exists");
        }
        importer = new XCalibourImporter(file);
        Thread thread = new Thread(importer);
        thread.start();
        //
        //importer.setProgressHandle(ph);
        //importer.run();

        System.out.println("Waiting for thread to finish");
        // loop until MessageLoop
        // thread exits
        while (thread.isAlive()) {

            // Wait maximum of 1 second
            // for MessageLoop thread
            // to finish.
            // System.out.print(".");
            Thread.sleep(100);
        }
        System.out.println("finished!");


        return importer.getDataset();
    }

    private void writeToFile(String output, SpectrumDataset<MassSpectrum> dataset) {
        PrintWriter writer = null;
        try {
            System.out.println("writing to " + output);
            writer = new PrintWriter(output, "UTF-8");
            double sum;
            MassSpectrum mass;
            for (int t = 0; t < dataset.size(); t++) {
                mass = dataset.get(t);
                sum = 0.0;
                for (int i = 0; i < mass.size(); i++) {
                    sum += mass.item(i).getIntensity();
                }
                StringBuilder sb = new StringBuilder();
                sb.append(dataset.getTimePoint(t).getPosition());
                sb.append(',');
                sb.append(sum).append("\n");
                writer.write(sb.toString());
            }
            writer.close();

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            writer.close();
        }
    }
}
