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
package org.clueminer.jri;

import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class JRITest {

    private static JRI engine;
    private static final double DELTA = 1e-9;

    public JRITest() {
    }

    @BeforeClass
    public static void setUp() {
        engine = new JRI();
    }

    @Test
    public void testLoadLibrary() throws Exception {
        engine.loadLibrary("clv", this.getClass().getName());
    }

    @Test
    public void testAssign_String_doubleArrArr() throws Exception {
    }

    @Test
    public void testAssign_String_intArrArr() throws Exception {
    }

    @Test
    public void testAssign_String_intArr() throws Exception {
        engine.assign("vec", new int[]{1, 2, 3, 4, 5});
        assertEquals(3.0, engine.eval("mean(vec)").asDouble(), DELTA);
    }

    @Test
    public void testAssign_String_doubleArr() throws Exception {
        engine.assign("vec", new double[]{1.0, 2.0, 3.0, 4.0, 5.0});
        assertEquals(3.0, engine.eval("mean(vec)").asDouble(), DELTA);
    }

    @Test
    public void testAssign_String_StringArr() throws Exception {
    }

    @Test
    public void testEval() throws Exception {
    }

    @Test
    public void testPrintLastError() throws Exception {
    }

    @Test
    public void testInterrupt() {
    }

    @Test
    public void testGetLastError() throws Exception {
    }

    @Test
    public void testShutdown() {
    }

}
