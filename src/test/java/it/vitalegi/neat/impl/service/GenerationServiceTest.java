package it.vitalegi.neat.impl.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import it.vitalegi.neat.impl.Generation;
import it.vitalegi.neat.impl.Random;
import it.vitalegi.neat.impl.Species;
import it.vitalegi.neat.impl.UniqueId;
import it.vitalegi.neat.impl.function.CompatibilityDistance;
import it.vitalegi.neat.impl.player.DummyPlayerFactory;
import it.vitalegi.neat.impl.player.Player;
import it.vitalegi.neat.impl.util.ContextUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class GenerationServiceTest extends AbstractTest {

	@BeforeClass
	public static void initClass() {
		Random.init();
	}

	Generation generation;

	Logger log = LoggerFactory.getLogger(GenerationServiceTest.class);

	DummyPlayerFactory playerFactory;

	@Before
	public void init() {
		init(ContextUtil.builder().inject());

		playerFactory = new DummyPlayerFactory();
		generation = new Generation(new UniqueId(), playerFactory, 0);
	}

	@Test
	public void testComputeFitnesses() {
		Species species1 = speciesService.newInstance(0, 14);
		species1.addPlayer(playerFactory.newPlayer(geneService.newInstance(generation.getUniqueId(), 1, 0, 0, 0), 10));
		species1.addPlayer(playerFactory.newPlayer(geneService.newInstance(generation.getUniqueId(), 1, 0, 0, 0), 12));
		species1.addPlayer(playerFactory.newPlayer(geneService.newInstance(generation.getUniqueId(), 1, 0, 0, 0), 11));

		generationService.addSpecies(generation, species1);

		generationService.computeFitnesses(generation);

		Assert.assertEquals(1, species1.getHistoryBestFitnesses().size());
		Assert.assertEquals(12, species1.getLastFitness(), 0.1);

	}

	@Test
	public void testGetCompatibleSpeciesIf1CompatibleSpecies() {
		CompatibilityDistance cd = mock(CompatibilityDistance.class);

		init(ContextUtil.builder().compatibilityDistance(cd).inject());

		Gene gene1 = geneService.newInstance(generation.getUniqueId());
		Player player1 = playerFactory.newPlayer(gene1);
		Gene gene2 = geneService.newInstance(generation.getUniqueId());
		Player player2 = playerFactory.newPlayer(gene2);

		when(cd.isCompatible(gene1, gene2)).thenReturn(true);
		when(cd.isCompatible(gene2, gene1)).thenReturn(true);

		Species species1 = speciesService.newInstance(1, 0);
		generationService.addSpecies(generation, species1);
		generationService.addPlayer(generation, player1, species1);
		Species compatible2 = generationService.getCompatibleSpecies(generation, player2);
		Assert.assertNotNull(compatible2);
		Assert.assertEquals(1, compatible2.getId());
	}

	@Test
	public void testGetCompatibleSpeciesIf2CompatibleSpecies() {
		CompatibilityDistance cd = mock(CompatibilityDistance.class);

		init(ContextUtil.builder().compatibilityDistance(cd).inject());

		Gene gene1 = geneService.newInstance(generation.getUniqueId());
		Player player1 = playerFactory.newPlayer(gene1);
		Gene gene2 = geneService.newInstance(generation.getUniqueId());
		Player player2 = playerFactory.newPlayer(gene2);
		Gene gene3 = geneService.newInstance(generation.getUniqueId());
		Player player3 = playerFactory.newPlayer(gene3);

		when(cd.isCompatible(gene1, gene2)).thenReturn(false);
		when(cd.isCompatible(gene2, gene1)).thenReturn(false);
		when(cd.isCompatible(gene1, gene3)).thenReturn(true);
		when(cd.isCompatible(gene3, gene1)).thenReturn(true);
		when(cd.isCompatible(gene2, gene3)).thenReturn(true);
		when(cd.isCompatible(gene3, gene2)).thenReturn(true);

		Species species1 = speciesService.newInstance(1, 0);
		generationService.addSpecies(generation, species1);
		generationService.addPlayer(generation, player1, species1);
		Species species2 = speciesService.newInstance(2, 0);
		generationService.addSpecies(generation, species1);
		generationService.addPlayer(generation, player2, species2);
		Species compatible3 = generationService.getCompatibleSpecies(generation, player3);
		Assert.assertNotNull(compatible3);
		Assert.assertEquals(1, compatible3.getId());
	}

	@Test
	public void testGetCompatibleSpeciesIfNoCompatibleSpecies() {
		CompatibilityDistance cd = mock(CompatibilityDistance.class);

		init(ContextUtil.builder().compatibilityDistance(cd).inject());

		Gene gene1 = geneService.newInstance(generation.getUniqueId());
		Player player1 = playerFactory.newPlayer(gene1);
		Gene gene2 = geneService.newInstance(generation.getUniqueId());
		Player player2 = playerFactory.newPlayer(gene2);

		when(cd.isCompatible(gene1, gene2)).thenReturn(false);
		when(cd.isCompatible(gene2, gene1)).thenReturn(false);
		Species species1 = speciesService.newInstance(1, 0);
		generationService.addSpecies(generation, species1);
		generationService.addPlayer(generation, player1, species1);
		Assert.assertNull(generationService.getCompatibleSpecies(generation, player2));
	}

	@Test
	public void testGetCompatibleSpeciesIfNoSpecies() {
		Gene gene1 = geneService.newInstance(generation.getUniqueId());
		Player player1 = playerFactory.newPlayer(gene1);

		Assert.assertNull(generationService.getCompatibleSpecies(generation, player1));
	}

}
