package org.clueminer.events;

import java.util.Iterator;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ListenerListTest {

    private ListenerList subject = new ListenerList();

    public ListenerListTest() {
    }

    @Test
    public void testSize() {
        assertEquals(0, subject.size());
    }

    @Test
    public void testIsEmpty() {
        assertEquals(true, subject.isEmpty());
    }

    private ListenerList<String> trivialSet() {
        ListenerList<String> list = new ListenerList<>();
        list.add("first");
        list.add("second");
        list.add("third");
        return list;
    }

    @Test
    public void testEnsureCapacity() {
    }

    @Test
    public void testGetCapacity() {
    }

    //@Test
    public void testAdd_GenericType() {
        ListenerList<String> list = new ListenerList<>();
        String a = "first";
        String b = "second";
        String c = "third";
        list.add(c, new String[]{b});
        list.add(b, new String[]{a});
        list.add(a);
        assertArrayEquals(new String[]{"first", "second", "third"}, list.getListeners());
    }

    @Test
    public void testAdd_GenericType_GenericType() {
    }

    @Test
    public void testBuild() {
    }

    /**
     * We want to get listeners in the very same order as they were inserted.
     */
    @Test
    public void testGetListeners() {
        ListenerList<String> list = trivialSet();
        assertArrayEquals(new String[]{"first", "second", "third"}, list.getListeners());
    }

    @Test
    public void testIterator() {
        ListenerList<String> list = trivialSet();
        Iterator<String> it = list.iterator();
        assertEquals("first", it.next());
        assertEquals("second", it.next());
        assertEquals("third", it.next());
    }

    @Test
    public void testRemove() {
        ListenerList<String> list = trivialSet();
        list.remove(list.get(0));
        assertEquals(2, list.size());
        Iterator<String> it = list.iterator();
        assertEquals("second", it.next());
        assertEquals("third", it.next());
    }

}
