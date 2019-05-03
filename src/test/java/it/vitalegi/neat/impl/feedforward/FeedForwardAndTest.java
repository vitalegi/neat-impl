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
public class FeedForwardAndTest {

	@BeforeClass
	public static void initClass() {
		Random.init();
	}

	Gene gene;

	Logger log = LoggerFactory.getLogger(FeedForwardAndTest.class);

	UniqueId uniqueId;

	private void bool(String name, Gene gene, int in1, int in2, int out) {
		String desc = name + "(" + in1 + "," + in2 + ") = " + out;
		log.info("Start {}", desc);
		double[] outputs = new FeedForwardImpl(gene).feedForward(new double[] { in1, in2 });
		log.info("End {}", desc);
		int o = outputs[0] > 0.9 ? 1 : 0;
		Assert.assertEquals(desc, out, o);
	}

	@Before
	public void init() {
		uniqueId = new UniqueId();
		gene = Gene.newInstance(uniqueId, 2, 1);
		gene.addConnection(gene.getSortedInputNodes().get(0), gene.getSortedOutputNodes().get(0), 0.3, true);
		gene.addConnection(gene.getSortedInputNodes().get(1), gene.getSortedOutputNodes().get(0), 0.3, true);
	}

	@Test
	public void testAnd00() {
		bool("And", gene, 0, 0, 0);
	}

	@Test
	public void testAnd01() {
		bool("And", gene, 0, 1, 0);
	}

	@Test
	public void testAnd10() {
		bool("And", gene, 1, 0, 0);
	}

	@Test
	public void testAnd11() {
		bool("And", gene, 1, 1, 1);
	}
}
