package it.vitalegi.neat.impl;

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

import it.vitalegi.neat.impl.function.CompatibilityDistance;
import it.vitalegi.neat.impl.function.CompatibilityDistanceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class GenerationTest {

	Generation generation;
	DummyPlayerFactory playerFactory;

	@BeforeClass
	public static void initClass() {
		Random.init();
	}

	@Before
	public void init() {
		playerFactory = new DummyPlayerFactory();
		generation = new Generation(playerFactory, 0, null);
	}

	@Test
	public void testIsYoungIf14GenOld() {
		generation.setGenNumber(30);
		Species species = Species.newInstance(0, 16, null);
		generation.addSpecies(species);
		Assert.assertTrue(generation.isYoung(species));
	}

	@Test
	public void testIsYoungIf15GenOld() {
		generation.setGenNumber(30);
		Species species = Species.newInstance(0, 15, null);
		generation.addSpecies(species);
		Assert.assertTrue(generation.isYoung(species));
	}

	@Test
	public void testIsYoungIf16GenOld() {
		generation.setGenNumber(30);
		Species species = Species.newInstance(0, 14, null);
		generation.addSpecies(species);
		Assert.assertFalse(generation.isYoung(species));
	}

	@Test
	public void testComputeFitnesses() {
		Species species1 = Species.newInstance(0, 14, null);
		species1.addPlayer(playerFactory.newPlayer(Gene.newInstance(generation.getUniqueId(), 1, 0, 0), 10));
		species1.addPlayer(playerFactory.newPlayer(Gene.newInstance(generation.getUniqueId(), 1, 0, 0), 12));
		species1.addPlayer(playerFactory.newPlayer(Gene.newInstance(generation.getUniqueId(), 1, 0, 0), 11));

		generation.addSpecies(species1);

		generation.computeFitnesses();

		Assert.assertEquals(1, species1.getHistoryBestFitnesses().size());
		Assert.assertEquals(12, species1.getLastFitness(), 0.1);

	}

	@Test
	public void testGetSpeciesToPreserveShouldPreserveYoungSpecies() {

		generation.setGenNumber(5);
		Species species1 = Species.newInstance(100, 6, null);
		species1.addPlayer(playerFactory.newPlayer(Gene.newInstance(generation.getUniqueId(), 1, 0, 0), 10));
		generation.addSpecies(species1);
		Assert.assertTrue(generation.getSpeciesToPreserve().stream().anyMatch(s -> s.getId() == 100));
	}

	@Test
	public void testGetSpeciesToPreserveShouldPreserveGrowthSpecies() {

		generation.setGenNumber(16);
		Species species1 = Species.newInstance(100, 0, null);
		species1.addPlayer(playerFactory.newPlayer(Gene.newInstance(generation.getUniqueId(), 1, 0, 0), 10));
		generation.addSpecies(species1);
		for (int i = 0; i <= 16; i++) {
			species1.addFitness(i);
		}
		Assert.assertEquals(0, species1.getFitness(0), 0.01);
		Assert.assertEquals(1, species1.getFitness(1), 0.01);
		Assert.assertEquals(16, species1.getFitness(16), 0.01);
		Assert.assertTrue(generation.getSpeciesToPreserve().stream().anyMatch(s -> s.getId() == 100));
	}

	@Test
	public void testGetCompatibleSpeciesIfNoSpecies() {
		CompatibilityDistance cd = Mockito.mock(CompatibilityDistance.class);
		generation.setCompatibilityDistance(cd);
		Gene gene1 = Gene.newInstance(generation.getUniqueId());
		Player player1 = playerFactory.newPlayer(gene1);

		Assert.assertNull(generation.getCompatibleSpecies(player1));
	}

	@Test
	public void testGetCompatibleSpeciesIfNoCompatibleSpecies() {
		CompatibilityDistance cd = Mockito.mock(CompatibilityDistance.class);
		generation.setCompatibilityDistance(cd);
		Gene gene1 = Gene.newInstance(generation.getUniqueId());
		Player player1 = playerFactory.newPlayer(gene1);
		Gene gene2 = Gene.newInstance(generation.getUniqueId());
		Player player2 = playerFactory.newPlayer(gene2);

		Mockito.when(cd.isCompatible(gene1, gene2)).thenReturn(false);
		Mockito.when(cd.isCompatible(gene2, gene1)).thenReturn(false);
		Species species1 = Species.newInstance(1, 0, cd);
		generation.addSpecies(species1);
		generation.addPlayer(player1, species1);
		Assert.assertNull(generation.getCompatibleSpecies(player2));
	}

	@Test
	public void testGetCompatibleSpeciesIf1CompatibleSpecies() {
		CompatibilityDistance cd = Mockito.mock(CompatibilityDistance.class);
		generation.setCompatibilityDistance(cd);
		Gene gene1 = Gene.newInstance(generation.getUniqueId());
		Player player1 = playerFactory.newPlayer(gene1);
		Gene gene2 = Gene.newInstance(generation.getUniqueId());
		Player player2 = playerFactory.newPlayer(gene2);

		Mockito.when(cd.isCompatible(gene1, gene2)).thenReturn(true);
		Mockito.when(cd.isCompatible(gene2, gene1)).thenReturn(true);

		Species species1 = Species.newInstance(1, 0, cd);
		generation.addSpecies(species1);
		generation.addPlayer(player1, species1);
		Species compatible2 = generation.getCompatibleSpecies(player2);
		Assert.assertNotNull(compatible2);
		Assert.assertEquals(1, compatible2.getId());
	}

	@Test
	public void testGetCompatibleSpeciesIf2CompatibleSpecies() {
		CompatibilityDistance cd = Mockito.mock(CompatibilityDistance.class);
		generation.setCompatibilityDistance(cd);
		Gene gene1 = Gene.newInstance(generation.getUniqueId());
		Player player1 = playerFactory.newPlayer(gene1);
		Gene gene2 = Gene.newInstance(generation.getUniqueId());
		Player player2 = playerFactory.newPlayer(gene2);
		Gene gene3 = Gene.newInstance(generation.getUniqueId());
		Player player3 = playerFactory.newPlayer(gene3);

		Mockito.when(cd.isCompatible(gene1, gene2)).thenReturn(false);
		Mockito.when(cd.isCompatible(gene2, gene1)).thenReturn(false);
		Mockito.when(cd.isCompatible(gene1, gene3)).thenReturn(true);
		Mockito.when(cd.isCompatible(gene3, gene1)).thenReturn(true);
		Mockito.when(cd.isCompatible(gene2, gene3)).thenReturn(true);
		Mockito.when(cd.isCompatible(gene3, gene2)).thenReturn(true);

		Species species1 = Species.newInstance(1, 0, cd);
		generation.addSpecies(species1);
		generation.addPlayer(player1, species1);
		Species species2 = Species.newInstance(2, 0, cd);
		generation.addSpecies(species1);
		generation.addPlayer(player2, species2);
		Species compatible3 = generation.getCompatibleSpecies(player3);
		Assert.assertNotNull(compatible3);
		Assert.assertEquals(1, compatible3.getId());
	}

	@Test
	public void testPreserveSpeciesShouldCreateSpecies() {
		CompatibilityDistance cd = new CompatibilityDistanceImpl(0.1, 1, 2);

		Generation targetGen = new Generation(playerFactory, 1, cd);

		Gene gene = Gene.newInstance(generation.getUniqueId(), 2, 1);
		gene.addConnection(gene.getSortedInputNodes().get(0), gene.getSortedOutputNodes().get(0), 1, true);
		gene.addConnection(gene.getSortedInputNodes().get(1), gene.getSortedOutputNodes().get(0), 1, true);

		Species oldSpecies = Species.newInstance(1, 0, cd);

		targetGen.preserveSpecies(oldSpecies, gene);
		Assert.assertEquals(1, targetGen.getSpecies().size());
		Assert.assertEquals(oldSpecies.getId(), targetGen.getSpecies().get(0).getId());

	}

	@Test
	public void testPreserveSpeciesShouldCopyChampionIfRelevantSpecies() {
		CompatibilityDistance cd = new CompatibilityDistanceImpl(0.1, 1, 2);

		Generation targetGen = new Generation(playerFactory, 1, cd);

		Gene gene = Mockito.mock(Gene.class);

		Species oldSpecies = new Species(1, 0, cd) {
			public boolean isRelevantSpecies() {
				return true;
			}
		};

		targetGen.preserveSpecies(oldSpecies, gene);
		Mockito.verify(gene, Mockito.times(0)).mutate(Generation.MUTATE_NODE_PROBABILITY,
				Generation.MUTATE_CONNECTION_PROBABILITY, Generation.MUTATE_ENABLE_PROBABILITY);
	}

	@Test
	public void testPreserveSpeciesShouldMutateChampionIfIrrelevantSpecies() {
		CompatibilityDistance cd = new CompatibilityDistanceImpl(0.1, 1, 2);

		Generation targetGen = new Generation(playerFactory, 1, cd);

		Gene gene = Mockito.mock(Gene.class);

		Species oldSpecies = new Species(1, 0, cd) {
			public boolean isRelevantSpecies() {
				return false;
			}
		};

		targetGen.preserveSpecies(oldSpecies, gene);
		Mockito.verify(gene, Mockito.times(1)).mutate(Generation.MUTATE_NODE_PROBABILITY,
				Generation.MUTATE_CONNECTION_PROBABILITY, Generation.MUTATE_ENABLE_PROBABILITY);
	}

	@Test
	public void testGetRandomPlayerPreserveWeights() {
		Map<Long, List<Player>> players = new HashMap<>();
		players.put(1L, Arrays.asList(Player.newPlayer(Gene.newInstance(generation.getUniqueId(), 0, 0, 0), 1)));
		players.put(2L, Arrays.asList(Player.newPlayer(Gene.newInstance(generation.getUniqueId(), 1, 0, 0), 99)));

		int[] indexes = new int[2];
		int sampleSize = 100;
		for (int i = 0; i < sampleSize; i++) {
			Player p = generation.getRandomPlayer(players);
			indexes[(int) p.getGeneId()]++;
		}
		Assert.assertThat(indexes[1], Matchers.greaterThan((int) (0.9 * sampleSize)));
	}

	@Test
	public void testGenerationInitializer() {
		Generation gen = Generation.createGen0(playerFactory, 2, 1, 5, new CompatibilityDistanceImpl(0.1, 1, 2));
		log.info(gen.stringify());
		Gene gene2 = Gene.newInstance(generation.getUniqueId(), 2, 1);
		gene2.addConnection(gene2.getSortedInputNodes().get(0), gene2.getSortedOutputNodes().get(0), 1, true);
		gene2.addConnection(gene2.getSortedInputNodes().get(1), gene2.getSortedOutputNodes().get(0), 1, true);
		gen.addPlayer(playerFactory.newPlayer(gene2));
		log.info(gen.stringify());
	}

	@Test
	public void testIsTopScoreSpecies() {
		CompatibilityDistance cd = new CompatibilityDistanceImpl(0.1, 1, 2);
		Generation gen = Generation.createGen0(playerFactory, 2, 1, 5, cd);

		Species s1 = Species.newInstance(1, 0, cd);
		s1.getHistoryBestFitnesses().add(100.0);
		gen.addSpecies(s1);

		Species s2 = Species.newInstance(2, 0, cd);
		s2.getHistoryBestFitnesses().add(200.0);
		gen.addSpecies(s2);

		Species s3 = Species.newInstance(3, 0, cd);
		s3.getHistoryBestFitnesses().add(300.0);
		gen.addSpecies(s3);

		Assert.assertTrue(gen.isTopScoreSpecies(s2));
		Assert.assertTrue(gen.isTopScoreSpecies(s3));
		Assert.assertFalse(gen.isTopScoreSpecies(s1));
	}

	@Test
	public void testNextGeneration() {
		Generation gen = Generation.createGen0(playerFactory, 2, 1, 5, new CompatibilityDistanceImpl(0.1, 1, 2));
		log.info(gen.stringify());
		Generation nextGen = gen.nextGeneration();
		log.info(nextGen.stringify());
	}

	Logger log = LoggerFactory.getLogger(GenerationTest.class);
}
