/*
 * Copyright (C) 2011-2017 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.meta.h2;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.factory.EvaluationFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.external.NMIsqrt;
import org.clueminer.eval.external.Precision;
import org.clueminer.eval.utils.HashEvaluationTable;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.meta.api.CostMeasure;
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
 * @param <E>
 */
public class H2StoreTest<E extends Instance> {

    private H2Store subject;
    private static final String TEST_DB = "unit-test";

    public H2StoreTest() {
    }

    @Before
    public void setUp() {
        subject = H2Store.getInstance();

        subject.db(TEST_DB);
    }

    @After
    public void tearDown() {
        try {
            subject.close();
            subject.deleteDb(TEST_DB);
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
    public void testAdd() throws SQLException {
        Dataset<E> dataset = (Dataset<E>) FakeDatasets.irisDataset();
        subject.add(dataset, FakeClustering.irisWrong2());

        int id = subject.fetchDataset(dataset.getName());
        assertEquals(true, id > 0);
        subject.findResults(dataset, new NMIsqrt());
    }

    @Test
    public void testFindScore() {
        int datasetId = subject.fetchDataset(FakeDatasets.irisDataset());
        int pid = subject.fetchPartitioning(datasetId, FakeClustering.iris());

        ClusterEvaluation e = EvaluationFactory.getInstance().getDefault();
        //TODO: return 0.0 when no record was found
/* double score = subject.findScore(FakeDatasets.irisDataset(), FakeClustering.iris(), e);
         * assertNotSame(Double.NaN, score); */
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
        Collection<MetaResult> res = subject.findResults(FakeDatasets.irisDataset(), new Precision(), "evo-test");
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
        Collection<MetaResult> res = subject.findResults(FakeDatasets.irisDataset(), new Precision());
        assertNotNull(res);
        assertEquals(2, res.size());
        res.forEach((r) -> {
            System.out.println(r.getFingerprint() + ": " + r.getScore());
        });
    }

    @Test
    public void testInsertCost() {
        Map<String, Double> map = new HashMap<>();
        map.put("foo", 1.0);
        map.put("bar", 2.0);
        subject.insertCost("test", CostMeasure.TIME, 325.0, map);
    }

}
