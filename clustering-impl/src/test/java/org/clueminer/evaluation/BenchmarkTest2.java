package org.clueminer.evaluation;

import au.com.bytecode.opencsv.CSVWriter;
import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.dataset.ArrayDataSet;
import com.panayotis.gnuplot.plot.AbstractPlot;
import com.panayotis.gnuplot.plot.ExternalDataSetPlot;
import com.panayotis.gnuplot.plot.Graph;
import com.panayotis.gnuplot.style.ColorPalette;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.terminal.ImageTerminal;
import com.panayotis.iodebug.Debug;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import javax.imageio.ImageIO;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.ARFFHandler;
import org.clueminer.io.CsvLoader;
import org.clueminer.io.FileHandler;
import org.clueminer.utils.DatasetWriter;
import org.clueminer.utils.FileUtils;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * To run this file you have to have a gnuplot packgage installed on your system
 *
 * @author Tomas Barton
 */
public class BenchmarkTest2 {

    private static Collection<? extends ClusterEvaluator> evaluators;
    private static String benchmarkFolder;
    private static CommonFixture tf;
    private Map<String, String> classColors = new HashMap<String, String>();
    private int colorNum = 0;

    @BeforeClass
    public static void setUpClass() throws Exception {
        evaluators = Lookup.getDefault().lookupAll(ClusterEvaluator.class);
        String home = System.getProperty("user.home") + File.separatorChar
                + NbBundle.getMessage(
                FileUtils.class,
                "FOLDER_Home");
        benchmarkFolder = home + File.separatorChar + "benchmark";
        File f = new File(benchmarkFolder);
        System.out.println("Writing output to " + f.toString());
        if (!f.exists()) {
            boolean success = (new File(benchmarkFolder)).mkdir();
            if (success) {
                System.out.println("Directory: " + benchmarkFolder + " created");
            }
        }
        tf = new CommonFixture();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    private String createFolder(String name) {
        String dir = benchmarkFolder + File.separatorChar + name + File.separatorChar;
        boolean success = (new File(dir)).mkdir();
        if (success) {
            System.out.println("Directory: " + dir + " created");
        }
        return dir;
    }

    private String getClassColor(String clazzName) {
        if (classColors.containsKey(clazzName)) {
            return classColors.get(clazzName);
        }
        String color = ColorPalette.getColor(colorNum++);
        classColors.put(clazzName, color);
        return color;
    }

    public void toCsv(DatasetWriter writer, Clustering<Cluster> clusters, Dataset<Instance> dataset) {
        String[] header = new String[dataset.attributeCount() + 1];
        header[dataset.attributeCount()] = "label";
        int i = 0;
        for (Attribute ta : dataset.getAttributes().values()) {
            header[i++] = String.valueOf(ta.getName());
        }
        writer.writeNext(header);
        for (Cluster<Instance> clust : clusters) {
            for (Instance inst : clust) {
                StringBuilder sb = new StringBuilder(inst.toString(","));
                sb.append(",").append(clust.getName());
                writer.writeLine(sb.toString());
            }
        }

    }

    private double[][] kMeans(Dataset data, int kmin, int kmax, String dir) throws IOException, Exception {
        double[][] results = new double[evaluators.size()][kmax - kmin];
        for (int n = kmin; n < kmax; n++) {
            long start = System.currentTimeMillis();
            ClusteringAlgorithm km = new KMeans(n, 100, new EuclideanDistance());
            Clustering<Cluster> clusters = km.partition(data);
            long end = System.currentTimeMillis();
            System.out.println("measuring k = " + n + " took " + (end - start) + " ms");
            System.out.println("k = " + n);


            String dataDir = dir + "data" + File.separatorChar;
            (new File(dataDir)).mkdir();

            PrintWriter writer = new PrintWriter(dataDir + "/" + n + "data.csv", "UTF-8");
            CSVWriter csv = new CSVWriter(writer);
            toCsv(csv, clusters, data);


            double score;
            int j = 0;
            for (ClusterEvaluator c : evaluators) {
                score = c.score(clusters, data);
                results[j++][n - kmin] = score;
            }
            System.out.println("===========");
        }
        return results;
    }

    private void runExperiment(Dataset data, int kmin, int kmax, int kreal) throws IOException, Exception {
        String dir = createFolder(data.getName());
        long start = System.currentTimeMillis();
        double[][] results = kMeans(data, kmin, kmax, dir);
        long end = System.currentTimeMillis();
        System.out.println("measuring " + data.getName() + " took " + (end - start) + " ms");
        //  plotResults(datasetName, kmin, kmax, results, kreal, dir);
    }

    //@Test
    public void testPlotting() throws IOException, Exception {
        String datasetName = "iris";
        String dir = createFolder(datasetName);
        ARFFHandler arff = new ARFFHandler();
        Dataset data = new SampleDataset();
        data.setName(datasetName);
        arff.load(tf.irisArff(), data, 4);
        data.setName(datasetName);
        int kmin = 2;
        //max k we test
        int kmax = 10;
        double[][] results = kMeans(data, kmin, kmax, dir);
    }

    //@Test
    public void testIris() throws IOException, Exception {
        String datasetName = "iris";
        Dataset data = new SampleDataset();
        data.setName(datasetName);
        ARFFHandler arff = new ARFFHandler();
        arff.load(tf.irisArff(), data, 4);
        assertTrue(150 == data.size());
        int kmin = 2;
        //max k we test
        int kmax = 15;
        int kreal = 3;
        runExperiment(data, kmin, kmax, kreal);
    }

    @Test
    public void testWellSeparated() throws IOException, Exception {
        String datasetName = "well-separated";
        Dataset data = new SampleDataset();
        data.setName(datasetName);
        CsvLoader csv = new CsvLoader();
        csv.load(tf.wellSeparatedCsv(), data, 2, ",", new ArrayList<Integer>());
        assertTrue(1777 == data.size());
        int kmin = 2;
        //max k we test
        int kmax = 6;
        int kreal = 5;
        System.out.println("starting experiment");
        runExperiment(data, kmin, kmax, kreal);
    }

    //  @Test
    public void testWine() throws IOException, Exception {
        String datasetName = "wine";
        // 1st attribute is class identifier (1-3)
        Dataset data = new SampleDataset();
        data.setName(datasetName);
        FileHandler.loadDataset(tf.irisData(), data, 1, ",");
        int kmin = 2;
        //max k we test
        int kmax = 15;
        int kreal = 3;
        runExperiment(data, kmin, kmax, kreal);
    }

    //  @Test
    public void testYeast() throws IOException, Exception {
        String datasetName = "yeast";
        // 10th attribute is class identifier
        ArrayList<Integer> skippedIndexes = new ArrayList<Integer>();
        skippedIndexes.add(0); //we skip instance name
        File file = tf.yeastData();
        Dataset data = new SampleDataset();
        data.setName(datasetName);
        ARFFHandler arff = new ARFFHandler();
        arff.load(file, data, 9, "\\s+", skippedIndexes);
        int kmin = 2;
        //max k we test
        int kmax = 15;
        int kreal = 10;
        runExperiment(data, kmin, kmax, kreal);
    }
}
