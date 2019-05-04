package it.vitalegi.neat.impl.function;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import it.vitalegi.neat.AbstractTest;
import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.Random;
import it.vitalegi.neat.impl.UniqueId;
import it.vitalegi.neat.impl.util.ContextUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class CompatibilityFunctionTest extends AbstractTest {

	@BeforeClass
	public static void initClass() {
		Random.init();
	}

	UniqueId uniqueId;

	@Before
	public void init() {
		uniqueId = new UniqueId();
		init(ContextUtil.builder().inject());
	}

	@Test
	public void testGetWeightDifferenceIfCommonAncestor() {
		final double C1 = 3.0;
		final double C2 = 2.0;
		final double W11 = 1.0;
		final double W12 = 10.0;
		final double W2 = 100.0;
		final double W3 = 200.0;

		CompatibilityDistanceImpl distanceImpl = new CompatibilityDistanceImpl(1, C1, C2);
		distanceImpl.geneService = geneService;

		Gene parent = geneService.newInstance(uniqueId);

		geneService.addConnection(parent, 1, 2, W11, true);
		geneService.addConnection(parent, 1, 3, W2, true);
		geneService.addConnection(parent, 1, 4, W2, true);

		Gene gene1 = geneService.clone(parent);
		geneService.addConnection(gene1, 2, 3, 4.0, true);
		geneService.addConnection(gene1, 2, 4, 4.0, true);

		geneService.getConnection(gene1, 1, 2).setWeight(W12);

		Gene gene2 = geneService.clone(parent);
		geneService.addConnection(gene2, 2, 5, 4.0, true);
		geneService.addConnection(gene2, 2, 6, 4.0, true);

		double distance = distanceImpl.getDistance(gene1, gene2);

		Assert.assertEquals(distance, distanceImpl.getDistance(5, 4, (Math.abs(W11 - W12) + W2 - W2 + W3 - W3) / 3),
				0.001);
	}
}