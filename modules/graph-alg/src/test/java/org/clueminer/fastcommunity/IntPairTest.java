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
package org.clueminer.fastcommunity;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Hamster
 */
public class IntPairTest {

    /**
     * Test of getFirst method, of class IntPair.
     */
    @Test
    public void testGetFirst() {
        System.out.println("getFirst");
        IntPair a = new IntPair(1, 2);
        IntPair b = new IntPair(2, 1);
        IntPair c = new IntPair(1, 2);
        assertEquals(true, a.equals(c));
        assertEquals(false, a.equals(b));
    }

}
