package it.vitalegi.neat.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import it.vitalegi.neat.impl.analysis.EvolutionAnalysis;
import it.vitalegi.neat.impl.function.CompatibilityDistanceImpl;
import it.vitalegi.neat.impl.util.StringUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class XorPlayerTest {

	private static class XorPlayer extends Player {

		private double execute(double in1, double in2) {
			return feedForward(new double[] { in1, in2 })[0];
		}

		static final double ONE = 1.0;
		static final double ZERO = -1.0;

		@Override
		public void run() {
			double sum = 0;
			int errors = 0;
			for (int i = 0; i < 100; i++) {
				double in1 = Random.nextBoolean() ? ONE : ZERO;
				double in2 = Random.nextBoolean() ? ONE : ZERO;
				double out = execute(in1, in2);
				double expected = in1 == in2 ? ZERO : ONE;
				double diff = Math.abs(out - expected);
				if (diff > 0.3) {
					errors++;
				}
				sum += 1 - diff * diff;
			}
			fitness = sum - errors;
		}

		public void assertPerfect() {
			log.info("{} XOR {} = expected {} actual {}", ONE, ZERO, ONE, execute(ONE, ZERO));
			log.info("{} XOR {} = expected {} actual {}", ZERO, ONE, ONE, execute(ZERO, ONE));
			log.info("{} XOR {} = expected {} actual {}", ONE, ONE, ZERO, execute(ONE, ONE));
			log.info("{} XOR {} = expected {} actual {}", ZERO, ZERO, ZERO, execute(ZERO, ZERO));
			Assert.assertEquals(ONE, execute(ONE, ZERO), 0.3);
			Assert.assertEquals(ONE, execute(ZERO, ONE), 0.3);
			Assert.assertEquals(ZERO, execute(ONE, ONE), 0.3);
			Assert.assertEquals(ZERO, execute(ZERO, ZERO), 0.3);
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();

			double sum = 0;
			sb.append(" " + ONE + " XOR " + ZERO + " = " + StringUtil.format(execute(ONE, ZERO)));
			sb.append(" " + ZERO + " XOR " + ONE + " = " + StringUtil.format(execute(ZERO, ONE)));
			sb.append(" " + ONE + " XOR " + ONE + " = " + StringUtil.format(execute(ONE, ONE)));
			sb.append(" " + ZERO + " XOR " + ZERO + " = " + StringUtil.format(execute(ZERO, ZERO)));
			sb.append(" " + StringUtil.format(fitness));
			return sb.toString();
		}

		Logger log = LoggerFactory.getLogger(XorPlayer.class);
	}

	private static class XorPlayerFactory implements PlayerFactory {

		@Override
		public XorPlayer newPlayer(Gene gene) {
			XorPlayer p = new XorPlayer();
			p.setGene(gene);
			return p;
		}
	}

	Generation generation;

	Logger log = LoggerFactory.getLogger(XorPlayerTest.class);

	PlayerFactory playerFactory;

	protected Generation perform(Generation generation) {
		generation.getPlayers().forEach(Player::run);
		generation.computeFitnesses();
		return generation.nextGeneration();
	}

	@Test
	public void test10Generation() {
		playerFactory = new XorPlayerFactory();
		generation = Generation.createGen0(playerFactory, 2, 1, 150, new CompatibilityDistanceImpl(2.0, 1, 2));

		List<List<String>> results = new ArrayList<>();

		EvolutionAnalysis analysis = new EvolutionAnalysis();
		for (int i = 0; i < 70; i++) {
			log.info("CUR: " + i);
			Generation nextGen = perform(generation);
			analysis.add(generation);

			List<String> rr = new ArrayList<>();
			results.add(rr);
			generation.getPlayers().forEach(p -> rr.add(p.toString()));

			generation = nextGen;
		}
		log.info(generation.stringify());
		log.info("\n" + analysis.getAnalysis());

		Generation lastGen = analysis.getGenerations().get(analysis.getGenerations().size() - 1);

		XorPlayer bestPlayer = (XorPlayer) lastGen.getPlayers().stream() //
				.sorted(Comparator.comparing(Player::getFitness).reversed()) //
				.findFirst().orElse(null);

		results.forEach(r -> {
			// log.info(">");
			r.forEach(rr -> {
				// log.info(">>> " + rr);
			});
		});

		bestPlayer.assertPerfect();
	}
}
