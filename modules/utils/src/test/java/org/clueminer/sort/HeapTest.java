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

import java.util.Comparator;
import java.util.Random;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class HeapTest {

    public HeapTest() {
    }

    @Test
    public void testNonFixedSize() {
        //create heap with capacity 10
        Heap<Double> instance = new Heap<>(new Comparator<Double>() {

            @Override
            public int compare(Double o1, Double o2) {
                return o1.compareTo(o2);
            }
        });
        Random rand = new Random();
        //add 20 items
        double d;
        for (int i = 0; i < 20; i++) {
            d = rand.nextDouble();
            System.out.println("adding " + i + " = " + d);
            instance.add(d);
        }
        //and retrieve 20 items
        double prev = -1;
        for (int j = 0; j < 20; j++) {
            d = instance.pop();
            assertTrue(prev + " should be less than " + d, prev < d);
            prev = d;
        }
    }

}
