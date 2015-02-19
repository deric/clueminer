/*
 * Copyright (C) 2015 clueminer.org
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
package org.clueminer.knn;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.ARFFHandler;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class CachingKNNTest {

    private CachingKNN subject;
    private Dataset<? extends Instance> irisData;
    private Dataset<? extends Instance> insectData;

    public CachingKNNTest() {
    }

    @Before
    public void setUp() {
        subject = new CachingKNN();
    }

    @Test
    public void testNnIds() {
    }

    @Test
    public void testNn() {
        Dataset<? extends Instance> d = irisDataset();
        int k = 5;
        Instance[] nn = subject.nn(0, k, d, new Props());
        assertEquals(k, nn.length);

    }

    public Dataset<? extends Instance> insectDataset() {
        if (insectData == null) {
            CommonFixture tf = new CommonFixture();
            insectData = new ArrayDataset(30, 3);
            ARFFHandler arff = new ARFFHandler();
            try {
                arff.load(tf.insectArff(), irisData, 3);
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return irisData;
    }

    public Dataset<? extends Instance> irisDataset() {
        if (irisData == null) {
            CommonFixture tf = new CommonFixture();
            irisData = new ArrayDataset(150, 4);
            ARFFHandler arff = new ARFFHandler();
            try {
                arff.load(tf.irisArff(), irisData, 4);
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return irisData;
    }

}
