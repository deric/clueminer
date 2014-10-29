package org.clueminer.export.newick;

import org.clueminer.clustering.aggl.HACLW;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.utils.Props;
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
        String res = subject.doExport(result);
        System.out.println(res);
    }

}
