package it.vitalegi.neat.impl.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import it.vitalegi.neat.AbstractTest;
import it.vitalegi.neat.impl.Random;
import it.vitalegi.neat.impl.Species;
import it.vitalegi.neat.impl.UniqueId;
import it.vitalegi.neat.impl.player.DummyPlayer;
import it.vitalegi.neat.impl.player.DummyPlayerFactory;
import it.vitalegi.neat.impl.player.Player;
import it.vitalegi.neat.impl.util.ContextUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class SpeciesServiceTest extends AbstractTest {

	@BeforeClass
	public static void initClass() {
		Random.init();
	}

	private DummyPlayerFactory playerFactory;

	private UniqueId uniqueId;

	@Test
	public void getBestPlayers() {
		Species species = speciesService.newInstance(0, 6);
		species.addPlayer(new DummyPlayer(feedForward, geneService, geneService.newInstance(uniqueId), 5));
		species.addPlayer(new DummyPlayer(feedForward, geneService, geneService.newInstance(uniqueId), 10));
		species.addPlayer(new DummyPlayer(feedForward, geneService, geneService.newInstance(uniqueId), 1));

		List<Player> best = speciesService.getBestPlayers(species, 1);

		Assert.assertEquals(1, best.size());
		Assert.assertEquals(10, best.get(0).getFitness(), 0.1);
	}

	@Before
	public void init() {
		playerFactory = new DummyPlayerFactory();
		uniqueId = new UniqueId();
		init(ContextUtil.builder().inject());
	}

	@Test
	public void testGetChampion() {
		playerFactory.setFitness(5);
		Species species = speciesService.newInstance(0, 6);
		species.addPlayer(playerFactory.newPlayer(geneService.newInstance(uniqueId, 1, 0, 0, 0)));
		playerFactory.setFitness(6);
		species.addPlayer(playerFactory.newPlayer(geneService.newInstance(uniqueId, 2, 0, 0, 0)));
		Assert.assertEquals(2, speciesService.getChampion(species).getGene().getId());
	}

	@Test
	public void testGetFitness() {
		Species species = speciesService.newInstance(0, 6);
		species.addFitness(1);
		species.addFitness(2);
		species.addFitness(3);
		species.addFitness(4);
		Assert.assertEquals(1, speciesService.getFitness(species, 6), 0.001);
		Assert.assertEquals(2, speciesService.getFitness(species, 7), 0.001);
		Assert.assertEquals(3, speciesService.getFitness(species, 8), 0.001);
		Assert.assertEquals(4, speciesService.getFitness(species, 9), 0.001);
	}

	@Test
	public void testGetLastFitness() {
		Species species = speciesService.newInstance(0, 6);
		species.addFitness(1);
		species.addFitness(2);
		Assert.assertEquals(2, species.getLastFitness(), 0.001);
	}
}
