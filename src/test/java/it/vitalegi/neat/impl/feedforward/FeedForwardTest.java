package it.vitalegi.neat.impl.feedforward;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.Random;
import it.vitalegi.neat.impl.UniqueId;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class FeedForwardTest {

	@BeforeClass
	public static void initClass() {
		Random.init();
	}

	Logger log = LoggerFactory.getLogger(FeedForwardTest.class);

	UniqueId uniqueId;

	@Before
	public void init() {
		uniqueId = new UniqueId();
	}

	@Test
	public void testIfNoConnections() {
		Gene gene = Gene.newInstance(uniqueId, 1, 2);
		double[] outputs = new FeedForward(gene).feedForward(new double[] { 1.0 });
		Assert.assertArrayEquals(new double[] { 0.5, 0.5 }, outputs, 0.0001);
	}
}
