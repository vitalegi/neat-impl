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

import it.vitalegi.neat.AbstractTest;
import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.Random;
import it.vitalegi.neat.impl.UniqueId;
import it.vitalegi.neat.impl.util.ContextUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class FeedForwardOrTest extends AbstractTest {

	@BeforeClass
	public static void initClass() {
		Random.init();
	}

	Gene gene;

	Logger log = LoggerFactory.getLogger(FeedForwardOrTest.class);

	UniqueId uniqueId;

	private void bool(String name, Gene gene, int in1, int in2, int out) {
		String desc = name + "(" + in1 + "," + in2 + ") = " + out;
		log.info("Start {}", desc);
		double[] outputs = feedForward.feedForward(gene, new double[] { in1, in2 });
		log.info("End {}", desc);
		int o = outputs[0] > 0.6 ? 1 : 0;
		Assert.assertEquals(desc, out, o);
	}

	@Before
	public void init() {
		uniqueId = new UniqueId();
		init(ContextUtil.builder().inject());
		gene = geneService.newInstance(uniqueId, 2, 1);

		geneService.addConnection(gene, geneService.getSortedInputNodes(gene).get(0),
				geneService.getSortedOutputNodes(gene).get(0), 0.6, true);
		geneService.addConnection(gene, geneService.getSortedInputNodes(gene).get(1),
				geneService.getSortedOutputNodes(gene).get(0), 0.6, true);
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
}
