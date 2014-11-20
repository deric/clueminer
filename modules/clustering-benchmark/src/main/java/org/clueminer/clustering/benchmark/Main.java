package org.clueminer.clustering.benchmark;

import org.clueminer.clustering.benchmark.exp.Data;
import org.clueminer.clustering.benchmark.exp.HclusPar;
import org.clueminer.clustering.benchmark.exp.HclusPar2;
import org.clueminer.clustering.benchmark.exp.Hclust;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author deric
 */
public class Main {

    private static final Map<String, Bench> map = new HashMap<>();
    private static Main instance;

    public Main() {
        map.put("hclust", new Hclust());
        map.put("data", new Data());
        map.put("hclust-par", new HclusPar());
        map.put("hclust-par2", new HclusPar2());
    }

    /**
     * Entrypoint to all experiments
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (instance == null) {
            instance = new Main();
        }
        if (args.length == 0) {
            usage();
        }
        String exp = args[0];
        if (!Main.map.containsKey(exp)) {
            usage();
        }

        String[] other = Arrays.copyOfRange(args, 1, args.length);
        Bench bench = Main.map.get(exp);
        //run it
        bench.main(other);
    }

    private static void usage() {
        System.out.println("Usage: java -jar {jar name} [experiment name] [[optional arguments]]");
        System.out.println("Valid experriments values are:");
        for (String key : map.keySet()) {
            for (int i = 0; i < 5; i++) {
                System.out.print(" ");
            }
            System.out.print("- " + key + "\n");
        }
        System.out.println("use '[experiment] --help' to find out more about optional arguments");
        System.out.println("--------------------------");
        System.exit(1);
    }

}
