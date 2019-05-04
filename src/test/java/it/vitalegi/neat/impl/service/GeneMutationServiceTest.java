package it.vitalegi.neat.impl.service;

import org.hamcrest.Matchers;
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
import it.vitalegi.neat.impl.configuration.NeatConfigFactory;
import it.vitalegi.neat.impl.function.CompatibilityDistanceImpl;
import it.vitalegi.neat.impl.util.ContextUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class GeneMutationServiceTest extends AbstractTest {

	@BeforeClass
	public static void initClass() {
		Random.init();
	}

	private UniqueId uniqueId;

	@Before
	public void init() {
		uniqueId = new UniqueId();
		init(ContextUtil.builder().inject());

	}

	@Test
	public void testAddNodeShouldGiveDifferentIds() {
		long in1 = 1;
		long out1 = 2;

		Gene gene1 = geneService.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 }, new long[0]);
		geneService.addConnection(gene1, in1, out1, 1.0, true);

		Gene gene2 = geneService.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 }, new long[0]);
		geneService.addConnection(gene2, in1, out1, 1.0, true);

		geneMutationServiceImpl.mutateAddRandomNode(gene1);
		geneMutationServiceImpl.mutateAddRandomNode(gene2);

		Assert.assertEquals(1, geneService.getMatchingGenesCount(gene1, gene2));
	}

	@Test
	public void testCheckConnectedIfDirectConnection() {
		long in1 = 1;
		long out1 = 2;

		Gene gene = geneService.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 }, new long[0]);

		geneService.addConnection(gene, in1, out1, 1.0, true);
		Assert.assertTrue(geneMutationServiceImpl.checkConnected(gene, geneService.getNodeById(gene, in1),
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
		Assert.assertTrue(geneMutationServiceImpl.checkConnected(gene, geneService.getNodeById(gene, in1),
				geneService.getNodeById(gene, h2)));
	}

	@Test
	public void testCheckConnectedIfNotConnected() {
		long in1 = 1;
		long out1 = 2;

		Gene gene = geneService.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 }, new long[0]);

		Assert.assertFalse(geneMutationServiceImpl.checkConnected(gene, geneService.getNodeById(gene, in1),
				geneService.getNodeById(gene, out1)));
	}

	@Test
	public void testIsValidConnectionIfCreatesLoopShouldNotBeValid() {
		long in1 = 1;
		long out1 = 2;

		Gene gene = geneService.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 }, new long[0]);

		geneService.addConnection(gene, in1, out1, 1.0, true);
		Assert.assertFalse(geneMutationServiceImpl.isValidConnection(gene, geneService.getNodeById(gene, in1),
				geneService.getNodeById(gene, out1)));
	}

	@Test
	public void testIsValidConnectionIfExistsShouldNotBeValid() {
		long in1 = 1;
		long out1 = 2;
		Gene gene = geneService.newInstance(uniqueId, 0, new long[] { in1 }, new long[] { out1 }, new long[0]);
		geneService.addConnection(gene, in1, out1, 1, false);
		Assert.assertFalse(geneMutationServiceImpl.isValidConnection(gene, geneService.getNodeById(gene, in1),
				geneService.getNodeById(gene, out1)));
	}

	@Test
	public void testMutateAddRandomNodeIfConnectionExists() {

		Gene gene = geneService.newInstance(uniqueId, 1, 1);
		long in1 = geneService.getInputNode(gene, 0);
		long out1 = geneService.getOutputNode(gene, 0);
		geneService.addConnection(gene, in1, out1, 0.7, true);
		gene = geneMutationServiceImpl.mutateAddRandomNode(gene);
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
		geneMutationServiceImpl.mutateAddRandomNode(gene1);
		Assert.assertEquals(0, gene1.getConnections().size());
	}

	@Test
	public void testMutateUniformWeightsShouldPreserveCompatibility() {

		GeneMutationService predictableGeneMutation = new PredictableGeneMutationService().uniformWeightsPerturbation();

		double delta = 0.2;
		init(ContextUtil.builder()//
				.neatConfig(NeatConfigFactory.create()//
						.maxWeight(1.0)//
						.minWeight(-1.0)//
						.uniformPerturbation(0.1)//
						.build())
				.compatibilityDistance(new CompatibilityDistanceImpl(delta, 1, 2))//
				.inject());

		Gene gene1 = geneService.newInstance(uniqueId, 1, 1);

		for (int i = 0; i < 100; i++) {
			gene1 = geneMutationService.mutate(neatConfig, gene1);
			Gene gene2 = predictableGeneMutation.mutate(neatConfig, geneService.clone(gene1));

			Assert.assertThat("Iteration " + i, compatibilityDistance.getDistance(gene1, gene2),
					Matchers.lessThan(delta));
		}
	}
}