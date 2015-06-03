package org.clueminer.chinesewhispers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.api.factory.Clusterings;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.graph.adjacencyList.AdjListFactory;
import org.clueminer.graph.adjacencyList.AdjListGraph;
import org.clueminer.graph.adjacencyList.AdjListNode;
import org.clueminer.graph.api.GraphConvertor;
import org.clueminer.graph.api.GraphConvertorFactory;
import org.clueminer.graph.api.Node;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Hamster
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class ChineseWhispers extends AbstractClusteringAlgorithm {

    private AdjListGraph graph;

    public static final String MAX_ITERATIONS = "max_iterations";

    @Param(name = ChineseWhispers.MAX_ITERATIONS, description = "Maximum number of iterations")
    private int maxIterations;

    public static final String EDGE_THRESHOLD = "edge_threshold";
    @Param(name = ChineseWhispers.EDGE_THRESHOLD, description = "Minimum distance for edge initialization")
    private double edgeThreshold;

    public static final String GRAPH_CONV = "graph_conv";
    @Param(name = ChineseWhispers.GRAPH_CONV,
           factory = "org.clueminer.graph.api.GraphConvertorFactory",
           type = org.clueminer.clustering.params.ParamType.STRING)
    private GraphConvertor graphCon;

    @Override
    public String getName() {
        return "Chinese Whispers";
    }

    @Override
    public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset, Props props) {
        graph = new AdjListGraph();
        String dist = props.get("distance", "Euclidean");
        this.distanceFunction = DistanceFactory.getInstance().getProvider(dist);
        props.put("algorithm", getName());
        maxIterations = props.getInt(MAX_ITERATIONS, 100);

        Long[] mapping = AdjListFactory.getInstance().createNodesFromInput(dataset, graph);
        String initializer = props.get(GRAPH_CONV, "distance threshold");
        graphCon = GraphConvertorFactory.getInstance().getProvider(initializer);
        graphCon.setDistanceMeasure(distanceFunction);
        graphCon.createEdges(graph, dataset, mapping, props);

        for (Node nodeIt : graph.getNodes()) {
            AdjListNode node = (AdjListNode) nodeIt;
            Long classValue = node.getId();
            node.getInstance().setClassValue(classValue.toString());
        }

        boolean changes = true;
        Random random = new Random();
        int i = 0;
        while (changes && i < maxIterations) {
            changes = false;
            for (Node nodeIt : graph.getNodes()) {
                AdjListNode node = (AdjListNode) nodeIt;
                HashMap<String, Integer> classes = new HashMap<>(graph.getNodeCount());
                String maxClass = (String) node.getInstance().classValue();
                Integer maxCount = 1;
                for (Node neighborIt : graph.getNeighbors(node)) {
                    AdjListNode neighbor = (AdjListNode) neighborIt;
                    String classValue = (String) neighbor.getInstance().classValue();
                    Integer count = classes.get(classValue);
                    count = (count == null) ? 1 : count++;
                    classes.put(classValue, count);
                    if (count > maxCount
                            || (count.equals(maxCount) && random.nextBoolean())) {
                        maxCount = count;
                        maxClass = classValue;
                    }
                }
                if (!maxClass.equals(node.getInstance().classValue())) {
                    changes = true;
                    node.getInstance().setClassValue(maxClass);
                }
            }
            i++;
        }

        //graph.print();
        HashMap<String, List<Node>> clusters = new HashMap<>();
        for (Node node : graph.getNodes()) {
            String classValue = (String) node.getInstance().classValue();
            List<Node> thisCluster = clusters.get(classValue);
            if (thisCluster == null) {
                thisCluster = new ArrayList<>();
                clusters.put(classValue, thisCluster);
            }
            thisCluster.add(node);
        }
        Clustering result = Clusterings.newList();
        for (Map.Entry<String, List<Node>> entrySet : clusters.entrySet()) {
            //Long clusterId = entrySet.getKey();
            List<Node> clusterNodes = entrySet.getValue();
            Cluster cluster = result.createCluster();
            if (colorGenerator != null) {
                cluster.setColor(colorGenerator.next());
            }
            //System.out.println("Cluster " + clusterId);
            for (Node clusterNode : clusterNodes) {
                //System.out.println("\t" + clusterNode.getId());
                cluster.add(clusterNode.getInstance());
            }
            //System.out.println("");
        }
        return result;
    }
}
