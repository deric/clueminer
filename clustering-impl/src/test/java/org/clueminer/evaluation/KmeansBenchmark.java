package org.clueminer.evaluation;

import au.com.bytecode.opencsv.CSVWriter;
import com.panayotis.gnuplot.style.ColorPalette;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
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
import org.clueminer.stats.AttrNumStats;
import org.clueminer.utils.DatasetWriter;
import org.clueminer.utils.Dump;
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
public class KmeansBenchmark {

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
                writer.writeLine(appendClass(inst, clust.getName()));
            }
        }
    }

    private StringBuilder appendClass(Instance inst, String klass) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < inst.size(); i++) {
            if (i > 0) {
                res.append(',');
            }
            res.append(inst.value(i));
        }
        return res.append(',').append(klass);
    }

    private String plotTemplate(int k, int x, int y, Clustering<Cluster> clustering, String dataFile) {
        Cluster<Instance> first = clustering.get(0);
        int attrCnt = first.attributeCount();
        int labelPos = attrCnt + 1;
        //attributes are numbered from zero, gnuplot columns from 1
        double max = first.getAttribute(x - 1).statistics(AttrNumStats.MAX);
        double min = first.getAttribute(x - 1).statistics(AttrNumStats.MIN);
        String xrange = "[" + min + ":" + max + "]";
        max = first.getAttribute(y - 1).statistics(AttrNumStats.MAX);
        min = first.getAttribute(y - 1).statistics(AttrNumStats.MIN);
        String yrange = "[" + min + ":" + max + "]";

        String res = "set terminal pdf font  \"Times-New-Roman,8\" \n"
                + "set datafile separator \",\"\n"
                + "set key outside bottom horizontal box\n"
                + "set title \"k = " + k + "\"\n"
                + "set xlabel \"" + first.getAttribute(x - 1).getName() + "\" font \"Times,7\"\n"
                + "set ylabel \"" + first.getAttribute(y - 1).getName() + "\" font \"Times,7\"\n"
                + "set xtics 0,0.5 nomirror\n"
                + "set ytics 0,0.5 nomirror\n"
                + "set mytics 2\n"
                + "set mx2tics 2\n"
                + "set xrange " + xrange + "\n"
                + "set yrange " + yrange + "\n"
                + "set grid\n"
                + "set pointsize 0.5\n";
        int i = 0;
        int last = clustering.size() - 1;
        for (Cluster clust : clustering) {
            if (i == 0) {
                res += "plot ";
            }
            res += "\"< awk -F\\\",\\\" '{if($" + labelPos + " == \\\"" + clust.getName() + "\\\") print}' " + dataFile + "\" u " + x + ":" + y + " t \"" + clust.getName() + "\" w p";
            if (i != last) {
                res += ", \\\n";
            } else {
                res += "\n";
            }

            i++;
        }
        return res;
    }

    private String bashTemplate() {
        String res = "#!/bin/bash\n"
                + "cd data\n";
        return res;
    }

    private String getDataDir(String dir) {
        return dir + "data" + File.separatorChar;
    }

    private double[][] kMeans(Dataset data, int kmin, int kmax, String dir, int x, int y) throws IOException, Exception {
        double[][] results = new double[evaluators.size()][kmax - kmin];
        String[] files = new String[kmax - kmin];
        int i = 0;
        for (int n = kmin; n < kmax; n++) {
            long start = System.currentTimeMillis();
            ClusteringAlgorithm km = new KMeans(n, 100, new EuclideanDistance());
            Clustering<Cluster> clusters = km.partition(data);
            long end = System.currentTimeMillis();
            System.out.println("measuring k = " + n + " took " + (end - start) + " ms");
            System.out.println("k = " + n);


            String dataDir = getDataDir(dir);
            (new File(dataDir)).mkdir();
            String dataFile = "data-" + n + ".csv";
            String scriptFile = "plot-" + n + ".gpt";
            files[i] = scriptFile + " > ../" + data.getName() + "-" + n + ".pdf";
            PrintWriter writer = new PrintWriter(dataDir + File.separatorChar + dataFile, "UTF-8");
            CSVWriter csv = new CSVWriter(writer, ',');
            toCsv(csv, clusters, data);
            writer.close();

            PrintWriter template = new PrintWriter(dataDir + scriptFile, "UTF-8");
            template.write(plotTemplate(n, x, y, clusters, dataFile));
            template.close();

            double score;
            int j = 0;
            for (ClusterEvaluator c : evaluators) {
                score = c.score(clusters, data);
                results[j++][n - kmin] = score;
            }
            System.out.println("===========");
            i++;
        }

        //bash script to generate results
        PrintWriter template = new PrintWriter(dir + File.separatorChar + "plot-all", "UTF-8");
        template.write(bashTemplate());
        for (int j = 0; j < files.length; j++) {
            template.write("gnuplot " + files[j] + "\n");
        }
        template.close();
        return results;
    }

    private String[] plotResults(String datasetName, int kmin, int kmax, double[][] results, int kreal, String dir) throws IOException {
        String dataDir = getDataDir(dir);
        String[] plots = new String[evaluators.size()];
        int i = 0;
        for (ClusterEvaluator c : evaluators) {
            System.out.println("evaluator " + c.getName());
            //reformat data for plotting
            double[][] d = new double[kmax - kmin][2];
            for (int n = kmin; n < kmax; n++) {
                d[n - kmin][0] = n;
                d[n - kmin][1] = results[i][n - kmin];
            }
            //Dump.matrix(d, c.getName(), 3);
            String name = c.getName();
            name = name.toLowerCase().replace(" ", "_") + ".gpt";
            PrintWriter template = new PrintWriter(dataDir + name, "UTF-8");
            template.write(plotEvaluation(c.getName(), kreal, d));
            template.close();
            plots[i] = name;
            i++;
        }
        return plots;
    }

    private String plotEvaluation(String evaluator, int k, double[][] score) {

        String res = "set term pdf font 'Times-New-Roman,8'\n"
                + "set title '" + evaluator + "'\n"
                + "set xtics add ('k=" + k + "' " + k + ")\n"
                + "set arrow from " + k + ", graph 0 to " + k + ", graph 1 nohead ls 4 lw 2\n"
                + "set key off\n"
                + "set grid \n"
                + "set size 1.0, 1.0\n"
                + "set ylabel 'score'\n"
                + "set xlabel 'number of clusters'\n"
                + "plot '-' title 'Datafile' with linespoints linewidth 2 pointtype 7 pointsize 0.3 ;\n";
        for (int i = 0; i < score.length; i++) {
            res += String.valueOf(score[i][0]) + " " + String.valueOf(score[i][1]) + " \n";

        }
        res += "e\n";
        return res;
    }

    private void runExperiment(Dataset data, int kmin, int kmax, int kreal, int x, int y) throws IOException, Exception {
        String dir = createFolder(data.getName());
        long start = System.currentTimeMillis();
        double[][] results = kMeans(data, kmin, kmax, dir, x, y);
        long end = System.currentTimeMillis();
        System.out.println("measuring " + data.getName() + " took " + (end - start) + " ms");
        String[] plots = plotResults(data.getName(), kmin, kmax, results, kreal, dir);


        //add file to bash script
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(dir + File.separatorChar + "plot-all", true)));
            int i = 0;
            for (String line : plots) {
                out.write("gnuplot " + line + " > ../eval-" + i + ".pdf\n");
                i++;
            }
            out.close();
        } catch (IOException e) {
            //oh noes!
        }
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
        double[][] results = kMeans(data, kmin, kmax, dir, 1, 2);
    }

    @Test
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
        runExperiment(data, kmin, kmax, kreal, 3, 4);
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
        int kmax = 9;
        int kreal = 5;
        System.out.println("starting experiment");
        runExperiment(data, kmin, kmax, kreal, 1, 2);
    }
/*
    @Test
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

    @Test
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
    }*/
}
