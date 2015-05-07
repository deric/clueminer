package org.clueminer.fastcommunity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import org.clueminer.clustering.aggl.Element;
import org.clueminer.clustering.algorithm.HClustResult;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.adjacencyList.AdjListFactory;
import org.clueminer.graph.adjacencyList.AdjListGraph;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Node;
import org.clueminer.hclust.DLeaf;
import org.clueminer.hclust.DTreeNode;
import org.clueminer.hclust.DynamicTreeData;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Hamster
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class FastCommunity extends AbstractClusteringAlgorithm implements AgglomerativeClustering {

	private AdjListGraph graph;
	private PriorityQueue<ReverseElement> pq;
//	private double[] a;
//	private double[] Q;
	private CommunityNetwork network;
	DeltaQMatrix dQ;

	@Override
	public String getName() {
		return "Fast Community";
	}

	@Override
	public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset, Props props) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public HierarchicalResult hierarchy(Dataset<? extends Instance> dataset, Props pref) {
		graph = new AdjListGraph();
		List<Node> nodes = AdjListFactory.getInstance().createNodesFromInput(dataset);
		graph.addAllNodes(nodes);
		this.createEdges(dataset, nodes);
//		a = new double[dataset.size()];

		HierarchicalResult result = new HClustResult(dataset, pref);
//		pref.put(AgglParams.ALG, getName());
		int n = dataset.size();
		int items = triangleSize(n);
		pq = new PriorityQueue<>(items);
		dQ = new DeltaQMatrix(pq);

		DendroTreeData treeData = computeLinkage(dataset, n);

		treeData.createMapping(n, treeData.getRoot());
		result.setTreeData(treeData);
		return result;
	}

	@Override
	public boolean isLinkageSupported(String linkage) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private Map<Integer, Community> initialAssignment(int n, Dataset<? extends Instance> dataset,
			DendroNode[] nodes) {
		Map<Integer, Community> clusterAssignment = new HashMap<>(n);
		network = new CommunityNetwork(dQ, graph.getEdgeCount());
		for(int i = 0; i < n; i++) {
			Community community = new Community(network, graph, i, graph.getNode(i));
			clusterAssignment.put(i, community);
			nodes[i] = new DLeaf(i, dataset.get(i));
			network.add(community);
		}
		network.initConnections(graph);
		System.out.println("Initialized:");
		network.print();
//		double eij = ;
//		for(int i = 0; i < graph.getNodeCount(); i++) {
//			int degree = graph.getDegree(graph.getNode(i));
//			a[i] = eij * degree;
//			Q[0] -= a[i] * a[i];
//		}
		return clusterAssignment;
	}

	private DendroTreeData computeLinkage(Dataset<? extends Instance> dataset, int n) {
		DendroNode[] nodes = new DendroNode[(2 * n - 1)];

		Map<Integer, Community> assignments = initialAssignment(n, dataset, nodes);

		dQ.build(graph);

		populatePriorityQueue(dQ);
		System.out.println(pq.toString());

		ReverseElement current;
//		HashSet<Integer> blacklist = new HashSet<>();
		DendroNode node = null;
		Community left, right;
		int nodeId = n;

		while(!pq.isEmpty() && assignments.size() > 1) {
			current = pq.poll();
			int i = current.getRow();
			int j = current.getColumn();
			if(i > j) {
				int tmp = i;
				i = j;
				j = tmp;
			}
			node = getOrCreate(nodeId++, nodes);
			node.setLeft(nodes[i]);
			node.setRight(nodes[j]);
			node.setHeight(current.getValue());

			left = assignments.get(i);
			if(left == null)
				System.out.println("Community " + i + " not found!");
			right = assignments.remove(j);

			left.addAll(right);
			System.out.println("Merging " + i + " + " + j);
			network.merge(i, j);
			network.print();
			System.out.println(pq.toString());
			System.out.println("=============================");
		}
		network.print();

		//last node is the root
		DendroTreeData treeData = new DynamicTreeData(node);
		return treeData;
	}

//	private void updateDistances(int mergedId, Set<Integer> mergedCluster,
//			Matrix similarityMatrix, Map<Integer, Set<Integer>> assignments,
//			ClusterLinkage linkage) {
//		Element current;
//		double distance;
//		for(Map.Entry<Integer, Set<Integer>> cluster : assignments.entrySet()) {
//			distance = linkage.similarity(similarityMatrix, cluster.getValue(), mergedCluster);
//			current = new Element(distance, mergedId, cluster.getKey());
//			pq.add(current);
//		}
//		//System.out.println("adding " + mergedId + " -> " + mergedCluster.toString());
//		//finaly add merged cluster
//		assignments.put(mergedId, mergedCluster);
//	}

	private DendroNode getOrCreate(int id, DendroNode[] nodes) {
		if(nodes[id] == null) {
			DendroNode node = new DTreeNode(id);
			nodes[id] = node;
		}
		return nodes[id];
	}

	private int triangleSize(int n) {
		return ((n - 1) * n) >>> 1;
	}

	private void createEdges(Dataset<? extends Instance> dataset, List<Node> nodes) {
		for(int instanceIdx = 0; instanceIdx < dataset.size(); instanceIdx++) {
			for(int attributeIdx = 0; attributeIdx < dataset.attributeCount(); attributeIdx++) {
				if(dataset.get(instanceIdx, attributeIdx) > 0.5) {
					Node source = nodes.get(instanceIdx);
					Node target = nodes.get(attributeIdx);
					Edge edge = graph.getFactory().newEdge(source, target);
					graph.addEdge(edge);
				}
			}
		}
	}

	private void populatePriorityQueue(DeltaQMatrix dQ) {
		for(int i = 0; i < graph.getNodeCount(); i++) {
			for(int j = 0; j < graph.getNodeCount(); j++) {
				ReverseElement element = dQ.get(i, j);
				if(element != null && !pq.contains(element))
					pq.add(element);
			}
		}
	}
}
