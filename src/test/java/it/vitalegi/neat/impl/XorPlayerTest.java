package it.vitalegi.neat.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import it.vitalegi.neat.AbstractTest;
import it.vitalegi.neat.impl.function.CompatibilityDistance;
import it.vitalegi.neat.impl.function.CompatibilityDistanceImpl;
import it.vitalegi.neat.impl.player.Player;
import it.vitalegi.neat.impl.player.XorPlayer;
import it.vitalegi.neat.impl.player.XorPlayerFactory;
import it.vitalegi.neat.impl.util.ContextUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class XorPlayerTest extends AbstractTest {

	Generation generation;

	Logger log = LoggerFactory.getLogger(XorPlayerTest.class);

	@Before
	public void init() {
	}

	protected Generation perform(Generation generation) {
		generation.getPlayers().stream().map(p -> (XorPlayer) p).forEach(XorPlayer::execute);
		generationService.computeFitnesses(generation);
		return generationService.nextGeneration(generation);
	}

	@Ignore
	@Test
	public void testXor() {
		trainNetwork(150, 100, new double[] { 1, -1 }, 3, 1.4, 1.8, true);
	}

	public void trainNetwork(int generations, int population, double[] biases, double deltaT, double c1, double c2,
			boolean enableLog) {
		init(ContextUtil.builder()//
				.playerFactory(new XorPlayerFactory(biases))//
				.compatibilityDistance(new CompatibilityDistanceImpl(deltaT, c1, c2))//
				.inject());

		generation = generationService.createGen0(playerFactory, 2 + biases.length, 1, 0, population);

		List<Generation> gens = new ArrayList<>();
		for (int i = 0; i < generations; i++) {
			((XorPlayerFactory) playerFactory).setGeneration(i);
			Generation nextGen = perform(generation);
			gens.add(generation);
			generation = nextGen;
		}
		Generation lastGen = gens.get(gens.size() - 1);

		XorPlayer bestPlayer = (XorPlayer) lastGen.getPlayers().stream() //
				.sorted(Comparator.comparing(Player::getFitness).reversed()) //
				.findFirst().orElse(null);

		if (enableLog && log.isDebugEnabled()) {
			log.debug(generationService.stringify(generation));
			evolutionAnalysis.logAnalysis(gens, log);
			log.debug("Networks");
			// analysis.getNetworks(new double[] { 1.0, 1.0 }, biases, log);
		}
		log.info("Best Player {}: ", geneService.stringify(bestPlayer.getGene(), true));
		bestPlayer.assertPerfect();
	}
}
