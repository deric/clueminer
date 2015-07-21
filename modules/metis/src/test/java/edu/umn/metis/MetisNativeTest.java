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
package edu.umn.metis;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class MetisNativeTest {

    private static MetisNative subject;

    public MetisNativeTest() {
        subject = new MetisNative();
    }

    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    //@Test
    public void testGraph() {
        System.out.println("gr: " + subject.PartGraphKway());
        System.out.println("gr: " + subject.PartGraphRecursive());
    }

}
