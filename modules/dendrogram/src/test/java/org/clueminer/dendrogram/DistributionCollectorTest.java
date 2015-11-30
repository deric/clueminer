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
package org.clueminer.dendrogram;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 * @param <E>
 */
public class DistributionCollectorTest<E extends Instance> {

    private DistributionCollector<E> subject;

    public DistributionCollectorTest() {
        subject = new DistributionCollector<>(50);
    }

    @Test
    public void testDatasetChanged() {
    }

    @Test
    public void testClear() {
        Dataset<E> dataset = (Dataset<E>) FakeDatasets.kumarData();
        subject.datasetChanged(dataset);
        for (int i = 0; i < dataset.size(); i++) {
            for (int j = 0; j < dataset.attributeCount(); j++) {
                subject.sample(dataset.get(i, j));
            }
        }
        assertEquals(12, subject.getNumSamples());
    }

    @Test
    public void testSample() {
        Dataset<E> dataset = (Dataset<E>) FakeDatasets.irisDataset();
        subject.datasetChanged(dataset);
        for (int i = 0; i < dataset.size(); i++) {
            for (int j = 0; j < dataset.attributeCount(); j++) {
                subject.sample(dataset.get(i, j));
            }
        }
        assertEquals(600, subject.getNumSamples());
        //subject.dump();
    }

}
