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

    private final ListenerList subject = new ListenerList();

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

    /**
     * (third) -> (second) -> first
     */
    @Test
    public void testAdd_GenericType() {
        ListenerList<String> list = new ListenerList<>();
        //create nodes in reverse order
        list.add("third", new String[]{"second"});
        list.add("second", new String[]{"first"});
        list.add("first");
        //expect sorted nodes
        assertArrayEquals(new String[]{"first", "second", "third"}, list.getListeners());
    }

    /**
     * (third) -> (second) -> first
     */
    @Test
    public void testAdd_GenericType_order() {
        ListenerList<String> list = new ListenerList<>();
        //should work in any order
        list.add("second", new String[]{"first"});
        list.add("first");
        list.add("third", new String[]{"second"});
        //expect sorted nodes
        assertArrayEquals(new String[]{"first", "second", "third"}, list.getListeners());
    }

    @Test
    public void testSimpleConstraints() {
        ListenerList<String> list = new ListenerList<>();
        list.add("A", new String[]{"B"});
        list.add("C", new String[]{"B"});
        list.add("B");
        String[] res = list.toArray(new String[list.size()]);
        assertEquals("B", res[0]);
        //A and C might be in any order
        assertEquals(3, res.length);
    }

    @Test
    public void testMissingRequiredInstance() {
        ListenerList<String> list = new ListenerList<>();
        //B is not added to list of listeners, but will be included automatically
        //because is in requirements
        list.add("A", new String[]{"B"});
        list.add("C", new String[]{"B"});
        String[] res = list.toArray(new String[list.size()]);
        assertEquals("B", res[0]);
        //A and C might be in any order
        assertEquals(3, res.length);
    }

    @Test
    public void testSyntacticSugar() {
        ListenerList<String> list = new ListenerList<>();
        list.add("A", "B");
        String[] res = list.toArray(new String[list.size()]);
        assertEquals(2, res.length);
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
