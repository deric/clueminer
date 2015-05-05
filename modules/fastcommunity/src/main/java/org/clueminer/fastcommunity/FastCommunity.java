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
import org.clueminer.clustering.api.AgglParams;
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
import org.clueminer.graph.adjacencyList.AdjListGraph;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixFactory;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixNode;
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
	private PriorityQueue<Element> pq;
	private double[] a;
	private double[] Q;
	private CommunityNetwork network;

	public static double INITIAL_EIJ = 0.5;

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
		List<Node> nodes = AdjMatrixFactory.getInstance().createNodesFromInput(dataset);
		graph.addAllNodes(nodes);
		this.createEdges(dataset, nodes);
		a = new double[dataset.size()];

		HierarchicalResult result = new HClustResult(dataset, pref);
		pref.put(AgglParams.ALG, getName());
		int n = dataset.size();
		int items = triangleSize(n);
		pq = new PriorityQueue<>(items);

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
		network = new CommunityNetwork();
		for(int i = 0; i < n; i++) {
			Community community = new Community(network, graph, i, graph.getNode(i));
			clusterAssignment.put(i, community);
			nodes[i] = new DLeaf(i, dataset.get(i));
			network.add(community);
		}
		network.initConnections(graph);
		double eij = INITIAL_EIJ;
		for(int i = 0; i < graph.getNodeCount(); i++) {
			int degree = graph.getDegree(graph.getNode(i));
			a[i] = eij * degree;
			Q[0] -= a[i] * a[i];
		}
		return clusterAssignment;
	}

	private DeltaQMatrix buildDeltaQ() {
		DeltaQMatrix dQ = new DeltaQMatrix();
		dQ.build(graph, a);
		return dQ;
	}

	private DendroTreeData computeLinkage(Dataset<? extends Instance> dataset, int n) {
		DendroNode[] nodes = new DendroNode[(2 * n - 1)];

		Map<Integer, Community> assignments = initialAssignment(n, dataset, nodes);

		DeltaQMatrix dQ = buildDeltaQ();

		populatePriorityQueue(dQ);

		Element current;
		HashSet<Integer> blacklist = new HashSet<>();
		DendroNode node = null;
		Community left, right;
		int nodeId = n;

		while(!pq.isEmpty() && assignments.size() > 1) {
			current = pq.poll();
			int i = current.getRow();
			int j = current.getColumn();
			//System.out.println(curr.toString() + " remain: " + pq.size() + ", height: " + String.format("%.3f", curr.getValue()));
			if(!blacklist.contains(i) && !blacklist.contains(j)) {
				node = getOrCreate(nodeId++, nodes);
				node.setLeft(nodes[i]);
				node.setRight(nodes[j]);
				node.setHeight(current.getValue());

				//System.out.println("node " + node.getId() + " left: " + node.getLeft() + " right: " + node.getRight());
				blacklist.add(i);
				blacklist.add(j);

				left = assignments.remove(i);
				right = assignments.remove(j);

				left.addAll(right);
				network.merge(i, j);
//				updateDistances(node.getId(), left, similarityMatrix, assignments, pq, params.getLinkage());
			}
		}

		//last node is the root
		DendroTreeData treeData = new DynamicTreeData(node);
		return treeData;
	}

	private void updateDistances(int mergedId, Set<Integer> mergedCluster,
			Matrix similarityMatrix, Map<Integer, Set<Integer>> assignments,
			ClusterLinkage linkage) {
		Element current;
		double distance;
		for(Map.Entry<Integer, Set<Integer>> cluster : assignments.entrySet()) {
			distance = linkage.similarity(similarityMatrix, cluster.getValue(), mergedCluster);
			current = new Element(distance, mergedId, cluster.getKey());
			pq.add(current);
		}
		//System.out.println("adding " + mergedId + " -> " + mergedCluster.toString());
		//finaly add merged cluster
		assignments.put(mergedId, mergedCluster);
	}

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
					AdjMatrixNode source = (AdjMatrixNode) nodes.get(instanceIdx);
					AdjMatrixNode target = (AdjMatrixNode) nodes.get(attributeIdx);
					Edge edge = graph.getFactory().newEdge(source, target);
					graph.addEdge(edge);
				}
			}
		}
	}

	private void populatePriorityQueue(DeltaQMatrix dQ) {
		for(int i = 0; i < graph.getNodeCount(); i++) {
			for(int j = 0; j < graph.getNodeCount(); i++) {
				double value = dQ.get(i, j);
				Element element = new Element(value, i, j);
				if(!pq.contains(element))
					pq.add(element);
			}
		}
	}
}
