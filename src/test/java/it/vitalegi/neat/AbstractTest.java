package it.vitalegi.neat;

import it.vitalegi.neat.impl.analysis.EvolutionAnalysis;
import it.vitalegi.neat.impl.analysis.GenerationEntry;
import it.vitalegi.neat.impl.configuration.NeatConfig;
import it.vitalegi.neat.impl.feedforward.FeedForward;
import it.vitalegi.neat.impl.function.CompatibilityDistance;
import it.vitalegi.neat.impl.player.PlayerFactory;
import it.vitalegi.neat.impl.service.FirstGenerationService;
import it.vitalegi.neat.impl.service.GeneMutationService;
import it.vitalegi.neat.impl.service.GeneMutationServiceImpl;
import it.vitalegi.neat.impl.service.GeneService;
import it.vitalegi.neat.impl.service.GenerationService;
import it.vitalegi.neat.impl.service.NextGenerationService;
import it.vitalegi.neat.impl.service.NextGenerationServiceImpl;
import it.vitalegi.neat.impl.service.SpeciesService;
import it.vitalegi.neat.impl.util.ContextUtil;

public abstract class AbstractTest {

	protected CompatibilityDistance compatibilityDistance;
	protected EvolutionAnalysis evolutionAnalysis;
	protected FeedForward feedForward;
	protected FirstGenerationService firstGenerationService;
	protected GeneMutationService geneMutationService;
	protected GeneMutationServiceImpl geneMutationServiceImpl;
	protected GenerationEntry generationEntry;
	protected GenerationService generationService;
	protected GeneService geneService;
	protected NeatConfig neatConfig;
	protected NextGenerationService nextGenerationService;
	protected NextGenerationServiceImpl nextGenerationServiceImpl;
	protected PlayerFactory playerFactory;
	protected SpeciesService speciesService;

	protected void init(ContextUtil context) {
		feedForward = context.getFeedForward();
		generationService = context.getGenerationService();
		speciesService = context.getSpeciesService();
		geneService = context.getGeneService();
		geneMutationService = context.getGeneMutationService();
		if (geneMutationService instanceof GeneMutationServiceImpl) {
			geneMutationServiceImpl = (GeneMutationServiceImpl) geneMutationService;
		}
		compatibilityDistance = context.getCompatibilityDistance();
		playerFactory = context.getPlayerFactory();
		evolutionAnalysis = context.getEvolutionAnalysis();
		generationEntry = context.getGenerationEntry();
		nextGenerationService = context.getNextGenerationService();
		if (nextGenerationService instanceof NextGenerationServiceImpl) {
			nextGenerationServiceImpl = (NextGenerationServiceImpl) nextGenerationService;
		}
		neatConfig = context.getNeatConfig();
		firstGenerationService = context.getFirstGenerationService();
	}

}
