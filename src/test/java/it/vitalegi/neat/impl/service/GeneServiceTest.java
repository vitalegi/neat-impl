package it.vitalegi.neat.impl.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import it.vitalegi.neat.AbstractTest;
import it.vitalegi.neat.impl.Connection;
import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.Node;
import it.vitalegi.neat.impl.Random;
import it.vitalegi.neat.impl.UniqueId;
import it.vitalegi.neat.impl.util.ContextUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class GeneServiceTest extends AbstractTest {

	@BeforeClass
	public static void initClass() {
		Random.init();
	}

	private UniqueId uniqueId;

	@Test
	public void addConnectionShouldAddConnection() {
		Gene gene = geneService.newInstance(uniqueId);
		geneService.addConnection(gene, 1, 4, 0, true);
		Assert.assertEquals(1, gene.getConnections().size());
		Assert.assertNotNull(gene.getConnections().get(0));
		Assert.assertEquals(1, geneService.getConnectionByIndex(gene, 0).getFromNode().getId());
		Assert.assertEquals(4, geneService.getConnectionByIndex(gene, 0).getToNode().getId());
	}

	@Test
	public void addConnectionShouldAddNode() {
		Gene gene = geneService.newInstance(uniqueId);
		geneService.addConnection(gene, 1, 4, 0, true);
		Assert.assertNotNull(geneService.getNodeById(gene, 1));
		Assert.assertNotNull(geneService.getNodeById(gene, 4));
	}

	@Test
	public void addConnectionShouldPreserveOrder() {
		Gene gene = geneService.newInstance(uniqueId);

		geneService.addConnection(gene, 5, 1, 4, 0, true);
		geneService.addConnection(gene, 6, 2, 3, 0, false);
		Assert.assertEquals(2, gene.getConnections().size());

		Assert.assertNotNull(gene.getConnections().get(0));
		Assert.assertEquals(5, geneService.getConnectionByIndex(gene, 0).getId());
		Assert.assertEquals(6, geneService.getConnectionByIndex(gene, 1).getId());
	}

	@Test
	public void cloneShouldPreserveConnections() {
		Gene gene = geneService.newInstance(uniqueId);

		geneService.addConnection(gene, 1, 2, 3, 0, true);
		Gene cloned = geneService.clone(gene);
		Assert.assertNotNull(geneService.getConnectionById(cloned, 1));
	}

	@Test
	public void cloneShouldPreserveNodes() {
		Gene gene = geneService.newInstance(uniqueId);

		geneService.addConnection(gene, 0, 1, 2, 0, true);
		Gene cloned = geneService.clone(gene);
		Assert.assertNotNull(geneService.getNodeById(cloned, 1));
		Assert.assertNotNull(geneService.getNodeById(cloned, 2));
	}

	@Before
	public void init() {
		uniqueId = new UniqueId();
		init(ContextUtil.builder().inject());

	}

	@Test
	public void matchingGenesIfCommonAnchestorExistsShouldReturnIndex() {
		Gene parent = geneService.newInstance(uniqueId);

		geneService.addConnection(parent, 1, 4, 0.0, true);
		geneService.addConnection(parent, 2, 3, 0.0, true);

		Gene gene1 = geneService.clone(parent);
		geneService.addConnection(gene1, 3, 5, 0.0, true);

		Gene gene2 = geneService.clone(parent);
		geneService.addNode(gene2, 6, geneService.getConnection(gene2, 1, 4));

		int index = geneService.getMatchingGenesCount(gene1, gene2);
		Assert.assertEquals(2, index);
	}

	@Test
	public void matchingGenesIfCommonAnchestorNotExistsShouldReturnNegativeNumber() {

		Gene gene1 = geneService.newInstance(uniqueId);
		geneService.addConnection(gene1, 3, 5, 0.0, true);

		Gene gene2 = geneService.newInstance(uniqueId);
		geneService.addConnection(gene2, 3, 5, 0.0, true);
		geneService.addNode(gene2, 6, geneService.getConnection(gene2, 3, 5));

		int index = geneService.getMatchingGenesCount(gene1, gene2);
		Assert.assertEquals(0, index);
	}

	@Test
	public void testAddConnectionShouldPreserveIds() {
		long in1 = 1;
		long out1 = 2;

		Gene gene1 = geneService.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 }, new long[0]);
		Connection con1 = geneService.addConnection(gene1, in1, out1, 1.0, true);

		Gene gene2 = geneService.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 }, new long[0]);
		Connection con2 = geneService.addConnection(gene2, in1, out1, 1.0, true);

		Assert.assertTrue(con1.getId() == con2.getId());
	}

	@Test
	public void testAddNodeShouldAddConnections() {
		Gene gene = geneService.newInstance(uniqueId);
		Connection con = geneService.addConnection(gene, 3, 5, 0.0, true);
		geneService.addNode(gene, 4, con);

		Assert.assertNotNull(geneService.getConnection(gene, 3, 4));
		Assert.assertNotNull(geneService.getConnection(gene, 4, 5));
	}

	@Test
	public void testAddNodeShouldDisableOldConnection() {
		Gene gene = geneService.newInstance(uniqueId);
		Connection con = geneService.addConnection(gene, 3, 5, 0.0, true);
		geneService.addNode(gene, 4, con);

		Assert.assertFalse(geneService.getConnection(gene, 3, 5).isEnabled());
	}

	@Test
	public void testAddNodeShouldGiveDifferentIds() {
		long in1 = 1;
		long out1 = 2;

		Gene gene1 = geneService.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 }, new long[0]);
		Connection con1 = geneService.addConnection(gene1, in1, out1, 1.0, true);

		Gene gene2 = geneService.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 }, new long[0]);
		Connection con2 = geneService.addConnection(gene2, in1, out1, 1.0, true);

		geneService.mutateAddRandomNode(gene1);
		geneService.mutateAddRandomNode(gene2);

		Assert.assertEquals(1, geneService.getMatchingGenesCount(gene1, gene2));
	}

	@Test
	public void testCheckConnectedIfDirectConnection() {
		long in1 = 1;
		long out1 = 2;

		Gene gene = geneService.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 }, new long[0]);

		geneService.addConnection(gene, in1, out1, 1.0, true);
		Assert.assertTrue(geneService.checkConnected(gene, geneService.getNodeById(gene, in1),
				geneService.getNodeById(gene, out1)));
	}

	@Test
	public void testCheckConnectedIfIndirectConnection() {
		long in1 = 1;
		long out1 = 2;
		long h1 = 3;
		long h2 = 4;

		Gene gene = geneService.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 }, new long[0]);

		geneService.addConnection(gene, in1, out1, 1.0, true);
		geneService.addNode(gene, h1, geneService.getConnection(gene, in1, out1));
		geneService.addNode(gene, h2, geneService.getConnection(gene, h1, out1));
		Assert.assertTrue(geneService.checkConnected(gene, geneService.getNodeById(gene, in1),
				geneService.getNodeById(gene, h2)));
	}

	@Test
	public void testCheckConnectedIfNotConnected() {
		long in1 = 1;
		long out1 = 2;

		Gene gene = geneService.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 }, new long[0]);

		Assert.assertFalse(geneService.checkConnected(gene, geneService.getNodeById(gene, in1),
				geneService.getNodeById(gene, out1)));
	}

	@Test
	public void testGetDisjointGenesCountIfCommonAnchestor() {
		Gene parent = geneService.newInstance(uniqueId);

		geneService.addConnection(parent, 1, 4, 0.0, true);
		geneService.addConnection(parent, 2, 3, 0.0, true);

		Gene gene1 = geneService.clone(parent);
		geneService.addConnection(gene1, 3, 5, 0.0, true);

		Gene gene2 = geneService.clone(parent);
		geneService.addNode(gene2, 6, geneService.getConnection(gene2, 1, 4));
		geneService.addNode(gene2, 7, geneService.getConnection(gene2, 1, 4));

		int count = geneService.getDisjointGenesCount(gene1, gene2);
		Assert.assertEquals(5, count);
	}

	@Test
	public void testGetDisjointGenesCountIfCommonAnchestorNotExists() {

		Gene gene1 = geneService.newInstance(uniqueId);
		geneService.addConnection(gene1, 3, 5, 0.0, true);

		Gene gene2 = geneService.newInstance(uniqueId);
		geneService.addConnection(gene2, 4, 5, 0.0, true);
		geneService.addConnection(gene2, 2, 5, 0.0, true);

		int count = geneService.getDisjointGenesCount(gene1, gene2);
		Assert.assertEquals(3, count);

	}

	@Test
	public void testGetOffspringIfCommonAnchestor() {

		Gene parent = geneService.newInstance(uniqueId);

		geneService.addConnection(parent, 10, 1, 4, 0, true);
		geneService.addConnection(parent, 20, 2, 4, 0, false);

		Gene gene1 = geneService.copy(parent, geneService.newInstance(uniqueId));
		geneService.addConnection(gene1, 30, 3, 5, 0, true);

		Gene gene2 = geneService.copy(parent, geneService.newInstance(uniqueId));

		geneService.addConnection(gene2, 40, 6, 5, 0, true);

		Gene offspring = geneService.offspring(gene1, gene2);
		Assert.assertNotNull(geneService.getConnectionById(offspring, 10));
		Assert.assertNotNull(geneService.getConnectionById(offspring, 20));

		Connection c30 = geneService.getConnectionById(offspring, 30);
		Connection c40 = geneService.getConnectionById(offspring, 40);
		Assert.assertTrue((c30 != null && c40 == null) || (c30 == null && c40 != null));
		Assert.assertEquals(3, offspring.getSize());
	}

	@Test
	public void testGetOffspringIfNoCommonAnchestor() {

		Gene gene1 = geneService.newInstance(uniqueId);
		geneService.addConnection(gene1, 30, 3, 5, 0, true);

		Gene gene2 = geneService.newInstance(uniqueId);

		geneService.addConnection(gene2, 40, 6, 5, 0, true);

		Gene offspring = geneService.offspring(gene1, gene2);

		Connection c30 = geneService.getConnectionById(offspring, 30);
		Connection c40 = geneService.getConnectionById(offspring, 40);
		Assert.assertTrue((c30 != null && c40 == null) || (c30 == null && c40 != null));
		Assert.assertEquals(1, offspring.getSize());
	}

	@Test
	public void testIsValidConnectionIfCreatesLoopShouldNotBeValid() {
		long in1 = 1;
		long out1 = 2;

		Gene gene = geneService.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 }, new long[0]);

		geneService.addConnection(gene, in1, out1, 1.0, true);
		Assert.assertFalse(geneService.isValidConnection(gene, geneService.getNodeById(gene, in1),
				geneService.getNodeById(gene, out1)));
	}

	@Test
	public void testIsValidConnectionIfExistsShouldNotBeValid() {
		long in1 = 1;
		long out1 = 2;
		Gene gene = geneService.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 }, new long[0]);
		geneService.addConnection(gene, in1, out1, 1, false);
		Assert.assertFalse(geneService.isValidConnection(gene, geneService.getNodeById(gene, in1),
				geneService.getNodeById(gene, out1)));
	}

	@Test
	public void testMutateAddRandomNodeIfConnectionExists() {

		Gene gene = geneService.newInstance(uniqueId, 1, 1);
		long in1 = geneService.getInputNode(gene, 0);
		long out1 = geneService.getOutputNode(gene, 0);
		geneService.addConnection(gene, in1, out1, 0.7, true);
		gene = geneService.mutateAddRandomNode(gene);
		Assert.assertEquals(3, gene.getConnections().size());
		Assert.assertEquals(3, gene.getNodes().size());

		Node hidden = gene.getNodes().stream()//
				.filter(n -> n.getId() != in1)//
				.filter(n -> n.getId() != out1)//
				.findFirst().orElse(null);

		Connection in1ToHidden = geneService.getConnection(gene, in1, hidden.getId());
		Connection in1ToOut1 = geneService.getConnection(gene, in1, out1);
		Connection hiddenToOut1 = geneService.getConnection(gene, hidden.getId(), out1);

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
	public void testMutateAddRandomNodeIfNoConnections() {

		Gene gene1 = geneService.newInstance(uniqueId, 1, 1);
		geneService.mutateAddRandomNode(gene1);
		Assert.assertEquals(0, gene1.getConnections().size());
	}
}