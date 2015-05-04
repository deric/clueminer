package org.clueminer.fastcommunity;

import java.util.Set;

/**
 *
 * @author Hamster
 */
public class Community {
	Set<Integer> nodes;
	Double eii;

	public void add(int i) {
		nodes.add(i);
	}

	public Set<Integer> getNodes() {
		return nodes;
	}

	public void addAll(Community right) {
		nodes.addAll(right.getNodes());
	}
}
