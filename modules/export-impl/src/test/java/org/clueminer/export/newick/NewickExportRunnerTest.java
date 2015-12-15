package org.clueminer.export.newick;

import org.clueminer.clustering.aggl.HCLW;
import org.clueminer.clustering.aggl.linkage.CompleteLinkage;
import org.clueminer.clustering.aggl.linkage.SingleLinkage;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.ClusteringType;
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
        pref.put(AgglParams.LINKAGE, SingleLinkage.name);
        pref.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        HCLW alg = new HCLW();
        HierarchicalResult result = alg.hierarchy(dataset, pref);
        result.getTreeData().print();
        subject.setIncludeNodeNames(false);
        String res = subject.doExport(result);
        System.out.println(res);
//        assertEquals("(((2:0.143,5:0.143):0.199,1:0.342):0.044,((3:0.102,6:0.102):0.118,4:0.220):0.166):0.0;", res);
    }

    @Test
    public void testExportKumar() {
        Dataset<? extends Instance> dataset = FakeDatasets.kumarData();
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, CompleteLinkage.name);
        pref.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        subject.setLabel("name");
        HCLW alg = new HCLW();
        HierarchicalResult result = alg.hierarchy(dataset, pref);
        result.getTreeData().print();
        subject.setIncludeNodeNames(false);
        String res = subject.doExport(result);
        //assertEquals("(((C:0.0,D:0.0):1.0,B:0.0):2.23606797749979,A:0.0):3.16227766016838;", res);
        System.out.println(res);

    }

    @Test
    public void testDoExportWithNames() {
        Dataset<? extends Instance> dataset = FakeDatasets.schoolData();
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, CompleteLinkage.name);
        pref.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        subject.setLabel("name");
        HCLW alg = new HCLW();
        HierarchicalResult result = alg.hierarchy(dataset, pref);
        result.getTreeData().print();
        subject.setIncludeNodeNames(false);
        String res = subject.doExport(result);
        //assertEquals("(((C:0.0,D:0.0):1.0,B:0.0):2.23606797749979,A:0.0):3.16227766016838;", res);
        System.out.println(res);

    }

}
