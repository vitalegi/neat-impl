package it.vitalegi.neat.impl.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import it.vitalegi.neat.impl.configuration.NeatConfig;
import it.vitalegi.neat.impl.function.CompatibilityDistanceImpl;
import it.vitalegi.neat.impl.player.DummyPlayer;
import it.vitalegi.neat.impl.player.DummyPlayerFactory;
import it.vitalegi.neat.impl.player.Player;
import it.vitalegi.neat.impl.util.ContextUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class NextGenerationServiceTest extends AbstractTest {

	@BeforeClass
	public static void initClass() {
		Random.init();
	}

	Generation generation;

	Logger log = LoggerFactory.getLogger(NextGenerationServiceTest.class);

	DummyPlayerFactory playerFactory;

	@Before
	public void init() {
		init(ContextUtil.builder().inject());

		playerFactory = new DummyPlayerFactory();
		generation = new Generation(new UniqueId(), playerFactory, 0);
	}

	@Test
	public void testGenerationInitializer() {

		Generation gen = firstGenerationService.create(neatConfig, playerFactory, 2, 1, 0, 5);
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
	public void testGetRandomPlayerPreserveWeights() {
		Map<Long, List<Player>> players = new HashMap<>();
		players.put(1L, Arrays.asList(new DummyPlayer(feedForward, geneService,
				geneService.newInstance(generation.getUniqueId(), 0, 0, 0, 0), 1)));
		players.put(2L, Arrays.asList(new DummyPlayer(feedForward, geneService,
				geneService.newInstance(generation.getUniqueId(), 1, 0, 0, 0), 99)));

		int[] indexes = new int[2];
		int sampleSize = 100;
		for (int i = 0; i < sampleSize; i++) {
			Player p = nextGenerationServiceImpl.getRandomPlayer(players);
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
		Assert.assertTrue(nextGenerationServiceImpl.getSpeciesToPreserve(neatConfig, generation).stream()
				.anyMatch(s -> s.getId() == 100));
	}

	@Test
	public void testGetSpeciesToPreserveShouldPreserveYoungSpecies() {

		generation.setGenNumber(5);
		Species species1 = speciesService.newInstance(100, 6);
		species1.addPlayer(playerFactory.newPlayer(geneService.newInstance(generation.getUniqueId(), 1, 0, 0, 0), 10));
		generationService.addSpecies(generation, species1);
		Assert.assertTrue(nextGenerationServiceImpl.getSpeciesToPreserve(neatConfig, generation).stream()
				.anyMatch(s -> s.getId() == 100));
	}

	@Test
	public void testIsTopScoreSpecies() {

		Generation gen = firstGenerationService.create(neatConfig, playerFactory, 2, 1, 0, 5);

		Species s1 = speciesService.newInstance(1, 0);
		s1.getHistoryBestFitnesses().add(100.0);
		generationService.addSpecies(gen, s1);

		Species s2 = speciesService.newInstance(2, 0);
		s2.getHistoryBestFitnesses().add(200.0);
		generationService.addSpecies(gen, s2);

		Species s3 = speciesService.newInstance(3, 0);
		s3.getHistoryBestFitnesses().add(300.0);
		generationService.addSpecies(gen, s3);

		Assert.assertTrue(nextGenerationServiceImpl.isTopScoreSpecies(neatConfig, gen, s2));
		Assert.assertTrue(nextGenerationServiceImpl.isTopScoreSpecies(neatConfig, gen, s3));
		Assert.assertFalse(nextGenerationServiceImpl.isTopScoreSpecies(neatConfig, gen, s1));
	}

	@Test
	public void testIsYoungIf14GenOld() {
		generation.setGenNumber(30);
		Species species = speciesService.newInstance(0, 16);
		generationService.addSpecies(generation, species);
		Assert.assertTrue(nextGenerationServiceImpl.isYoung(neatConfig, generation, species));
	}

	@Test
	public void testIsYoungIf15GenOld() {
		generation.setGenNumber(30);
		Species species = speciesService.newInstance(0, 15);
		generationService.addSpecies(generation, species);
		Assert.assertTrue(nextGenerationServiceImpl.isYoung(neatConfig, generation, species));
	}

	@Test
	public void testIsYoungIf16GenOld() {
		generation.setGenNumber(30);
		Species species = speciesService.newInstance(0, 14);
		generationService.addSpecies(generation, species);
		Assert.assertFalse(nextGenerationServiceImpl.isYoung(neatConfig, generation, species));
	}

	@Test
	public void testNextGeneration() {

		Generation gen = firstGenerationService.create(neatConfig, playerFactory, 2, 1, 0, 5);
		log.info(generationService.stringify(gen));
		Generation nextGen = nextGenerationServiceImpl.nextGeneration(neatConfig, gen);
		log.info(generationService.stringify(nextGen));
	}

	@Test
	public void testPreserveSpeciesShouldCopyChampionIfRelevantSpecies() {

		GeneMutationService gm = mock(GeneMutationService.class);

		init(ContextUtil.builder()//
				.compatibilityDistance(new CompatibilityDistanceImpl(0.1, 1, 2))//
				.speciesService(new SpeciesServiceImpl() {
					@Override
					public boolean isRelevantSpecies(NeatConfig neatConfig, Species s) {
						return true;
					}
				})//
				.geneMutationService(gm)//
				.inject());

		Generation targetGen = new Generation(new UniqueId(), playerFactory, 1);

		Gene gene = new Gene(targetGen.getUniqueId(), targetGen.getUniqueId().nextGeneId());

		Species oldSpecies = new Species(1, 0);

		nextGenerationServiceImpl.preserveSpecies(neatConfig, targetGen, oldSpecies, gene);

		verify(geneMutationService, times(0))//
				.mutate(any(NeatConfig.class), any(Gene.class));
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

		nextGenerationServiceImpl.preserveSpecies(neatConfig, targetGen, oldSpecies, gene);
		Assert.assertEquals(1, targetGen.getSpecies().size());
		Assert.assertEquals(oldSpecies.getId(), targetGen.getSpecies().get(0).getId());

	}

	@Test
	public void testPreserveSpeciesShouldMutateChampionIfIrrelevantSpecies() {

		GeneMutationService gm = mock(GeneMutationService.class);

		init(ContextUtil.builder()//
				.compatibilityDistance(new CompatibilityDistanceImpl(0.1, 1, 2))//
				.speciesService(new SpeciesServiceImpl() {
					@Override
					public boolean isRelevantSpecies(NeatConfig neatConfig, Species s) {
						return false;
					}
				})//
				.nextGenerationService(new NextGenerationServiceImpl() {
					@Override
					protected boolean isTopScoreSpecies(NeatConfig neatConfig, Generation gen, Species species) {
						return false;
					}
				}).geneMutationService(gm)//
				.inject());

		Generation targetGen = new Generation(new UniqueId(), playerFactory, 1);

		Gene gene = new Gene(targetGen.getUniqueId(), targetGen.getUniqueId().nextGeneId());

		Species oldSpecies = new Species(1, 0);

		nextGenerationServiceImpl.preserveSpecies(neatConfig, targetGen, oldSpecies, gene);

		verify(geneMutationService, times(1))//
				.mutate(any(NeatConfig.class), any(Gene.class));
	}
}
