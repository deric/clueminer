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
        if (id > maxId) {
            maxId = id;
        }
        communities.put(community.getId(), community);
    }

    void initConnections(Graph graph) {
        for (Node node : graph.getNodes()) {
            for (Node neighbor : graph.getNeighbors(node)) {
                int i = (int) node.getId();
                int j = (int) neighbor.getId();
                matrix.put(IntPair.ordered(i, j), 1);
            }
        }
    }

    void merge(Integer target, Integer source) {
        if (target > source) {
            int tmp = target;
            target = source;
            source = tmp;
        }
        Community a = communities.get(target);
        Community b = communities.get(source);
        Integer edgesBetween = matrix.get(new IntPair(target, source));
        if (edgesBetween == null) {
            edgesBetween = 0;
        }
        totalEdgesCount -= edgesBetween;

		Community c = new Community(++maxId, a, b, edgesBetween);
		communities.put(c.getId(), c);

        //System.out.println("Removing " + target + " - " + source);
        deltaQ.remove(target, source);
        matrix.remove(new IntPair(target, source));
        communities.remove(target);
        communities.remove(source);

        for (int i = 0; i <= maxId; i++) {
			Integer edgesSourceToNeighbor = matrix.get(IntPair.ordered(i, source));
			if (edgesSourceToNeighbor == null)
				edgesSourceToNeighbor = 0;

			Integer edgesTargetToNeighbor = matrix.get(IntPair.ordered(i, target));
			if (edgesTargetToNeighbor == null) {
				edgesTargetToNeighbor = 0;
			}

            deltaQ.remove(i, source);
            deltaQ.remove(i, target);

			matrix.remove(IntPair.ordered(i, source));
			matrix.remove(IntPair.ordered(i, target));

            if (edgesSourceToNeighbor > 0 || edgesTargetToNeighbor > 0) {
				matrix.put(IntPair.ordered(i, c.getId()),
							edgesTargetToNeighbor + edgesSourceToNeighbor);
			}
        }
        for (int i = 0; i <= maxId; i++) {
            Integer edgesNewToNeighbor = matrix.get(IntPair.ordered(i, c.getId()));
            deltaQ.remove(c.getId(), i);
            if (edgesNewToNeighbor != null && edgesNewToNeighbor > 0) {
//				dQ = 2 * (e_ij - a_i * a_j)
                double ai = c.getEdgesOutside() / totalEdgesCount;
                double aj = communities.get(i).getEdgesOutside() / totalEdgesCount;
                double eij = edgesNewToNeighbor / totalEdgesCount;
                Double value = 2 * (eij - ai * aj);
                deltaQ.add(c.getId(), i, value);
            }
        }
    }

    public void print() {
        System.out.println("Communities:");
        for (Map.Entry<Integer, Community> entrySet : communities.entrySet()) {
            Community community = entrySet.getValue();
            System.out.println(community);
        }
        System.out.println("---------------------");
        System.out.println("Connections:");
        for (Map.Entry<IntPair, Integer> entrySet : matrix.entrySet()) {
            Integer connections = entrySet.getValue();
            Integer i = entrySet.getKey().getFirst();
            Integer j = entrySet.getKey().getSecond();
            System.out.println("\t" + i + " -> " + j + " [" + connections + "]");
        }
        System.out.println("---------------------");
    }

}
