package it.vitalegi.neat.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class GeneTest {

	private UniqueId uniqueId;

	@BeforeClass
	public static void initClass() {
		Random.init();
	}

	@Before
	public void init() {
		uniqueId = new UniqueId();
	}

	@Test
	public void addConnectionShouldAddNode() {
		Gene gene = Gene.newInstance(uniqueId);
		gene.addConnection(1, 4, 0, true);
		Assert.assertNotNull(gene.getNodeById(1));
		Assert.assertNotNull(gene.getNodeById(4));
	}

	@Test
	public void addConnectionShouldAddConnection() {
		Gene gene = Gene.newInstance(uniqueId);
		gene.addConnection(1, 4, 0, true);
		Assert.assertEquals(1, gene.getConnections().size());
		Assert.assertNotNull(gene.getConnections().get(0));
		Assert.assertEquals(1, gene.getConnectionByIndex(0).getFromNode().getId());
		Assert.assertEquals(4, gene.getConnectionByIndex(0).getToNode().getId());
	}

	@Test
	public void addConnectionShouldPreserveOrder() {
		Gene gene = Gene.newInstance(uniqueId);

		gene.addConnection(5, 1, 4, 0, true);
		gene.addConnection(6, 2, 3, 0, false);
		Assert.assertEquals(2, gene.getConnections().size());

		Assert.assertNotNull(gene.getConnections().get(0));
		Assert.assertEquals(5, gene.getConnectionByIndex(0).getId());
		Assert.assertEquals(6, gene.getConnectionByIndex(1).getId());
	}

	@Test
	public void cloneShouldPreserveNodes() {
		Gene gene = Gene.newInstance(uniqueId);

		gene.addConnection(0, 1, 2, 0, true);
		Gene cloned = gene.clone();
		Assert.assertNotNull(cloned.getNodeById(1));
		Assert.assertNotNull(cloned.getNodeById(2));
	}

	@Test
	public void cloneShouldPreserveConnections() {
		Gene gene = Gene.newInstance(uniqueId);

		gene.addConnection(1, 2, 3, 0, true);
		Gene cloned = gene.clone();
		Assert.assertNotNull(cloned.getConnectionById(1));
	}

	@Test
	public void matchingGenesIfCommonAnchestorExistsShouldReturnIndex() {
		Gene parent = Gene.newInstance(uniqueId);

		parent.addConnection(1, 4, 0.0, true);
		parent.addConnection(2, 3, 0.0, true);

		Gene gene1 = parent.clone();
		gene1.addConnection(3, 5, 0.0, true);

		Gene gene2 = parent.clone();
		gene2.addNode(6, gene2.getConnection(1, 4));

		int index = gene1.getMatchingGenesCount(gene2);
		Assert.assertEquals(2, index);
	}

	@Test
	public void matchingGenesIfCommonAnchestorNotExistsShouldReturnNegativeNumber() {

		Gene gene1 = Gene.newInstance(uniqueId);
		gene1.addConnection(3, 5, 0.0, true);

		Gene gene2 = Gene.newInstance(uniqueId);
		gene2.addConnection(3, 5, 0.0, true);
		gene2.addNode(6, gene2.getConnection(3, 5));

		int index = gene1.getMatchingGenesCount(gene2);
		Assert.assertEquals(0, index);
	}

	@Test
	public void testAddNodeShouldDisableOldConnection() {
		Gene gene = Gene.newInstance(uniqueId);
		Connection con = gene.addConnection(3, 5, 0.0, true);
		gene.addNode(4, con);

		Assert.assertFalse(gene.getConnection(3, 5).isEnabled());
	}

	@Test
	public void testAddNodeShouldAddConnections() {
		Gene gene = Gene.newInstance(uniqueId);
		Connection con = gene.addConnection(3, 5, 0.0, true);
		gene.addNode(4, con);

		Assert.assertNotNull(gene.getConnection(3, 4));
		Assert.assertNotNull(gene.getConnection(4, 5));
	}

	@Test
	public void testGetOffspringIfCommonAnchestor() {

		Gene parent = Gene.newInstance(uniqueId);

		parent.addConnection(10, 1, 4, 0, true);
		parent.addConnection(20, 2, 4, 0, false);

		Gene gene1 = parent.copy(Gene.newInstance(uniqueId));
		gene1.addConnection(30, 3, 5, 0, true);

		Gene gene2 = parent.copy(Gene.newInstance(uniqueId));

		gene2.addConnection(40, 6, 5, 0, true);

		Gene offspring = gene1.offspring(gene2);
		Assert.assertNotNull(offspring.getConnectionById(10));
		Assert.assertNotNull(offspring.getConnectionById(20));
		Assert.assertNotNull(offspring.getConnectionById(30));
		Assert.assertNotNull(offspring.getConnectionById(40));
		Assert.assertEquals(4, offspring.getSize());
	}

	@Test
	public void testGetOffspringIfNoCommonAnchestor() {

		Gene gene1 = Gene.newInstance(uniqueId);
		gene1.addConnection(30, 3, 5, 0, true);

		Gene gene2 = Gene.newInstance(uniqueId);

		gene2.addConnection(40, 6, 5, 0, true);

		Gene offspring = gene1.offspring(gene2);
		Assert.assertNotNull(offspring.getConnectionById(30));
		Assert.assertEquals(2, offspring.getSize());
	}

	@Test
	public void testGetDisjointGenesCountIfCommonAnchestor() {
		Gene parent = Gene.newInstance(uniqueId);

		parent.addConnection(1, 4, 0.0, true);
		parent.addConnection(2, 3, 0.0, true);

		Gene gene1 = parent.clone();
		gene1.addConnection(3, 5, 0.0, true);

		Gene gene2 = parent.clone();
		gene2.addNode(6, gene2.getConnection(1, 4));
		gene2.addNode(7, gene2.getConnection(1, 4));

		int count = gene1.getDisjointGenesCount(gene2);
		Assert.assertEquals(5, count);
	}

	@Test
	public void testGetDisjointGenesCountIfCommonAnchestorNotExists() {

		Gene gene1 = Gene.newInstance(uniqueId);
		gene1.addConnection(3, 5, 0.0, true);

		Gene gene2 = Gene.newInstance(uniqueId);
		gene2.addConnection(4, 5, 0.0, true);
		gene2.addConnection(2, 5, 0.0, true);

		int count = gene1.getDisjointGenesCount(gene2);
		Assert.assertEquals(3, count);

	}

	@Test
	public void testMutateAddRandomNodeIfNoConnections() {

		Gene gene1 = Gene.newInstance(uniqueId, 1, 1);
		gene1.mutateAddRandomNode();
		Assert.assertEquals(0, gene1.getConnections().size());
	}

	@Test
	public void testMutateAddRandomNodeIfConnectionExists() {

		Gene gene = Gene.newInstance(uniqueId, 1, 1);
		long in1 = gene.getInputNode(0);
		long out1 = gene.getOutputNode(0);
		gene.addConnection(in1, out1, 0.7, true);
		gene.mutateAddRandomNode();
		Assert.assertEquals(3, gene.getConnections().size());
		Assert.assertEquals(3, gene.getNodes().size());

		Node hidden = gene.getNodes().stream()//
				.filter(n -> n.getId() != in1)//
				.filter(n -> n.getId() != out1)//
				.findFirst().orElse(null);

		Connection in1ToHidden = gene.getConnection(in1, hidden.getId());
		Connection in1ToOut1 = gene.getConnection(in1, out1);
		Connection hiddenToOut1 = gene.getConnection(hidden.getId(), out1);

		Assert.assertEquals(in1, in1ToHidden.getFromNode().getId());
		Assert.assertEquals(hidden.getId(), in1ToHidden.getToNode().getId());
		Assert.assertEquals(in1, in1ToOut1.getFromNode().getId());
		Assert.assertEquals(out1, in1ToOut1.getToNode().getId());
		Assert.assertEquals(hidden.getId(), hiddenToOut1.getFromNode().getId());
		Assert.assertEquals(out1, hiddenToOut1.getToNode().getId());

		Assert.assertTrue("old connection should be disabled", !in1ToOut1.isEnabled());
		Assert.assertTrue("new connection should be enabled", in1ToHidden.isEnabled());
		Assert.assertTrue("new connection should be enabled", hiddenToOut1.isEnabled());
		Assert.assertEquals("connection to new node should have weight = 1", 1, in1ToHidden.getWeight(), 0.01);
		Assert.assertEquals("connection from new node should have weight = old weight", 0.7, hiddenToOut1.getWeight(),
				0.01);
	}

	@Test
	public void testCheckConnectedIfNotConnected() {
		long in1 = 1;
		long out1 = 2;

		Gene gene = Gene.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 });

		Assert.assertFalse(gene.checkConnected(gene.getNodeById(in1), gene.getNodeById(out1)));
	}

	@Test
	public void testCheckConnectedIfDirectConnection() {
		long in1 = 1;
		long out1 = 2;

		Gene gene = Gene.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 });

		gene.addConnection(in1, out1, 1.0, true);
		Assert.assertTrue(gene.checkConnected(gene.getNodeById(in1), gene.getNodeById(out1)));
	}

	@Test
	public void testCheckConnectedIfIndirectConnection() {
		long in1 = 1;
		long out1 = 2;
		long h1 = 3;
		long h2 = 4;

		Gene gene = Gene.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 });

		gene.addConnection(in1, out1, 1.0, true);
		gene.addNode(h1, gene.getConnection(in1, out1));
		gene.addNode(h2, gene.getConnection(h1, out1));
		Assert.assertTrue(gene.checkConnected(gene.getNodeById(in1), gene.getNodeById(h2)));
	}

	@Test
	public void testIsValidConnectionIfExistsShouldNotBeValid() {
		long in1 = 1;
		long out1 = 2;
		Gene gene = Gene.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 });
		gene.addConnection(in1, out1, 1, false);
		Assert.assertFalse(gene.isValidConnection(gene.getNodeById(in1), gene.getNodeById(out1)));
	}

	@Test
	public void testIsValidConnectionIfCreatesLoopShouldNotBeValid() {
		long in1 = 1;
		long out1 = 2;

		Gene gene = Gene.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 });

		gene.addConnection(in1, out1, 1.0, true);
		Assert.assertFalse(gene.isValidConnection(gene.getNodeById(in1), gene.getNodeById(out1)));
	}

	@Test
	public void testAddConnectionShouldPreserveIds() {
		long in1 = 1;
		long out1 = 2;

		Gene gene1 = Gene.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 });
		Connection con1 = gene1.addConnection(in1, out1, 1.0, true);

		Gene gene2 = Gene.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 });
		Connection con2 = gene2.addConnection(in1, out1, 1.0, true);

		Assert.assertTrue(con1.getId() == con2.getId());
	}

	@Test
	public void testAddNodeShouldGiveDifferentIds() {
		long in1 = 1;
		long out1 = 2;

		Gene gene1 = Gene.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 });
		Connection con1 = gene1.addConnection(in1, out1, 1.0, true);

		Gene gene2 = Gene.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 });
		Connection con2 = gene2.addConnection(in1, out1, 1.0, true);

		gene1.mutateAddRandomNode();
		gene2.mutateAddRandomNode();

		Assert.assertEquals(1, gene1.getMatchingGenesCount(gene2));
	}
}