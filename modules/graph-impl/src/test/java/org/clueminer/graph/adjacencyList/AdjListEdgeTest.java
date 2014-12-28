package org.clueminer.graph.adjacencyList;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Hamster
 */
public class AdjListEdgeTest {

	@Test
	public void completeConstructorTest() {
		long id = 5;
		AdjListNode source = new AdjListNode(1);
		AdjListNode target = new AdjListNode(2);
		double weight = 2.5;
		boolean directed = true;
		AdjListEdge edge = new AdjListEdge(id, source, target, directed, weight);
		Assert.assertEquals(id, edge.getId());
		Assert.assertSame(source, edge.getSource());
		Assert.assertSame(target, edge.getTarget());
		Assert.assertEquals(directed, edge.isDirected());
		Assert.assertEquals(weight, edge.getWeight(), 0.001);
	}

	@Test
	public void defaultConstructorTest() {
		long id = 5;
		AdjListNode source = new AdjListNode(1);
		AdjListNode target = new AdjListNode(2);
		AdjListEdge edge = new AdjListEdge(id, source, target);
		Assert.assertEquals(id, edge.getId());
		Assert.assertSame(source, edge.getSource());
		Assert.assertSame(target, edge.getTarget());
		Assert.assertFalse(edge.isDirected());
		Assert.assertEquals(1, edge.getWeight(), 0.001);
	}
}
