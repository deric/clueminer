package org.clueminer.fastcommunity;

import java.util.HashMap;
import java.util.Map;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;

/**
 *
 * @author Hamster
 */
public class CommunityNetwork {

	int maxId;
	double totalEdgesCount;
	HashMap<IntPair, Integer> matrix;
	HashMap<Integer, Community> communities;
	DeltaQMatrix deltaQ;

	public CommunityNetwork(DeltaQMatrix deltaQ, int edgesCount) {
		matrix = new HashMap<>();
		communities = new HashMap<>();
		maxId = -1;
		totalEdgesCount = edgesCount;
		this.deltaQ = deltaQ;
	}

	void add(Community community) {
		int id = community.getId();
		if(id > maxId)
			maxId = id;
		communities.put(community.getId(), community);
	}

	void initConnections(Graph graph) {
		for(Node node : graph.getNodes()) {
			for(Node neighbor : graph.getNeighbors(node)) {
				int i = (int) node.getId();
				int j = (int) neighbor.getId();
				matrix.put(IntPair.ordered(i, j), 1);
			}
		}
	}

	void merge(Integer target, Integer source) {
		if(target > source) {
			int tmp = target;
			target = source;
			source = tmp;
		}
		Community a = communities.get(target);
		Community b = communities.get(source);
		Integer edgesBetween = matrix.get(new IntPair(target, source));
		if(edgesBetween == null)
			edgesBetween = 0;

		totalEdgesCount -= edgesBetween;
		a.addInsideEdges(b.getEdgesInside() + edgesBetween);
		Integer aOut = a.getEdgesOutside();
		Integer bOut = b.getEdgesOutside();
		a.setEdgesOutside(aOut + bOut - 2 * edgesBetween);

//		System.out.println("Removing " + target + " - " + source);
		deltaQ.remove(target, source);
		matrix.remove(new IntPair(target, source));
		communities.remove(source);

		for(int i = 0; i <= maxId; i++) {
			Integer edgesSourceToNeighbor, edgesTargetToNeighbor;
			edgesSourceToNeighbor = matrix.get(IntPair.ordered(i, source));

//			System.out.println("Removing " + i + " - " + source);
			deltaQ.remove(i, source);

			if(edgesSourceToNeighbor != null && edgesSourceToNeighbor > 0) {

				edgesTargetToNeighbor = matrix.get(IntPair.ordered(i, target));
				if(edgesTargetToNeighbor == null)
					edgesTargetToNeighbor = 0;

				matrix.put(IntPair.ordered(i, target), edgesTargetToNeighbor + edgesSourceToNeighbor);

				matrix.remove(IntPair.ordered(i, source));
			}
		}
		for(int i = 0; i <= maxId; i++) {
			Integer edgesTargetToNeighbor = matrix.get(IntPair.ordered(i, target));
//			System.out.println("Removing " + target + " - " + i);
			deltaQ.remove(target, i);
			if(edgesTargetToNeighbor != null && edgesTargetToNeighbor > 0) {
//				dQ = 2 * (e_ij - a_i * a_j)
				double ai = a.getEdgesOutside() / totalEdgesCount;
				double aj = communities.get(i).getEdgesOutside() / totalEdgesCount;
				double eij = edgesTargetToNeighbor / totalEdgesCount;
				Double value = 2 * (eij - ai * aj);
//				System.out.println("Adding " + target + " - " + i);
				deltaQ.add(target, i, value);
			}
		}
	}

	public void print() {
		System.out.println("Communities:");
		for(Map.Entry<Integer, Community> entrySet : communities.entrySet()) {
			Community community = entrySet.getValue();
			System.out.println(community);
		}
		System.out.println("---------------------");
		System.out.println("Connections:");
		for(Map.Entry<IntPair, Integer> entrySet : matrix.entrySet()) {
			Integer connections = entrySet.getValue();
			Integer i = entrySet.getKey().getFirst();
			Integer j = entrySet.getKey().getSecond();
			System.out.println("\t" + i + " -> " + j + " [" + connections + "]");
		}
		System.out.println("---------------------");
	}

}
