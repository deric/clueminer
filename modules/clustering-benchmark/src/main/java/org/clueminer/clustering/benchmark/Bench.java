package org.clueminer.clustering.benchmark;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import it.unimi.dsi.fastutil.objects.AbstractObject2ObjectMap;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.benchmark.DatasetFixture;

/**
 *
 * @author Tomas Barton
 */
public abstract class Bench {

    protected static String benchmarkFolder;
    protected HashMap<String, Map.Entry<Dataset<? extends Instance>, Integer>> availableDatasets = new HashMap<>();

    public Bench() {
        //constructor without arguments
    }

    public static void ensureFolder(String folder) {
        File file = new File(folder);
        if (!file.exists()) {
            if (file.mkdirs()) {
                System.out.println("Directory " + folder + " created!");
            } else {
                System.out.println("Failed to create " + folder + "directory!");
            }
        }
    }

    public abstract void main(String[] args);

    public static void printUsage(String[] args, JCommander cmd, AbsParams params) {

        try {
            cmd.parse(args);

        } catch (ParameterException ex) {
            System.out.println(ex.getMessage());
            cmd.usage();
            System.exit(0);
        }
    }

    protected void loadDatasets() {
        Map<Dataset<? extends Instance>, Integer> datasets = DatasetFixture.allDatasets();
        for (Map.Entry<Dataset<? extends Instance>, Integer> entry : datasets.entrySet()) {
            Dataset<? extends Instance> d = entry.getKey();
            availableDatasets.put(d.getName(), entry);
        }
    }

    protected void loadIris() {
        Dataset<? extends Instance> d = DatasetFixture.iris();
        Map.Entry<Dataset<? extends Instance>, Integer> entry
                = (Map.Entry<Dataset<? extends Instance>, Integer>) new AbstractObject2ObjectMap.BasicEntry<>(d, 3);

        availableDatasets.put(d.getName(), entry);

    }

    public static String safeName(String name) {
        return name.toLowerCase().replace(" ", "_");
    }

}
