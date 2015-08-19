/*
 * Copyright (C) 2011-2015 clueminer.org
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
package org.clueminer.eval;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.colors.RandomColorsGenerator;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.io.ARFFHandler;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class RatkowskyLanceTest {

    private static RatkowskyLance subject;
    private static final double delta = 1e-9;

    public RatkowskyLanceTest() {
        subject = new RatkowskyLance();
    }

    public static Dataset<? extends Instance> irisDataset() {
        CommonFixture tf = new CommonFixture();
        Dataset<? extends Instance> irisData = new ArrayDataset(150, 4);
        irisData.setName("iris");
        ARFFHandler arff = new ARFFHandler();
        try {
            arff.load(tf.irisArff(), irisData, 4);
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return irisData;
    }

    public static Clustering iris(Dataset<? extends Instance> irisData) {
        ColorGenerator cg = new RandomColorsGenerator();

        /**
         * fictive clustering, create iris cluster based on class labels (the
         * dataset is sorted)
         */
        Clustering irisClusters = new ClusterList(3);
        Cluster a = new BaseCluster(50);
        a.setColor(cg.next());
        a.setName("cluster 1");
        a.setAttributes(irisData.getAttributes());
        Cluster b = new BaseCluster(50);
        b.setName("cluster 2");
        b.setAttributes(irisData.getAttributes());
        b.setColor(cg.next());
        Cluster c = new BaseCluster(50);
        c.setName("cluster 3");
        c.setColor(cg.next());
        c.setAttributes(irisData.getAttributes());
        for (int i = 0; i < 50; i++) {
            a.add(irisData.instance(i));
            b.add(irisData.instance(i + 50));
            c.add(irisData.instance(i + 100));
        }

        irisClusters.add(a);
        irisClusters.add(b);
        irisClusters.add(c);
        //add dataset to lookup
        irisClusters.lookupAdd(irisData);

        return irisClusters;
    }

    /**
     * TODO global iris fixture is being modified by some test, thus this test
     * would fail with
     * {@link org.clueminer.fixtures.clustering.FakeDatasets.irisDataset()}
     */
    @Test
    public void sameStatsAsPrecomputed() {
        Dataset<? extends Instance> irisData = irisDataset();
        Clustering iris = iris(irisData);
        double scorePrecomp = subject.score(iris);
        iris.lookupRemove(irisData);
        double sc = subject.score(iris);

        assertEquals(scorePrecomp, sc, delta);
    }

    @Test
    public void testIris() {
        double scoreBetter = subject.score(FakeClustering.iris());
        double scoreWorser = subject.score(FakeClustering.irisWrong4());

        //should recognize better clustering
        assertEquals(true, subject.isBetter(scoreBetter, scoreWorser));
    }

    /**
     * Check against definition (and tests in R package clusterCrit)
     * https://cran.r-project.org/web/packages/clusterCrit/index.html
     *
     * NOTE: There's a small problem with precision of floating point
     * operations. First 7 decimal digits seems to match.
     */
    @Test
    public void testClusterCrit() {
        double score = subject.score(FakeClustering.int100p4());
        //clustCrit: 0.491870539886729
        //TODO: verify the implementation
        assertEquals(0.4826635728768826, score, delta);
    }

}
