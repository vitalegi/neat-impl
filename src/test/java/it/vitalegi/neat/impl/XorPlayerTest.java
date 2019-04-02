package it.vitalegi.neat.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import it.vitalegi.neat.impl.analysis.EvolutionAnalysis;
import it.vitalegi.neat.impl.function.CompatibilityDistanceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class XorPlayerTest {

	Generation generation;
	PlayerFactory playerFactory;

	Logger log = LoggerFactory.getLogger(XorPlayerTest.class);

	@Before
	public void init() {
		playerFactory = new XorPlayerFactory();
		generation = Generation.createGen0(playerFactory, 2, 1, 10, new CompatibilityDistanceImpl(0.1, 1, 2));
	}

	@Test
	public void test10Generation() {
		EvolutionAnalysis analysis = new EvolutionAnalysis();
		for (int i = 0; i < 10; i++) {
			Generation nextGen = perform(generation);
			analysis.add(generation);
			generation = nextGen;
		}
		log.info(generation.stringify());
		log.info("\n" + analysis.getAnalysis());

	}

	protected Generation perform(Generation generation) {
		generation.getPlayers().forEach(Player::run);
		generation.computeFitnesses();
		return generation.nextGeneration();
	}

	private static class XorPlayerFactory implements PlayerFactory {

		@Override
		public Player newPlayer(Gene gene) {
			XorPlayer p = new XorPlayer();
			p.setGene(gene);
			return p;
		}
	}

	private static class XorPlayer extends Player {

		public void run() {
			fitness = 0;
			double ONE = 1.0;
			double ZERO = 0.0;
			fitness += score(ONE, execute(ONE, ZERO));
			fitness += score(ONE, execute(ZERO, ONE));
			fitness += score(ZERO, execute(ONE, ONE));
			fitness += score(ZERO, execute(ZERO, ZERO));
		}

		private double score(double expected, double actual) {
			double diff = Math.abs(expected - actual);
			// return -diff;
			if (diff < 0.0001) {
				diff = 0.0001;
			}
			return 1 / (diff);
		}

		private double execute(double in1, double in2) {
			return feedForward(new double[] { in1, in2 })[0];
		}
	}
}
