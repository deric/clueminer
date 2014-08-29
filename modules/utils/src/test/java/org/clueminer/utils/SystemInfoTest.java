package org.clueminer.utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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

    @After
    public void tearDown() {
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
