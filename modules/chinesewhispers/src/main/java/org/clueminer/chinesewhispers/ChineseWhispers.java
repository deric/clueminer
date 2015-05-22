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
import org.clueminer.colors.ColorBrewer;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.adjacencyList.AdjListFactory;
import org.clueminer.graph.adjacencyList.AdjListGraph;
import org.clueminer.graph.adjacencyList.AdjListNode;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Node;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Hamster
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class ChineseWhispers extends AbstractClusteringAlgorithm {

    AdjListGraph graph = new AdjListGraph();

    public static final String MAX_ITERATIONS = "max_iterations";

    @Param(name = ChineseWhispers.MAX_ITERATIONS, description = "Maximum number of iterations")
    private int maxIterations;

    @Override
    public String getName() {
        return "Chinese Whispers";
    }

    @Override
    public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset, Props props) {
        props.put("algorithm", getName());
        maxIterations = props.getInt(MAX_ITERATIONS, 100);
        List<Node> nodes = AdjListFactory.getInstance().createNodesFromInput(dataset);
        graph.addAllNodes(nodes);
        this.createEdges(dataset, nodes);
        for (Node nodeIt : nodes) {
            AdjListNode node = (AdjListNode) nodeIt;
            Long classValue = node.getId();
            node.getInstance().setClassValue(classValue);
        }

        boolean changes = true;
        Random random = new Random();
        int i = 0;
        while (changes && i < maxIterations) {
            changes = false;
            for (Node nodeIt : nodes) {
                AdjListNode node = (AdjListNode) nodeIt;
                HashMap<Long, Integer> classes = new HashMap<>(nodes.size());
                Long maxClass = node.getId();
                Integer maxCount = 1;
                for (Node neighborIt : graph.getNeighbors(node)) {
                    AdjListNode neighbor = (AdjListNode) neighborIt;
                    Long classValue = (Long) neighbor.getInstance().classValue();
                    Integer count = classes.get(classValue);
                    count = count == null ? 1 : count + 1;
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
        HashMap<Long, List<Node>> clusters = new HashMap<>();
        for (Node node : graph.getNodes()) {
            Long classValue = (Long) node.getInstance().classValue();
            List<Node> thisCluster = clusters.get(classValue);
            if (thisCluster == null) {
                thisCluster = new ArrayList<>();
                clusters.put(classValue, thisCluster);
            }
            thisCluster.add(node);
        }
        Clustering result = Clusterings.newList();
        ColorGenerator generator = new ColorBrewer();
        for (Map.Entry<Long, List<Node>> entrySet : clusters.entrySet()) {
            //Long clusterId = entrySet.getKey();
            List<Node> clusterNodes = entrySet.getValue();
            Cluster cluster = result.createCluster();
            cluster.setColor(generator.next());
            //System.out.println("Cluster " + clusterId);
            for (Node clusterNode : clusterNodes) {
                //System.out.println("\t" + clusterNode.getId());
                cluster.add(clusterNode.getInstance());
            }
            //System.out.println("");
        }
        return result;
    }

    private void createEdges(Dataset<? extends Instance> dataset, List<Node> nodes) {
        for (int instanceIdx = 0; instanceIdx < dataset.size(); instanceIdx++) {
            for (int attributeIdx = 0; attributeIdx < dataset.attributeCount(); attributeIdx++) {
                if (dataset.get(instanceIdx, attributeIdx) > 0.5) {
                    AdjListNode source = (AdjListNode) nodes.get(instanceIdx);
                    AdjListNode target = (AdjListNode) nodes.get(attributeIdx);
                    Edge edge = graph.getFactory().newEdge(source, target);
                    graph.addEdge(edge);
                }
            }
        }
    }
}
