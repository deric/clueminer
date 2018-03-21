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
package org.clueminer.clustering.confusion;

import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.utils.Dump;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ConfusionTableTest {

    private ConfusionTable subject;

    public ConfusionTableTest() {
        subject = new ConfusionTable();
    }

    @Test
    public void testSetClusterings_Clustering_Dataset() {
        int[][] conf = subject.countMutual(FakeClustering.irisWrong2());
        Dump.matrix(conf, "iris wrong", 0);
        int sum;
        for (int[] conf1 : conf) {
            sum = 0;
            for (int j = 0; j < conf1.length; j++) {
                sum += conf1[j];
            }
            //sum in rows should be 50
            assertEquals(50, sum);
        }
    }

}
