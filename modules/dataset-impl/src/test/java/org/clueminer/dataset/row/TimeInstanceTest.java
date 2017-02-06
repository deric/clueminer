/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.dataset.row;

import java.util.Random;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class TimeInstanceTest {

    public TimeInstanceTest() {
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testCrop() {
        TimeInstance test = instance(15);
        assertEquals(15, test.size());
        //last index is included aswell
        TimeInstance cropped = test.crop(5, 10);
        //assertEquals(6, cropped.size());
        for (int i = 0; i < cropped.size(); i++) {
            System.out.println("inst " + i + ": " + cropped.value(i));
        }
    }

    private TimeInstance instance(int size) {
        TimeInstance row = new TimeInstance(size);
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            row.put(rand.nextDouble());
        }
        return row;
    }
}
