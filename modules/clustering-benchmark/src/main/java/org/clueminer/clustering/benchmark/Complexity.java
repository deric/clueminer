package org.clueminer.clustering.benchmark;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author deric
 */
public class Complexity {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BenchParams params = parseArguments(args);
        System.out.println("n = " + params.n);
        System.out.println("=== starting experiment:");
        Experiment exp = new Experiment(params);

        ExecutorService execService = Executors.newFixedThreadPool(1);
        execService.submit(exp);
        System.out.println("=== experiment finished");
    }

    private static BenchParams parseArguments(String[] args) {
        BenchParams params = new BenchParams();
        JCommander cmd = new JCommander(params);
        printUsage(args, cmd);
        return params;
    }

    public static void printUsage(String[] args, JCommander cmd) {
        if (args.length == 0) {
            StringBuilder sb = new StringBuilder();
            cmd.usage(sb);
            sb.append("\n").append("attributes marked with * are mandatory");
            System.out.println(sb);
            System.err.println("missing mandatory arguments");
            System.exit(0);
        }
        try {
            cmd.parse(args);
            /**
             * TODO validate values of parameters
             */
        } catch (ParameterException ex) {
            System.out.println(ex.getMessage());
            cmd.usage();
            System.exit(0);
        }
    }

}
