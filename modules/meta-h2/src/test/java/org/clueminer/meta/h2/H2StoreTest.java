package org.clueminer.meta.h2;

import java.sql.SQLException;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class H2StoreTest {

    private H2Store subject;

    public H2StoreTest() {
    }

    @Before
    public void setUp() {
        subject = H2Store.getInstance();

        subject.db("unit-test");
    }

    @After
    public void tearDown() {
        try {
            subject.close();
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Test
    public void testFetchDataset(){
        long id = subject.fetchDataset(FakeDatasets.irisDataset());
        assertEquals(true, id > 0);
    }

    @Test
    public void testAdd() {
    }

    @Test
    public void testFindScore() {
    }

    @Test
    public void testClose() throws Exception {
    }

}
