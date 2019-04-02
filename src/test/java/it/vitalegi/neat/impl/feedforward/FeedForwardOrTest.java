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
public class FeedForwardOrTest {

	Gene gene;

	UniqueId uniqueId;

	@BeforeClass
	public static void initClass() {
		Random.init();
	}

	@Before
	public void init() {
		uniqueId = new UniqueId();
		gene = Gene.newInstance(uniqueId, 2, 1);
		gene.addConnection(gene.getSortedInputNodes().get(0), gene.getSortedOutputNodes().get(0), 0.6, true);
		gene.addConnection(gene.getSortedInputNodes().get(1), gene.getSortedOutputNodes().get(0), 0.6, true);
	}

	@Test
	public void testAnd00() {
		bool("OR", gene, 0, 0, 0);
	}

	@Test
	public void testAnd01() {
		bool("OR", gene, 0, 1, 1);
	}

	@Test
	public void testAnd10() {
		bool("OR", gene, 1, 0, 1);
	}

	@Test
	public void testAnd11() {
		bool("OR", gene, 1, 1, 1);
	}

	private void bool(String name, Gene gene, int in1, int in2, int out) {
		String desc = name + "(" + in1 + "," + in2 + ") = " + out;
		log.info("Start {}", desc);
		double[] outputs = new FeedForward(gene).feedForward(new double[] { in1, in2 });
		log.info("End {}", desc);
		int o = outputs[0] > 0.6 ? 1 : 0;
		Assert.assertEquals(desc, out, o);
	}

	Logger log = LoggerFactory.getLogger(FeedForwardOrTest.class);
}
