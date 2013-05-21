package org.clueminer.dataset.benchmark;

import com.google.common.collect.Table;
import java.io.File;
import java.util.Map;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.ClusterEvaluatorFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.evaluation.external.ExternalEvaluator;
import org.clueminer.evaluation.external.JaccardIndex;
import org.clueminer.evolution.Evolution;

/**
 *
 * @author tombart
 */
public class Run {

    private Evolution test;
    //table for keeping results from experiments
    private Table<String, String, Double> table;
    private static ResultsCollector rc;
    private static String benchmarkFolder;
    private static String csvOutput;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int i = 0, j;
        String arg;
        char flag;
        boolean vflag = false;
        String outputfile = "";

        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];

            // use this type of check for "wordy" arguments
            if (arg.equals("-verbose")) {
                System.out.println("verbose mode on");
                vflag = true;
            } // use this type of check for arguments that require arguments
            else if (arg.equals("-dataset")) {
                if (i < args.length) {
                    outputfile = args[i++];
                } else {
                    System.err.println("-dataset requires a name");
                }
                if (vflag) {
                    System.out.println("dataset = " + outputfile);
                }
            } // use this type of check for a series of flag arguments
            else {
                for (j = 1; j < arg.length(); j++) {
                    flag = arg.charAt(j);
                    switch (flag) {
                        case 'x':
                            if (vflag) {
                                System.out.println("Option x");
                            }
                            break;
                        case 'n':
                            if (vflag) {
                                System.out.println("Option n");
                            }
                            break;
                        default:
                            System.err.println("Run: illegal option " + flag);
                            break;
                    }
                }
            }
        }
        if (i == args.length) {
            System.err.println("Usage: Benchmark [-verbose] [-xn] [-dataset name]");
        }
    }

    public void run() {
        Map<Dataset<Instance>, Integer> datasets = DatasetFixture.allDatasets();

        ClusterEvaluatorFactory factory = ClusterEvaluatorFactory.getDefault();
        ExternalEvaluator ext = new JaccardIndex();

        String name;

        for (Map.Entry<Dataset<Instance>, Integer> entry : datasets.entrySet()) {
            Dataset<Instance> dataset = entry.getKey();
            name = dataset.getName();
            System.out.println("=== dataset " + name);
            System.out.println("size: " + dataset.size());
            System.out.println(dataset.toString());
            String dataDir = benchmarkFolder + File.separatorChar + name;
            (new File(dataDir)).mkdir();
            for (ClusterEvaluator eval : factory.getAll()) {
                System.out.println("evaluator: " + eval.getName());
                test = new Evolution(dataset, 20);
                test.setAlgorithm(new KMeans(entry.getValue(), 100, new EuclideanDistance()));
                test.setEvaluator(eval);
                test.setExternal(ext);
                GnuplotWriter gw = new GnuplotWriter(test, benchmarkFolder, name + "/" + name + "-" + safeName(eval.getName()));
                gw.setPlotDumpMod(50);
                //collect data from evolution
                test.addEvolutionListener(new ConsoleDump());
                test.addEvolutionListener(gw);
                test.addEvolutionListener(rc);
                test.run();
            }
            String csvRes = benchmarkFolder + File.separatorChar + name + File.separatorChar + name + ".csv";
            rc.writeToCsv(csvRes);

        }
    }

    private String safeName(String name) {
        return name.toLowerCase().replace(" ", "_");
    }
}
