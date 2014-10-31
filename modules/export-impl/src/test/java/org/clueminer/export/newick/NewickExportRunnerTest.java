package org.clueminer.export.newick;

import org.clueminer.clustering.aggl.HACLW;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class NewickExportRunnerTest {

    private final NewickExportRunner subject;

    public NewickExportRunnerTest() {
        subject = new NewickExportRunner();
    }

    @Test
    public void testDoExport() {
        Dataset<? extends Instance> dataset = FakeDatasets.simpleData();
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, "Single Linkage");
        pref.putBoolean(AgglParams.CLUSTER_ROWS, true);
        HACLW alg = new HACLW();
        HierarchicalResult result = alg.hierarchy(dataset, pref);
        result.getTreeData().print();
        subject.setIncludeNodeNames(true);
        String res = subject.doExport(result);
//        assertEquals("(((C:0.0,D:0.0)#4:1.0,B:0.0)#5:1.414213562373095,A:0.0)#6:2.23606797749979;", res);
        assertEquals("(((2:0.0,3:0.0)#4:1.0,1:0.0)#5:1.414213562373095,0:0.0)#6:2.23606797749979;", res);
        System.out.println(res);

        //without inner node names
        subject.setIncludeNodeNames(false);
        res = subject.doExport(result);
        //      assertEquals("(((C:0.0,D:0.0):1.0,B:0.0):1.414213562373095,A:0.0):2.23606797749979;", res);
        assertEquals("(((2:0.0,3:0.0):1.0,1:0.0):1.414213562373095,0:0.0):2.23606797749979;", res);
        System.out.println(res);

    }

}
