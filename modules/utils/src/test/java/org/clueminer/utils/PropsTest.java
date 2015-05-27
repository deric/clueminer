package org.clueminer.utils;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

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
    public void tearDown() throws Exception {
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
        //intentionally
        assertEquals(false, subject.getBoolean("some_bool"));
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
    public void testPutBooleanPt() {
        String key = "bool-pt-key";
        subject.put(PropType.PERFORMANCE, key, true);
        assertEquals(true, subject.getBoolean(PropType.PERFORMANCE, key));

        //get value, return default if missing
        key = "bool-pt-key2";
        subject.put(PropType.PERFORMANCE, key, true);
        assertEquals(true, subject.getBoolean(PropType.PERFORMANCE, key, false));

        key = "bool-pt-key3";
        assertEquals(true, subject.getBoolean(PropType.PERFORMANCE, key, true));
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
    public void testGetPerf() {
        Props p = new Props();
        p.put(PropType.PERFORMANCE, "keep-matrix", true);
        boolean b = p.getBoolean(PropType.PERFORMANCE, "keep-matrix");
        assertEquals(true, b);

        b = p.getBoolean(PropType.PERFORMANCE, "some-non-existing-key", true);
        assertEquals(true, b);
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

    @Test
    public void testToString() {
        subject.put("some keeeey", "value");
        String str = subject.toString();
        System.out.println("toString: " + str);
    }

    @Test
    public void testPut_String_String() {
    }

    @Test
    public void testPut_3args_1() {
    }

    @Test
    public void testPut_3args_2() {
    }

    @Test
    public void testPutAll_Map() {
    }

    @Test
    public void testPutAll_PropType_Map() {
    }

    @Test
    public void testGet_PropType_String() {
    }

    @Test
    public void testGet_String() {
    }

    @Test
    public void testGet_String_String() {
    }

    @Test
    public void testGet_3args() {
    }

    @Test
    public void testGetBoolean_PropType_String() {
    }

    @Test
    public void testGetBoolean_3args() {
    }

    @Test
    public void testCopy() {
    }

    @Test
    public void testClone() {
        Props p = new Props();
        p.put("foo", "foo");
        Props clone = p.clone();
        clone.put("foo", "bar");
        assertEquals("bar", clone.get("foo"));
        assertEquals("foo", p.get("foo"));
    }

    @Test
    public void testSize() {
        Props p = new Props();
        assertEquals(0, p.size());
        p.putBoolean("aaa", true);
        assertEquals(1, p.size());
    }

    @Test
    public void testIsEmpty() {
        Props p = new Props();
        assertEquals(true, p.isEmpty());
        p.put(PropType.PERFORMANCE, "foo", "foo");
        assertEquals(false, p.isEmpty());
    }

    @Test
    public void testContainsKey() {
    }

    @Test
    public void testContainsValue() {
    }

    @Test
    public void testGet_Object() {
    }

    @Test
    public void testGet_PropType_Object() {
    }

    @Test
    public void testRemove() {
    }

    @Test
    public void testClear() {
        Props p = new Props();
        p.put("foo", "x");
        assertEquals(1, p.size());
        p.clear();
        assertEquals(0, p.size());
    }

    @Test
    public void testKeySet() {
    }

    @Test
    public void testValues() {
    }

    @Test
    public void testEntrySet() {
    }

    @Test
    public void testMerge() {
        Props p = new Props();
        p.put("foo", "x");
        p.put("bar", "y");
        Props other = new Props();
        other.put("foo", "a");
        other.put("baz", "c");
        other.put(PropType.PERFORMANCE, "perf", "ormance");

        p.merge(other);
        assertEquals("a", p.get("foo"));
        assertEquals("y", p.get("bar"));
        assertEquals("c", p.get("baz"));
        assertEquals("ormance", p.get(PropType.PERFORMANCE, "perf"));

    }

}
