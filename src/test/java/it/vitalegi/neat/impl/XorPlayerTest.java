package it.vitalegi.neat.impl;

import java.util.Comparator;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import it.vitalegi.neat.impl.analysis.EvolutionAnalysis;
import it.vitalegi.neat.impl.function.CompatibilityDistance;
import it.vitalegi.neat.impl.function.CompatibilityDistanceImpl;
import it.vitalegi.neat.impl.player.Player;
import it.vitalegi.neat.impl.player.PlayerFactory;
import it.vitalegi.neat.impl.player.XorPlayer;
import it.vitalegi.neat.impl.player.XorPlayerFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class XorPlayerTest {

	Generation generation;

	Logger log = LoggerFactory.getLogger(XorPlayerTest.class);

	PlayerFactory playerFactory;

	protected Generation perform(Generation generation) {
		generation.getPlayers().stream().map(p -> (XorPlayer) p).forEach(XorPlayer::execute);
		generation.computeFitnesses();
		return generation.nextGeneration();
	}

	@Ignore
	@Test
	public void testXor() {
		trainNetwork(150, 100, new double[] { 1, -1 }, 3, 1.4, 1.8, true);
	}

	public void trainNetwork(int generations, int population, double[] biases, double deltaT, double c1, double c2,
			boolean enableLog) {
		playerFactory = new XorPlayerFactory(biases);
		CompatibilityDistance cd = new CompatibilityDistanceImpl(deltaT, c1, c2);
		generation = Generation.createGen0(playerFactory, 2 + biases.length, 1, 0, population, cd);

		EvolutionAnalysis analysis = new EvolutionAnalysis();
		for (int i = 0; i < generations; i++) {
			((XorPlayerFactory) playerFactory).setGeneration(i);
			Generation nextGen = perform(generation);
			analysis.add(generation);
			generation = nextGen;
		}
		Generation lastGen = analysis.getGenerations().get(analysis.getGenerations().size() - 1);

		XorPlayer bestPlayer = (XorPlayer) lastGen.getPlayers().stream() //
				.sorted(Comparator.comparing(Player::getFitness).reversed()) //
				.findFirst().orElse(null);

		if (enableLog && log.isDebugEnabled()) {
			log.debug(generation.stringify());
			analysis.logAnalysis(log);
			log.debug("Networks");
			// analysis.getNetworks(new double[] { 1.0, 1.0 }, biases, log);
		}
		log.info("Best Player {}: ", bestPlayer.getGene().stringify(true));
		bestPlayer.assertPerfect();
	}
}
