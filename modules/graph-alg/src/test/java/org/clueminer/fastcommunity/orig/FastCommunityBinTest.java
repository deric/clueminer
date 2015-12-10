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
package org.clueminer.fastcommunity.orig;

import java.io.IOException;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public class FastCommunityBinTest<E extends Instance, C extends Cluster<E>> {

    private static FastCommunityBin subject;

    public FastCommunityBinTest() {
        subject = new FastCommunityBin<>();
    }

    @Test
    public void testCluster() {
        Props props = new Props();
        props.putInt("k", 3);
        Dataset<E> dataset = (Dataset<E>) FakeDatasets.schoolData();
        Clustering<E, C> clust = subject.cluster(dataset, props);
    }

    @Test
    public void testBinary() throws IOException, InterruptedException {
        assertTrue("binary does not exists", subject.getBinary("community").exists());
    }

    @Test
    public void testIsLinkageSupported() {
    }

}
