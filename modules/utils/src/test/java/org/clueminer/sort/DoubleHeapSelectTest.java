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
package org.clueminer.sort;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DoubleHeapSelectTest {

    public DoubleHeapSelectTest() {
    }

    @Test
    public void testSelectDouble() {
        DoubleHeapSelect instance = new DoubleHeapSelect(10);
        for (int i = 0; i < 1000; i++) {
            instance.add(0.1 * i);
            if (i > 10) {
                for (int j = 0; j < 10; j++) {
                    assertEquals(instance.get(j), 0.1 * j, 1E-10);
                }
            }
        }

        instance = new DoubleHeapSelect(10);
        for (int i = 0; i < 1000; i++) {
            instance.add(0.1 * (1000 - i));
            if (i >= 9) {
                for (int j = 0; j < 10; j++) {
                    assertEquals(instance.get(j), 0.1 * (1000 - i + j), 1E-10);
                }
            }
        }
    }

    /**
     * Test of get method, of class HeapSelect.
     */
    @Test
    public void testDoubleSelectBig() {
        System.out.println("DoubleHeapSelect Big");
        DoubleHeapSelect instance = new DoubleHeapSelect(10);
        for (int i = 0; i < 100000000; i++) {
            instance.add(Math.random());
        }

        for (int j = 0; j < 10; j++) {
            System.out.println(instance.get(j));
        }
    }

}
