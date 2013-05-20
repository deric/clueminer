package org.clueminer.clustering.benchmark;

import java.util.LinkedList;
import java.util.List;
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
 * @author deric
 */
public class AttributeEvolution {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {


        ClusterEvaluatorFactory factory = ClusterEvaluatorFactory.getDefault();
        ExternalEvaluator ext = new JaccardIndex();
        List<Dataset<Instance>> datasets = new LinkedList();
        datasets.add(DatasetFixture.iris());
        datasets.add(DatasetFixture.wine());
        datasets.add(DatasetFixture.yeast());
        Evolution test;
        String name;
        for (Dataset<Instance> dataset : datasets) {
            name = dataset.getName();
            System.out.println("=== dataset " + name);
            for (ClusterEvaluator eval : factory.getAll()) {
                System.out.println("evaluator: " + eval.getName());
                test = new Evolution(dataset, 20);
                test.setAlgorithm(new KMeans(3, 50, new EuclideanDistance()));
                test.setEvaluator(eval);
                test.setExternal(ext);
           //     GnuplotWriter gw = new GnuplotWriter(test, benchmarkFolder, name + "/" + name + "-" + safeName(eval.getName()));
             //   gw.setPlotDumpMod(50);
                //collect data from evolution
                //test.addEvolutionListener(new ConsoleDump(ext));
            //    test.addEvolutionListener(gw);
             //   test.addEvolutionListener(rc);
                test.run();
            }
        }

    }
}
