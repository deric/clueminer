package org.clueminer.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class PropsTest {

    private Props subject;

    public PropsTest() {
    }

    @Before
    public void setUp() {
        subject = new Props();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetGroupKeys() {
    }

    @Test
    public void testGetString_String() {
        subject.put("foo", "bar");
        assertEquals("bar", subject.get("foo"));
    }

    @Test
    public void testGetString_String_String() {
    }

    @Test
    public void testGetInt_String() {
    }

    @Test
    public void testGetInt_String_int() {
    }

    @Test
    public void testGetBoolean_String() {
    }

    @Test
    public void testGetBoolean_String_boolean() {
        assertEquals(false, subject.getBoolean("some_bool", false));
        assertEquals(true, subject.getBoolean("some_bool", true));
    }

    @Test
    public void testGetLong_String() {
    }

    @Test
    public void testGetLong_String_long() {
    }

    @Test
    public void testGetDouble_String() {
    }

    @Test
    public void testGetDouble_String_double() {
    }

    @Test
    public void testToProperties() {
    }

    @Test
    public void testPutBoolean() {
        String key = "boolean-key";
        subject.putBoolean(key, true);
        assertEquals(true, subject.getBoolean(key));
        //overwrite key
        subject.putBoolean(key, false);
        assertEquals(false, subject.getBoolean(key));

    }

}
