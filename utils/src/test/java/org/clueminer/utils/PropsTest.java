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
    private static final double delta = 1e-9;

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
        assertEquals("value", subject.get("some-missing key", "value"));
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

    @Test
    public void testGetString() {
    }

    @Test
    public void testGet() {
    }

    @Test
    public void testPutInt() {
        int i = 42;
        String key = "some integer";
        subject.putInt(key, i);
        assertEquals(subject.getInt(key), i);
    }

    @Test
    public void testPutDouble() {
        double d = 3.14159;
        String key = "some double";
        subject.putDouble(key, d);
        assertEquals(subject.getDouble(key), d, delta);
    }

}
