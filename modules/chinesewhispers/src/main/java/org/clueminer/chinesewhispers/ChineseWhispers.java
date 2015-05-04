package org.clueminer.chinesewhispers;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
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

	@Override
	public String getName() {
		return "Chinese Whispers";
	}

	@Override
	public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset, Props props) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset) {
		List<Node> nodes = AdjListFactory.getInstance().createNodesFromInput(dataset);
		graph.addAllNodes(nodes);
		this.createEdges(dataset, nodes);
		for(Node nodeIt : nodes) {
			AdjListNode node = (AdjListNode) nodeIt;
			Long classValue = node.getId();
			node.getInstance().setClassValue(classValue);
		}

		boolean changes = true;
		Random random = new Random();
		while(changes) {
			changes = false;
			for(Node nodeIt : nodes) {
				AdjListNode node = (AdjListNode) nodeIt;
				HashMap<Long, Integer> classes = new HashMap<>(nodes.size());
				Long maxClass = node.getId();
				Integer maxCount = 1;
				for(Node neighborIt : graph.getNeighbors(node)) {
					AdjListNode neighbor = (AdjListNode) neighborIt;
					Long classValue = (Long) neighbor.getInstance().classValue();
					Integer count = classes.get(classValue);
					count = count == null ? 1 : count + 1;
					classes.put(classValue, count);
					if(count > maxCount ||
					  (count.equals(maxCount) && random.nextBoolean())) {
						maxCount = count;
						maxClass = classValue;
					}
				}
				if(! maxClass.equals(node.getInstance().classValue())) {
					changes = true;
					node.getInstance().setClassValue(maxClass);
				}
			}
		}

		graph.print();
		return null;
	}

	private void createEdges(Dataset<? extends Instance> dataset, List<Node> nodes) {
		for(int instanceIdx = 0; instanceIdx < dataset.size(); instanceIdx++) {
			for(int attributeIdx = 0; attributeIdx < dataset.attributeCount(); attributeIdx++) {
				if(dataset.get(instanceIdx, attributeIdx) > 0.5) {
					AdjListNode source = (AdjListNode) nodes.get(instanceIdx);
					AdjListNode target = (AdjListNode) nodes.get(attributeIdx);
					Edge edge = graph.getFactory().newEdge(source, target);
					graph.addEdge(edge);
				}
			}
		}
	}
}
