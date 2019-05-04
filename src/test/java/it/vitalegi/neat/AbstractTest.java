package it.vitalegi.neat;

import it.vitalegi.neat.impl.analysis.EvolutionAnalysis;
import it.vitalegi.neat.impl.analysis.GenerationEntry;
import it.vitalegi.neat.impl.feedforward.FeedForward;
import it.vitalegi.neat.impl.function.CompatibilityDistance;
import it.vitalegi.neat.impl.player.PlayerFactory;
import it.vitalegi.neat.impl.service.GeneServiceImpl;
import it.vitalegi.neat.impl.service.GenerationServiceImpl;
import it.vitalegi.neat.impl.service.SpeciesServiceImpl;
import it.vitalegi.neat.impl.util.ContextUtil;

public abstract class AbstractTest {

	protected CompatibilityDistance compatibilityDistance;
	protected EvolutionAnalysis evolutionAnalysis;
	protected FeedForward feedForward;
	protected GenerationEntry generationEntry;
	protected GenerationServiceImpl generationService;
	protected GeneServiceImpl geneService;
	protected PlayerFactory playerFactory;
	protected SpeciesServiceImpl speciesService;

	protected void init(ContextUtil context) {
		feedForward = context.getFeedForward();
		generationService = context.getGenerationService();
		speciesService = context.getSpeciesService();
		geneService = context.getGeneService();
		compatibilityDistance = context.getCompatibilityDistance();
		playerFactory = context.getPlayerFactory();
		evolutionAnalysis = context.getEvolutionAnalysis();
		generationEntry = context.getGenerationEntry();

	}

}
