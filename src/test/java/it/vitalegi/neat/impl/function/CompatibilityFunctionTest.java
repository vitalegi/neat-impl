package it.vitalegi.neat.impl.function;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.Random;
import it.vitalegi.neat.impl.UniqueId;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class CompatibilityFunctionTest {

	UniqueId uniqueId;

	@BeforeClass
	public static void initClass() {
		Random.init();
	}

	@Before
	public void init() {
		uniqueId = new UniqueId();
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

		Gene parent = Gene.newInstance(uniqueId);

		parent.addConnection(1, 2, W11, true);
		parent.addConnection(1, 3, W2, true);
		parent.addConnection(1, 4, W2, true);

		Gene gene1 = parent.clone();
		gene1.addConnection(2, 3, 4.0, true);
		gene1.addConnection(2, 4, 4.0, true);

		gene1.getConnection(1, 2).setWeight(W12);

		Gene gene2 = parent.clone();
		gene2.addConnection(2, 5, 4.0, true);
		gene2.addConnection(2, 6, 4.0, true);

		double distance = distanceImpl.getDistance(gene1, gene2);

		Assert.assertEquals(distance, distanceImpl.getDistance(5, 4, (Math.abs(W11 - W12) + W2 - W2 + W3 - W3) / 3),
				0.001);
	}
}