package it.vitalegi.neat.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class SpeciesTest {

	private DummyPlayerFactory playerFactory;
	private UniqueId uniqueId;

	@BeforeClass
	public static void initClass() {
		Random.init();
	}

	@Before
	public void init() {
		playerFactory = new DummyPlayerFactory();
		uniqueId = new UniqueId();
	}

	@Test
	public void testGetFitness() {
		Species species = Species.newInstance(0, 6, null);
		species.addFitness(1);
		species.addFitness(2);
		species.addFitness(3);
		species.addFitness(4);
		Assert.assertEquals(1, species.getFitness(6), 0.001);
		Assert.assertEquals(2, species.getFitness(7), 0.001);
		Assert.assertEquals(3, species.getFitness(8), 0.001);
		Assert.assertEquals(4, species.getFitness(9), 0.001);
	}

	@Test
	public void testGetLastFitness() {
		Species species = Species.newInstance(0, 6, null);
		species.addFitness(1);
		species.addFitness(2);
		Assert.assertEquals(2, species.getLastFitness(), 0.001);
	}

	@Test
	public void testGetChampion() {
		playerFactory.setFitness(5);
		Species species = Species.newInstance(0, 6, null);
		species.addPlayer(playerFactory.newPlayer(Gene.newInstance(uniqueId, 1, 0, 0)));
		playerFactory.setFitness(6);
		species.addPlayer(playerFactory.newPlayer(Gene.newInstance(uniqueId, 2, 0, 0)));
		Assert.assertEquals(2, species.getChampion().getGene().getId());
	}

	@Test
	public void getBestPlayers() {
		Species species = Species.newInstance(0, 6, null);
		species.addPlayer(Player.newPlayer(Gene.newInstance(uniqueId), 5));
		species.addPlayer(Player.newPlayer(Gene.newInstance(uniqueId), 10));
		species.addPlayer(Player.newPlayer(Gene.newInstance(uniqueId), 1));

		List<Player> best = species.getBestPlayers(species, 1);

		Assert.assertEquals(1, best.size());
		Assert.assertEquals(10, best.get(0).getFitness(), 0.1);
	}
}
