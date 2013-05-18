package org.clueminer.evolution;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.stats.AttrNumStats;
import org.clueminer.utils.DatasetWriter;
import org.clueminer.utils.FileUtils;
import org.clueminer.utils.PointTypeIterator;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Barton
 */
public class GnuplotWriter implements EvolutionListener {

    private Evolution evolution;
    private Dataset<Instance> dataset;
    private String benchmarkFolder;
    private ClusterEvaluation externalValidation;
    private String outputDir;
    private Table<Integer, Double, Double> results;

    public GnuplotWriter(Evolution evolution, ClusterEvaluation external, String outputDir) {
        this.evolution = evolution;
        this.dataset = evolution.getDataset();
        this.externalValidation = external;
        this.outputDir = outputDir;


        String home = System.getProperty("user.home") + File.separatorChar
                + NbBundle.getMessage(
                FileUtils.class,
                "FOLDER_Home");
        benchmarkFolder = home + File.separatorChar + "benchmark";


        String dataDir = getDataDir(outputDir);
        (new File(dataDir)).mkdir();

        Table<String, String, Integer> table = Tables.newCustomTable(
                Maps.<String, Map<String, Integer>>newHashMap(),
                new Supplier<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> get() {
                return Maps.newHashMap();
            }
        });


    }

    @Override
    public void bestInGeneration(int generationNum, Individual best, double avgFitness) {
        //plotIndividual(generationNum, 1, 2, getDataDir(outputDir), best.getClustering());
        results.put(generationNum, best.getFitness(), avgFitness);
    }

    @Override
    public void finalResult(Individual best, long evolutionTime) {
        
    }

    private void plotIndividual(int n, int x, int y, String dataDir, Clustering<Cluster> clusters) {
        PrintWriter template = null;
        String strn = String.format("%02d", n);
        String dataFile = "data-" + strn + ".csv";
        String scriptFile = "plot-" + strn + ".gpt";

        try {
            PrintWriter writer = new PrintWriter(dataDir + File.separatorChar + dataFile, "UTF-8");
            CSVWriter csv = new CSVWriter(writer, ',');
            toCsv(csv, clusters, dataset);
            writer.close();

            template = new PrintWriter(dataDir + scriptFile, "UTF-8");
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }
        template.write(plotTemplate(n, x, y, clusters, dataFile));
        template.close();
    }

    private String plotTemplate(int k, int x, int y, Clustering<Cluster> clustering, String dataFile) {
        Cluster<Instance> first = clustering.get(0);
        double fitness = evolution.getEvaluator().score(clustering, dataset);
        int attrCnt = first.attributeCount();
        int labelPos = attrCnt + 1;
        //attributes are numbered from zero, gnuplot columns from 1
        double max = first.getAttribute(x - 1).statistics(AttrNumStats.MAX);
        double min = first.getAttribute(x - 1).statistics(AttrNumStats.MIN);
        String xrange = "[" + min + ":" + max + "]";
        max = first.getAttribute(y - 1).statistics(AttrNumStats.MAX);
        min = first.getAttribute(y - 1).statistics(AttrNumStats.MIN);
        String yrange = "[" + min + ":" + max + "]";

        double jacc = externalValidation.score(clustering, dataset);

        String res = "set datafile separator \",\"\n"
                + "set key outside bottom horizontal box\n"
                + "set title \"generation = " + k + ", fitness = " + fitness + ", jacc = " + jacc + "\"\n"
                + "set xlabel \"" + first.getAttribute(x - 1).getName() + "\" font \"Times,7\"\n"
                + "set ylabel \"" + first.getAttribute(y - 1).getName() + "\" font \"Times,7\"\n"
                //   + "set xtics 0,0.5 nomirror\n"
                //   + "set ytics 0,0.5 nomirror\n"
                + "set mytics 2\n"
                + "set mx2tics 2\n"
                //  + "set xrange " + xrange + "\n"
                //  + "set yrange " + yrange + "\n"
                + "set grid\n"
                + "set pointsize 0.5\n";
        int i = 0;
        int last = clustering.size() - 1;
        PointTypeIterator pti = new PointTypeIterator();
        for (Cluster clust : clustering) {
            if (i == 0) {
                res += "plot ";
            }
            res += "\"< awk -F\\\",\\\" '{if($" + labelPos + " == \\\"" + clust.getName() + "\\\") print}' " + dataFile + "\" u " + x + ":" + y + " t \"" + clust.getName() + "\" w p pt " + pti.next();
            if (i != last) {
                res += ", \\\n";
            } else {
                res += "\n";
            }

            i++;
        }
        return res;
    }

    private String getDataDir(String dir) {
        return createFolder(dir) + "data" + File.separatorChar;
    }

    private String createFolder(String name) {
        String dir = benchmarkFolder + File.separatorChar + name + File.separatorChar;
        boolean success = (new File(dir)).mkdir();
        if (success) {
            System.out.println("Directory: " + dir + " created");
        }
        return dir;
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
}
