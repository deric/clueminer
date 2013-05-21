package org.clueminer.evolution;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.clueminer.cluster.DatasetFixture;
import org.clueminer.clustering.algorithm.HCL;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.ClusterEvaluatorFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.evaluation.BICScore;
import org.clueminer.evaluation.Silhouette;
import org.clueminer.evaluation.external.ExternalEvaluator;
import org.clueminer.evaluation.external.JaccardIndex;
import org.clueminer.evaluation.external.Precision;
import org.clueminer.exception.UnsupportedAttributeType;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.ARFFHandler;
import org.clueminer.utils.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.NbBundle;

/**
 *
 * @author tombart
 */
public class EvolutionTest {

    private static CommonFixture tf = new CommonFixture();
    private static Dataset<Instance> irisDataset;
    private Evolution test;
    //table for keeping results from experiments
    private Table<String, String, Double> table;
    private static ResultsCollector rc;
    private static String benchmarkFolder;
    private static String csvOutput;

    public EvolutionTest() {
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

        benchmarkFolder = home + File.separatorChar + "benchmark";
        rc = new ResultsCollector(table);
        csvOutput = benchmarkFolder + File.separatorChar + "results.csv";
    }

    @BeforeClass
    public static void setUpClass() throws FileNotFoundException, UnsupportedAttributeType, IOException {
        irisDataset = DatasetFixture.iris();
    }

    @AfterClass
    public static void tearDownClass() {

        rc.writeToCsv(csvOutput);
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of attributesCount method, of class Evolution.
     */
    @Test
    public void testAttributesCount() {
    }

    /**
     * Test of getDataset method, of class Evolution.
     */
    @Test
    public void testGetDataset() {
    }

    /**
     * Test of run method, of class Evolution.
     */
    //  @Test
    public void testRun() {
        test = new Evolution(irisDataset, 50);
        test.setAlgorithm(new KMeans(3, 100, new EuclideanDistance()));
        test.setEvaluator(new BICScore());
        ExternalEvaluator ext = new JaccardIndex();
        test.setExternal(ext);
        //collect data from evolution
        GnuplotWriter gw = new GnuplotWriter(test, benchmarkFolder, "iris-evolution");
        gw.setPlotDumpMod(1);
        test.addEvolutionListener(gw);
        //test.addEvolutionListener(new ConsoleDump(ext));
        //test.setEvaluator(new JaccardIndex());
        test.run();
    }

    // @Test
    public void testInformed() {
        //test run with informed metric
        test = new Evolution(irisDataset, 50);
        test.setAlgorithm(new KMeans(3, 100, new EuclideanDistance()));
        ExternalEvaluator ext = new JaccardIndex();
        test.setEvaluator(ext);
        test.setExternal(ext);
        //collect data from evolution
        test.addEvolutionListener(new ConsoleDump());
        test.addEvolutionListener(new GnuplotWriter(test, benchmarkFolder, "iris-evolution-informed"));
        //test.setEvaluator(new JaccardIndex());
        test.run();
    }
    
   // @Test
    public void testVariousMeasuresAndDatasets() {
        ClusterEvaluatorFactory factory = ClusterEvaluatorFactory.getDefault();
        ExternalEvaluator ext = new JaccardIndex();
        List<Dataset<Instance>> datasets = new LinkedList();
      //  datasets.add(irisDataset);
        //datasets.add(DatasetFixture.wine());
        datasets.add(DatasetFixture.yeast());
       // datasets.add(DatasetFixture.yeast());
        
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
                GnuplotWriter gw = new GnuplotWriter(test, benchmarkFolder, name + "/" + name + "-" + safeName(eval.getName()));
                gw.setPlotDumpMod(50);
                //collect data from evolution
                //test.addEvolutionListener(new ConsoleDump(ext));
                test.addEvolutionListener(gw);
                test.addEvolutionListener(rc);
                test.run(); 
                rc.writeToCsv(csvOutput);
            }

        }
    }

    @Test
    public void testSilhouette() {
        ExternalEvaluator ext = new JaccardIndex();
        ClusterEvaluator eval = new Silhouette();
        Dataset<Instance> dataset = DatasetFixture.yeast();
        String name = dataset.getName();
        System.out.println("evaluator: " + eval.getName());
        test = new Evolution(dataset, 50);
        test.setAlgorithm(new KMeans(3, 100, new EuclideanDistance()));
        test.setEvaluator(eval);
        test.setExternal(ext);
        GnuplotWriter gw = new GnuplotWriter(test, benchmarkFolder, name + "/" + name + "-" + safeName(eval.getName()));
        gw.setPlotDumpMod(50);
        //collect data from evolution
        //test.addEvolutionListener(new ConsoleDump(ext));
        test.addEvolutionListener(gw);
        //test.setEvaluator(new JaccardIndex());
        test.run();
    }

    private String safeName(String name) {
        return name.toLowerCase().replace(" ", "_");
    }

    /**
     * Test of getMutationProbability method, of class Evolution.
     */
    @Test
    public void testGetMutationProbability() {
    }

    /**
     * Test of setMutationProbability method, of class Evolution.
     */
    @Test
    public void testSetMutationProbability() {
    }

    /**
     * Test of getCrossoverProbability method, of class Evolution.
     */
    @Test
    public void testGetCrossoverProbability() {
    }

    /**
     * Test of setCrossoverProbability method, of class Evolution.
     */
    @Test
    public void testSetCrossoverProbability() {
    }

    /**
     * Test of getAlgorithm method, of class Evolution.
     */
    @Test
    public void testGetAlgorithm() {
    }

    /**
     * Test of setAlgorithm method, of class Evolution.
     */
    @Test
    public void testSetAlgorithm() {
    }
}