/*
 * Copyright (C) 2011-2016 clueminer.org
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
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.exception.ParserError;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.ARFFHandler;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class AbstractNNTest {

    protected static final double DELTA = 1e-9;
    protected Dataset<? extends Instance> irisData;
    protected Dataset<? extends Instance> insectData;

    public Dataset<? extends Instance> insectDataset() {
        if (insectData == null) {
            CommonFixture tf = new CommonFixture();
            insectData = new ArrayDataset(30, 3);
            ARFFHandler arff = new ARFFHandler();
            try {
                arff.load(tf.insectArff(), insectData, 3);
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException | ParserError ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return insectData;
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
            } catch (IOException | ParserError ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return irisData;
    }

}
