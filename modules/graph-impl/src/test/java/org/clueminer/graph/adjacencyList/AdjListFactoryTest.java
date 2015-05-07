package org.clueminer.graph.adjacencyList;

import org.clueminer.graph.api.Edge;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Hamster
 */
public class AdjListFactoryTest {

	@Test
	public void singletonTest() {
		AdjListFactory first = AdjListFactory.getInstance();
		AdjListFactory second = AdjListFactory.getInstance();
		Assert.assertSame(first, second);
	}

	@Test
	public void newNodeTest() {
		long id;
		id = AdjListFactory.getInstance().newNode().getId();
		Assert.assertEquals(0, id);
		id = AdjListFactory.getInstance().newNode().getId();
		Assert.assertEquals(1, id);
		id = AdjListFactory.getInstance().newNode().getId();
		Assert.assertEquals(2, id);
		id = AdjListFactory.getInstance().newNode().getId();
		Assert.assertEquals(3, id);
		id = AdjListFactory.getInstance().newNode().getId();
		Assert.assertEquals(4, id);
	}

	@Test
	public void newEdgeTest() {
		AdjListNode source = new AdjListNode(1);
		AdjListNode target = new AdjListNode(2);
		Edge edge = AdjListFactory.getInstance().newEdge(source, target);
		Assert.assertEquals(0, edge.getId());
		Assert.assertSame(source, edge.getSource());
		Assert.assertSame(target, edge.getTarget());
		source = new AdjListNode(3);
		target = new AdjListNode(4);
		edge = AdjListFactory.getInstance().newEdge(source, target, true);
		Assert.assertEquals(1, edge.getId());
		Assert.assertSame(source, edge.getSource());
		Assert.assertSame(target, edge.getTarget());
		Assert.assertTrue(edge.isDirected());
	}
}
