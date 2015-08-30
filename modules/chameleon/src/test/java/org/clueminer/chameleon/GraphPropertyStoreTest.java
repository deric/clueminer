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
package org.clueminer.chameleon;

import org.junit.Test;

/**
 *
 * @author deric
 */
public class GraphPropertyStoreTest {

    private static GraphPropertyStore subject;

    public GraphPropertyStoreTest() {

    }

    @Test
    public void testStoreIsAddresable() {
        //number of clusters
        int n = 4;
        subject = new GraphPropertyStore(n);

        int m = 7;
        int k = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < i; j++) {
                subject.updateWeight(i, j, k++);
            }
        }

        subject.dump();

    }

}
