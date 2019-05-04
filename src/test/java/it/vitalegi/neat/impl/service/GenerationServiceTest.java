package it.vitalegi.neat.impl.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
import it.vitalegi.neat.impl.feedforward.FeedForward;
import it.vitalegi.neat.impl.function.CompatibilityDistance;
import it.vitalegi.neat.impl.player.DummyPlayer;
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
	public void testGenerationInitializer() {

		Generation gen = generationService.createGen0(playerFactory, 2, 1, 0, 5);
		log.info(generationService.stringify(gen));
		Gene gene2 = geneService.newInstance(generation.getUniqueId(), 2, 1);
		geneService.addConnection(gene2, geneService.getSortedInputNodes(gene2).get(0),
				geneService.getSortedOutputNodes(gene2).get(0), 1, true);
		geneService.addConnection(gene2, geneService.getSortedInputNodes(gene2).get(1),
				geneService.getSortedOutputNodes(gene2).get(0), 1, true);
		generationService.addPlayer(gen, gen.getFactory().newPlayer(gene2));
		log.info(generationService.stringify(gen));
	}

	@Test
	public void testGetCompatibleSpeciesIf1CompatibleSpecies() {
		CompatibilityDistance cd = Mockito.mock(CompatibilityDistance.class);

		init(ContextUtil.builder().compatibilityDistance(cd).inject());

		Gene gene1 = geneService.newInstance(generation.getUniqueId());
		Player player1 = playerFactory.newPlayer(gene1);
		Gene gene2 = geneService.newInstance(generation.getUniqueId());
		Player player2 = playerFactory.newPlayer(gene2);

		Mockito.when(cd.isCompatible(gene1, gene2)).thenReturn(true);
		Mockito.when(cd.isCompatible(gene2, gene1)).thenReturn(true);

		Species species1 = speciesService.newInstance(1, 0);
		generationService.addSpecies(generation, species1);
		generationService.addPlayer(generation, player1, species1);
		Species compatible2 = generationService.getCompatibleSpecies(generation, player2);
		Assert.assertNotNull(compatible2);
		Assert.assertEquals(1, compatible2.getId());
	}

	@Test
	public void testGetCompatibleSpeciesIf2CompatibleSpecies() {
		CompatibilityDistance cd = Mockito.mock(CompatibilityDistance.class);

		init(ContextUtil.builder().compatibilityDistance(cd).inject());

		Gene gene1 = geneService.newInstance(generation.getUniqueId());
		Player player1 = playerFactory.newPlayer(gene1);
		Gene gene2 = geneService.newInstance(generation.getUniqueId());
		Player player2 = playerFactory.newPlayer(gene2);
		Gene gene3 = geneService.newInstance(generation.getUniqueId());
		Player player3 = playerFactory.newPlayer(gene3);

		Mockito.when(cd.isCompatible(gene1, gene2)).thenReturn(false);
		Mockito.when(cd.isCompatible(gene2, gene1)).thenReturn(false);
		Mockito.when(cd.isCompatible(gene1, gene3)).thenReturn(true);
		Mockito.when(cd.isCompatible(gene3, gene1)).thenReturn(true);
		Mockito.when(cd.isCompatible(gene2, gene3)).thenReturn(true);
		Mockito.when(cd.isCompatible(gene3, gene2)).thenReturn(true);

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
		CompatibilityDistance cd = Mockito.mock(CompatibilityDistance.class);

		init(ContextUtil.builder().compatibilityDistance(cd).inject());

		Gene gene1 = geneService.newInstance(generation.getUniqueId());
		Player player1 = playerFactory.newPlayer(gene1);
		Gene gene2 = geneService.newInstance(generation.getUniqueId());
		Player player2 = playerFactory.newPlayer(gene2);

		Mockito.when(cd.isCompatible(gene1, gene2)).thenReturn(false);
		Mockito.when(cd.isCompatible(gene2, gene1)).thenReturn(false);
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

	@Test
	public void testGetRandomPlayerPreserveWeights() {
		Map<Long, List<Player>> players = new HashMap<>();
		players.put(1L, Arrays.asList(new DummyPlayer(feedForward, geneService,
				geneService.newInstance(generation.getUniqueId(), 0, 0, 0, 0), 1)));
		players.put(2L, Arrays.asList(new DummyPlayer(feedForward, geneService,
				geneService.newInstance(generation.getUniqueId(), 1, 0, 0, 0), 99)));

		int[] indexes = new int[2];
		int sampleSize = 100;
		for (int i = 0; i < sampleSize; i++) {
			Player p = generationService.getRandomPlayer(players);
			indexes[(int) p.getGeneId()]++;
		}
		Assert.assertThat(indexes[1], Matchers.greaterThan((int) (0.9 * sampleSize)));
	}

	@Test
	public void testGetSpeciesToPreserveShouldPreserveGrowthSpecies() {

		generation.setGenNumber(16);
		Species species1 = speciesService.newInstance(100, 0);
		species1.addPlayer(playerFactory.newPlayer(geneService.newInstance(generation.getUniqueId(), 1, 0, 0, 0), 10));
		generationService.addSpecies(generation, species1);
		for (int i = 0; i <= 16; i++) {
			species1.addFitness(i);
		}
		Assert.assertEquals(0, speciesService.getFitness(species1, 0), 0.01);
		Assert.assertEquals(1, speciesService.getFitness(species1, 1), 0.01);
		Assert.assertEquals(16, speciesService.getFitness(species1, 16), 0.01);
		Assert.assertTrue(generationService.getSpeciesToPreserve(generation).stream().anyMatch(s -> s.getId() == 100));
	}

	@Test
	public void testGetSpeciesToPreserveShouldPreserveYoungSpecies() {

		generation.setGenNumber(5);
		Species species1 = speciesService.newInstance(100, 6);
		species1.addPlayer(playerFactory.newPlayer(geneService.newInstance(generation.getUniqueId(), 1, 0, 0, 0), 10));
		generationService.addSpecies(generation, species1);
		Assert.assertTrue(generationService.getSpeciesToPreserve(generation).stream().anyMatch(s -> s.getId() == 100));
	}

	@Test
	public void testIsTopScoreSpecies() {

		Generation gen = generationService.createGen0(playerFactory, 2, 1, 0, 5);

		Species s1 = speciesService.newInstance(1, 0);
		s1.getHistoryBestFitnesses().add(100.0);
		generationService.addSpecies(gen, s1);

		Species s2 = speciesService.newInstance(2, 0);
		s2.getHistoryBestFitnesses().add(200.0);
		generationService.addSpecies(gen, s2);

		Species s3 = speciesService.newInstance(3, 0);
		s3.getHistoryBestFitnesses().add(300.0);
		generationService.addSpecies(gen, s3);

		Assert.assertTrue(generationService.isTopScoreSpecies(gen, s2));
		Assert.assertTrue(generationService.isTopScoreSpecies(gen, s3));
		Assert.assertFalse(generationService.isTopScoreSpecies(gen, s1));
	}

	@Test
	public void testIsYoungIf14GenOld() {
		generation.setGenNumber(30);
		Species species = speciesService.newInstance(0, 16);
		generationService.addSpecies(generation, species);
		Assert.assertTrue(generationService.isYoung(generation, species));
	}

	@Test
	public void testIsYoungIf15GenOld() {
		generation.setGenNumber(30);
		Species species = speciesService.newInstance(0, 15);
		generationService.addSpecies(generation, species);
		Assert.assertTrue(generationService.isYoung(generation, species));
	}

	@Test
	public void testIsYoungIf16GenOld() {
		generation.setGenNumber(30);
		Species species = speciesService.newInstance(0, 14);
		generationService.addSpecies(generation, species);
		Assert.assertFalse(generationService.isYoung(generation, species));
	}

	@Test
	public void testNextGeneration() {

		Generation gen = generationService.createGen0(playerFactory, 2, 1, 0, 5);
		log.info(generationService.stringify(gen));
		Generation nextGen = generationService.nextGeneration(gen);
		log.info(generationService.stringify(nextGen));
	}

	@Test
	public void testPreserveSpeciesShouldCopyChampionIfRelevantSpecies() {
		// TODO restore test
		/*
		 * CompatibilityDistance cd = new CompatibilityDistanceImpl(0.1, 1, 2);
		 *
		 * Generation targetGen = new Generation(new UniqueId(), playerFactory, 1);
		 *
		 * Gene gene = Mockito.mock(Gene.class);
		 *
		 * SpeciesServiceImpl speciesServiceMock = new SpeciesServiceImpl() {
		 *
		 * @Override public boolean isRelevantSpecies(Species s) { return true; } };
		 * generationService.speciesService = speciesServiceMock;
		 *
		 * Species oldSpecies = new Species(1, 0);
		 *
		 * generationService.preserveSpecies(targetGen, oldSpecies, gene);
		 *
		 * Mockito.verify(gene,
		 * Mockito.times(0)).mutate(Generation.MUTATE_ADD_NODE_PROBABILITY,
		 * Generation.MUTATE_REMOVE_NODE_PROBABILITY,
		 * Generation.MUTATE_CONNECTION_PROBABILITY,
		 * Generation.MUTATE_ENABLE_PROBABILITY);
		 */
	}

	@Test
	public void testPreserveSpeciesShouldCreateSpecies() {

		Generation targetGen = new Generation(new UniqueId(), playerFactory, 1);

		Gene gene = geneService.newInstance(generation.getUniqueId(), 2, 1);
		geneService.addConnection(gene, geneService.getSortedInputNodes(gene).get(0),
				geneService.getSortedOutputNodes(gene).get(0), 1, true);
		geneService.addConnection(gene, geneService.getSortedInputNodes(gene).get(1),
				geneService.getSortedOutputNodes(gene).get(0), 1, true);

		Species oldSpecies = speciesService.newInstance(1, 0);

		generationService.preserveSpecies(targetGen, oldSpecies, gene);
		Assert.assertEquals(1, targetGen.getSpecies().size());
		Assert.assertEquals(oldSpecies.getId(), targetGen.getSpecies().get(0).getId());

	}

	@Test
	public void testPreserveSpeciesShouldMutateChampionIfIrrelevantSpecies() {
		// TODO restore test
		/*
		 * CompatibilityDistance cd = new CompatibilityDistanceImpl(0.1, 1, 2);
		 *
		 * GenerationServiceImpl generationServiceMock = new GenerationServiceImpl() {
		 *
		 * @Override protected boolean isTopScoreSpecies(Generation gen, Species
		 * species) { return false; }
		 *
		 * }; Generation targetGen = new Generation(new UniqueId(), playerFactory, 1);
		 *
		 * Gene gene = Mockito.mock(Gene.class);
		 *
		 * SpeciesServiceImpl speciesServiceMock = new SpeciesServiceImpl() {
		 *
		 * @Override public boolean isRelevantSpecies(Species s) { return false; } };
		 * generationServiceMock.speciesService = speciesServiceMock;
		 *
		 * Species oldSpecies = new Species(1, 0);
		 *
		 * Mockito.when(gene.clone()).thenReturn(gene);
		 *
		 * generationServiceMock.preserveSpecies(targetGen, oldSpecies, gene);
		 * Mockito.verify(gene,
		 * Mockito.times(1)).mutate(Generation.MUTATE_ADD_NODE_PROBABILITY,
		 * Generation.MUTATE_REMOVE_NODE_PROBABILITY,
		 * Generation.MUTATE_CONNECTION_PROBABILITY,
		 * Generation.MUTATE_ENABLE_PROBABILITY);
		 */
	}
}
