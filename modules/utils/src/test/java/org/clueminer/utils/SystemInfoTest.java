/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.utils;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author deric
 */
public class SystemInfoTest {

    public SystemInfoTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        System.out.println("## System Information");
        System.out.println("OS: " + SystemInfo.OS_String);
        System.out.println("cores: " + SystemInfo.LogicalCores);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @org.junit.Test
    public void testIsWindows() {
        System.out.println("windows: " + SystemInfo.isWindows());
    }

    @org.junit.Test
    public void testIsMac() {
        System.out.println("mac: " + SystemInfo.isMac());
    }

    @org.junit.Test
    public void testIsLinux() {
        System.out.println("linux: " + SystemInfo.isLinux());
    }

}
