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
package org.clueminer.colors;

import java.awt.Color;
import java.util.HashSet;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class RBrewTest {

    private RBrew subject;

    public RBrewTest() {
    }

    @Before
    public void setUp() {
        subject = new RBrew();
    }

    @Test
    public void testNext_Color() {
        HashSet<Color> colors = new HashSet<>();
        Color c;
        for (int i = 0; i < 5; i++) {
            c = subject.next();
            System.out.println(i + ": " + c.toString());
            assertFalse("hash should not include duplicate colors", colors.contains(c));
            colors.add(c);
        }
    }

    @Test
    public void testReset() {
    }

}
