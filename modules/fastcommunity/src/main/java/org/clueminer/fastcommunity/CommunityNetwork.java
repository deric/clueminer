package org.clueminer.fastcommunity;

import java.util.HashMap;
import java.util.HashSet;
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

	public CommunityNetwork() {
		matrix = new HashMap<>();
		communities = new HashMap<>();
		maxId = -1;
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
				if(i < j)
					matrix.put(new IntPair(i, j), 1);
				else
					matrix.put(new IntPair(j, i), 1);
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

		for(int i = 0; i < maxId; i++) {
			Integer edgesSourceToNeighbor, edgesTargetToNeighbor;
			if(i < source)
				edgesSourceToNeighbor = matrix.get(new IntPair(i, source));
			  else
				edgesSourceToNeighbor = matrix.get(new IntPair(source, i));

			if(edgesSourceToNeighbor != null && edgesSourceToNeighbor > 0) {
				if(i < target)
					edgesTargetToNeighbor = matrix.get(new IntPair(i, target));
				  else
					edgesTargetToNeighbor = matrix.get(new IntPair(target, i));
				if(edgesTargetToNeighbor == null)
					edgesTargetToNeighbor = 0;

				if(i < target)
					matrix.put(new IntPair(i, target), edgesTargetToNeighbor + edgesSourceToNeighbor);
				  else
					matrix.put(new IntPair(target, i), edgesTargetToNeighbor + edgesSourceToNeighbor);

				if(i < source)
					matrix.remove(new IntPair(i, source));
				  else
					matrix.remove(new IntPair(source, i));
			}
		}
	}

}
