/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.clustering.struct;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.distance.api.Distance;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.exception.ParserError;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.arff.ARFFHandler;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class DendrogramDataTest {

    private DendrogramData subject;
    private static final CommonFixture TF = new CommonFixture();
    private final Dataset<? extends Instance> iris;
    //private ClusteringAlgorithm algorithm = new HAC();

    public DendrogramDataTest() throws IOException {
        iris = loadIris();

    }

    private Dataset<? extends Instance> loadIris() throws FileNotFoundException, IOException {
        Dataset<? extends Instance> irisData = new ArrayDataset(150, 4);
        ARFFHandler arff = new ARFFHandler();
        try {
            arff.load(TF.irisArff(), irisData, 4);
        } catch (ParserError ex) {
            Exceptions.printStackTrace(ex);
        }
        return irisData;
    }

    @Before
    public void setUp() {
        subject = new DendrogramData();
        subject.setDataset(iris);
    }


    @Test
    public void testSetDataset() {
        assertEquals(iris, subject.getDataset());
    }

    @Test
    public void testIsEmpty() {
        assertFalse(subject.isEmpty());
    }

    @Test
    public void testGetNumberOfRows() {
        assertEquals(150, subject.getNumberOfRows());
    }

    @Test
    public void testGetNumberOfColumns() {
        assertEquals(4, subject.getNumberOfColumns());
    }


    //@Test
    public void testGetDataset() throws IOException {
        Distance dm = DistanceFactory.getInstance().getDefault();
        Dataset<? extends Instance> dataset = loadIris();
        HierarchicalResult rowsResult = hclustRows(dataset, dm, new Props());
        DendrogramMapping mapping = new DendrogramData(dataset, rowsResult);
    }

    public HierarchicalResult hclustRows(Dataset<? extends Instance> dataset, Distance dm, Props params) {
        /*
         HierarchicalResult rowsResult = algorithm.hierarchy(dataset, params);
         CutoffStrategy strategy = getCutoffStrategy(params);
         double cut = rowsResult.findCutoff(strategy);
         params.putDouble(AgglParams.CUTOFF, cut);
         return rowsResult;*/
        return null;
    }


}
