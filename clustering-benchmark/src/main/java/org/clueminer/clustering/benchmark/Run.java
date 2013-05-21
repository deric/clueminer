package org.clueminer.clustering.benchmark;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.ClusterEvaluatorFactory;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.benchmark.ConsoleDump;
import org.clueminer.dataset.benchmark.DatasetFixture;
import org.clueminer.dataset.benchmark.GnuplotWriter;
import org.clueminer.dataset.benchmark.ResultsCollector;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.evaluation.external.ExternalEvaluator;
import org.clueminer.evaluation.external.JaccardIndex;
import org.clueminer.evolution.AttrEvolution;
import org.clueminer.utils.FileUtils;
import org.openide.util.NbBundle;

/**
 *
 * @author tombart
 */
public class Run {

    private Evolution test;
    //table for keeping results from experiments
    private Table<String, String, Double> table;
    private HashMap<String, Map.Entry<Dataset<Instance>, Integer>> availableDatasets = new HashMap<String, Map.Entry<Dataset<Instance>, Integer>>();
    private static ResultsCollector rc;
    private static String benchmarkFolder;
    private static String csvOutput;
    private static Run instance;

    public Run() {
        table = Tables.newCustomTable(
                Maps.<String, Map<String, Double>>newHashMap(),
                new Supplier<Map<String, Double>>() {
            @Override
            public Map<String, Double> get() {
                return Maps.newHashMap();
            }
        });

        String home = System.getProperty("user.home") + File.separatorChar
                + NbBundle.getMessage(
                FileUtils.class,
                "FOLDER_Home");
        createFolder(home);
        benchmarkFolder = home + File.separatorChar + "benchmark";
        createFolder(benchmarkFolder);
        rc = new ResultsCollector(table);
        csvOutput = benchmarkFolder + File.separatorChar + "results.csv";

        //preload dataset names
        Map<Dataset<Instance>, Integer> datasets = DatasetFixture.allDatasets();
        for (Map.Entry<Dataset<Instance>, Integer> entry : datasets.entrySet()) {
            Dataset<Instance> d = entry.getKey();
            availableDatasets.put(d.getName(), entry);
        }
    }

    private void createFolder(String folder) {
        File file = new File(folder);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory " + folder + " created!");
            } else {
                System.out.println("Failed to create " + folder + "directory!");
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int i = 0, j;
        String arg;
        char flag;
        boolean vflag = false;
        String datasetName = "";

        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];

            // use this type of check for "wordy" arguments
            if (arg.equals("-verbose")) {
                System.out.println("verbose mode on");
                vflag = true;
            } // use this type of check for arguments that require arguments
            else if (arg.equals("-dataset")) {
                if (i < args.length) {
                    datasetName = args[i++];
                } else {
                    System.err.println("-dataset requires a name");
                }
                if (vflag) {
                    System.out.println("dataset = " + datasetName);
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

        instance = new Run();
        instance.execute(datasetName);
    }

    public void execute(String datasetName) {
        Map<Dataset<Instance>, Integer> datasets = new HashMap<Dataset<Instance>, Integer>();
        if (availableDatasets.containsKey(datasetName)) {
            Map.Entry<Dataset<Instance>, Integer> entry = availableDatasets.get(datasetName);
            datasets.put(entry.getKey(), entry.getValue());
        } else {
            System.out.println("dataset " + datasetName + " not found");
            System.out.println("known datasets: ");
            for (Dataset<Instance> d : datasets.keySet()) {
                System.out.print(d.getName() + " ");
            }
        }
        // DatasetFixture.allDatasets();

        ClusterEvaluatorFactory factory = ClusterEvaluatorFactory.getDefault();
        ExternalEvaluator ext = new JaccardIndex();

        String name;
        System.out.println("working folder: " + benchmarkFolder);
        for (Map.Entry<Dataset<Instance>, Integer> entry : datasets.entrySet()) {
            Dataset<Instance> dataset = entry.getKey();
            name = dataset.getName();
            String csvRes = benchmarkFolder + File.separatorChar + name + File.separatorChar + name + ".csv";
            System.out.println("=== dataset " + name);
            System.out.println("size: " + dataset.size());
            System.out.println(dataset.toString());
            String dataDir = benchmarkFolder + File.separatorChar + name;
            (new File(dataDir)).mkdir();
            for (ClusterEvaluator eval : factory.getAll()) {
                System.out.println("evaluator: " + eval.getName());
                test = new AttrEvolution(dataset, 20);
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
                rc.writeToCsv(csvRes);
            }
        }
    }

    private String safeName(String name) {
        return name.toLowerCase().replace(" ", "_");
    }
}
