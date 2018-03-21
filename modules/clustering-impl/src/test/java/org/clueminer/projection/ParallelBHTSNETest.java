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
package org.clueminer.projection;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public class ParallelBHTSNETest<E extends Instance> {

    private final ParallelBHTSNE subject;
    private final int initialDims;
    private static final double perplexity = 20.0;
    private static final Logger LOG = LoggerFactory.getLogger(BHTSNETest.class);

    public ParallelBHTSNETest() {
        this.initialDims = 50;
        subject = new ParallelBHTSNE();
    }

    @Test
    public void testIris() {
        Dataset<E> dataset = (Dataset<E>) FakeClustering.irisDataset();
        double data[][] = dataset.arrayCopy();
        assertEquals(150, data.length);
        assertEquals(4, data[0].length);

        TSNEConfig config = new TSNEConfigImpl(data, 2, initialDims, perplexity, 1000);

        double[][] Y = subject.tsne(config);
        LOG.debug("output dim: {}x{}", Y.length, Y[0].length);

    }

}
