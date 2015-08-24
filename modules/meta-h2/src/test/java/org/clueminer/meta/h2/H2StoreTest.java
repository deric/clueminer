package org.clueminer.meta.h2;

import java.sql.SQLException;
import java.util.Collection;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.factory.EvaluationFactory;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.external.Precision;
import org.clueminer.eval.utils.HashEvaluationTable;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.meta.api.MetaResult;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class H2StoreTest {

    private H2Store subject;
    private static final String testDb = "unit-test";

    public H2StoreTest() {
    }

    @Before
    public void setUp() {
        subject = H2Store.getInstance();

        subject.db(testDb);
    }

    @After
    public void tearDown() {
        try {
            subject.close();
            subject.deleteDb(testDb);
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Test
    public void testFetchDataset() {
        int id = subject.fetchDataset(FakeDatasets.irisDataset());
        assertEquals(true, id > 0);
    }

    @Test
    public void testFetchPartitioning() {
        int datasetId = subject.fetchDataset(FakeDatasets.irisDataset());
        int id = subject.fetchPartitioning(datasetId, FakeClustering.iris());
        assertEquals(true, id > 0);
    }

    @Test
    public void testAdd() {
        subject.add(FakeDatasets.irisDataset(), FakeClustering.irisWrong2());
    }

    @Test
    public void testFindScore() {
        int datasetId = subject.fetchDataset(FakeDatasets.irisDataset());
        int pid = subject.fetchPartitioning(datasetId, FakeClustering.iris());

        ClusterEvaluation e = EvaluationFactory.getInstance().getDefault();
        //TODO: return 0.0 when no record was found
        /*double score = subject.findScore(FakeDatasets.irisDataset(),
         FakeClustering.iris(), e);
         assertNotSame(Double.NaN, score);*/
    }

    @Test
    public void testClose() throws Exception {
    }

    @Test
    public void testFetchEvolution() {
        int id = subject.fetchEvolution("test evolution");
        assertEquals(true, id > 0);
    }

    @Test
    public void testFetchAlgorithm() {
        int id = subject.fetchAlgorithm("my alg");
        assertEquals(true, id > 0);
    }

    @Test(expected = RuntimeException.class)
    public void testFetchRun() {
        //should throw an exception when record is not found
        subject.findRunsDataset(9999);
    }

    @Test
    public void testFindResults() {
        Collection<MetaResult> res = subject.findResults(FakeDatasets.irisDataset(), "evo-test", new Precision());
        assertNotNull(res);
    }

    @Test
    public void testAddResult() {
        int datasetId = subject.fetchDataset(FakeDatasets.irisDataset());
        Clustering<Instance, Cluster<Instance>> c = FakeClustering.iris();
        EvaluationTable et = new HashEvaluationTable(c, FakeDatasets.irisDataset());
        et.countAll();
        c.setEvaluationTable(et);
        subject.addClustering(datasetId, c, -1);
    }

}
