package org.clueminer.fastcommunity;

import java.util.HashMap;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;

/**
 *
 * @author Hamster
 */
public class CommunityNetwork {

	Integer maxId;
	HashMap<IntPair, Integer> matrix;
	HashMap<Integer, Community> communities;
	DeltaQMatrix deltaQ;

	public CommunityNetwork(DeltaQMatrix deltaQ) {
		matrix = new HashMap<>();
		communities = new HashMap<>();
		maxId = -1;
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

		a.addInsideEdges(b.getEdgesInside() + edgesBetween);
		Integer aOut = a.getEdgesOutside();
		Integer bOut = b.getEdgesOutside();
		a.setEdgesOutside(aOut + bOut - 2 * edgesBetween);

		deltaQ.remove(target, source);
		matrix.remove(new IntPair(target, source));

		for(int i = 0; i <= maxId; i++) {
			Integer edgesSourceToNeighbor, edgesTargetToNeighbor;
			edgesSourceToNeighbor = matrix.get(IntPair.ordered(i, source));

			if(edgesSourceToNeighbor != null && edgesSourceToNeighbor > 0) {
				deltaQ.remove(i, source);

				edgesTargetToNeighbor = matrix.get(IntPair.ordered(i, target));
				if(edgesTargetToNeighbor == null)
					edgesTargetToNeighbor = 0;

				matrix.put(IntPair.ordered(i, target), edgesTargetToNeighbor + edgesSourceToNeighbor);

				matrix.remove(IntPair.ordered(i, source));
			}
		}
		for(int i = 0; i <= maxId; i++) {
			Integer edgesTargetToNeighbor = matrix.get(IntPair.ordered(i, target));
			if(edgesTargetToNeighbor != null && edgesTargetToNeighbor > 0) {
				deltaQ.remove(target, i);
//				dQ = 2 * (e_ij - a_i * a_j)
//				TODO: eij is a fraction!
				double ai = a.getEdgesOutside();
				double aj = communities.get(i).getEdgesOutside();
				double eij = edgesTargetToNeighbor;
				Double value = 2 * (eij - ai * aj);
				deltaQ.add(target, i, value);
			}
		}
	}

}
