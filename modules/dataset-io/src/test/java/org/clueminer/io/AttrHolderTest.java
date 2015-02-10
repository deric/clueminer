package org.clueminer.io;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class AttrHolderTest {

    private static AttrHolder subject;

    public AttrHolderTest() {
    }

    @Test
    public void testGetName() {
    }

    @Test
    public void testSetName() {
    }

    @Test
    public void testGetType() {
    }

    @Test
    public void testGuessType() throws ParserError {
        AttrHolder a = new AttrHolder("erythema", null, null, "0,1,2,3");
        assertEquals("erythema", a.getName());
        assertEquals("0,1,2,3", a.getAllowed());
        //guess type
        assertEquals("INTEGER", a.getType());

        //from range
        a = new AttrHolder("erythema", null, "0,1", null);
        assertEquals("erythema", a.getName());
        assertEquals("0,1", a.getRange());
        //guess type
        assertEquals("INTEGER", a.getType());

    }

    @Test
    public void testSetType() {
    }

    @Test
    public void testGetRange() {
    }

    @Test
    public void testSetRange() {
    }

    @Test
    public void testGetAllowed() {
    }

    @Test
    public void testSetAllowed() {
    }


}
