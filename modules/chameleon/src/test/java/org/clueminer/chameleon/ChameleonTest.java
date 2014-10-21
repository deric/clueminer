package org.clueminer.chameleon;

import static org.junit.Assert.assertEquals;
import org.junit.Test;


/**
 *
 * @author Tomas Bruna
 */
public class ChameleonTest {
    
    @Test
    public void testGetName() {
        Chameleon ch = new Chameleon();
        assertEquals("Chameleon", ch.getName());
    }
    
}
