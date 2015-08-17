package org.clueminer.graph.knn;


import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.distance.api.KNN;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphConvertor;
import org.clueminer.graph.api.Node;
import org.clueminer.utils.Props;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Hamster
 */
@ServiceProvider(service = GraphConvertor.class)
public class KnnInitializator implements GraphConvertor {

    private Distance dm;
    private static final String name = "k-NN";

    @Override
    public String getName() {
        return name;
    }

    /**
     * Find k-nearest neighbors and add an edge between nodes
     *
     * @param graph
     * @param dataset
     * @param mapping
     * @param params
     */
    @Override
    public void createEdges(Graph graph, Dataset<? extends Instance> dataset, Long[] mapping, Props params) {
        KNN knn = Lookup.getDefault().lookup(KNN.class);
        if (knn == null) {
            throw new RuntimeException("did not find any provider for k-NN");
        }
        int k = params.getInt("k", 5);
        int[] nn;
        long nodeId;
        Node target;
        Edge edge;
        for (Node node : graph.getNodes()) {
            nn = knn.nnIds(node.getInstance().getIndex(), k, dataset, params);
            for (int i = 0; i < nn.length; i++) {
                nodeId = mapping[nn[i]];
                target = graph.getNode(nodeId);
                edge = graph.getFactory().newEdge(node, target);
                graph.addEdge(edge);
            }
        }
    }

    @Override
    public void setDistanceMeasure(Distance dm) {
        this.dm = dm;
    }

}
