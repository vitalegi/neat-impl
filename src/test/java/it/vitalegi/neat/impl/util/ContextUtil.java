package it.vitalegi.neat.impl.util;

import it.vitalegi.neat.impl.analysis.EvolutionAnalysis;
import it.vitalegi.neat.impl.analysis.GenerationEntry;
import it.vitalegi.neat.impl.feedforward.FeedForward;
import it.vitalegi.neat.impl.feedforward.FeedForwardImpl;
import it.vitalegi.neat.impl.function.CompatibilityDistance;
import it.vitalegi.neat.impl.function.CompatibilityDistanceImpl;
import it.vitalegi.neat.impl.player.PlayerFactory;
import it.vitalegi.neat.impl.player.XorPlayerFactory;
import it.vitalegi.neat.impl.service.GeneServiceImpl;
import it.vitalegi.neat.impl.service.GenerationServiceImpl;
import it.vitalegi.neat.impl.service.SpeciesServiceImpl;

public class ContextUtil {

	FeedForward feedForward;
	GenerationServiceImpl generationService;
	SpeciesServiceImpl speciesService;
	GeneServiceImpl geneService;
	CompatibilityDistance compatibilityDistance;
	EvolutionAnalysis evolutionAnalysis;
	PlayerFactory playerFactory;
	GenerationEntry generationEntry;

	public static ContextUtil builder() {
		ContextUtil context = new ContextUtil();

		context.feedForward = new FeedForwardImpl();
		context.generationService = new GenerationServiceImpl();
		context.speciesService = new SpeciesServiceImpl();
		context.geneService = new GeneServiceImpl();
		context.compatibilityDistance = new CompatibilityDistanceImpl(0.1, 1, 2);
		context.evolutionAnalysis = new EvolutionAnalysis();
		context.generationEntry = new GenerationEntry();

		return context;
	}

	public ContextUtil feedForward(FeedForward feedForward) {
		this.feedForward = feedForward;
		return this;
	};

	public ContextUtil generationService(GenerationServiceImpl generationService) {
		this.generationService = generationService;
		return this;
	};

	public ContextUtil speciesService(SpeciesServiceImpl speciesService) {
		this.speciesService = speciesService;
		return this;
	};

	public ContextUtil geneService(GeneServiceImpl geneService) {
		this.geneService = geneService;
		return this;
	};

	public ContextUtil compatibilityDistance(CompatibilityDistance compatibilityDistance) {
		this.compatibilityDistance = compatibilityDistance;
		return this;
	};

	public ContextUtil evolutionAnalysis(EvolutionAnalysis evolutionAnalysis) {
		this.evolutionAnalysis = evolutionAnalysis;
		return this;
	};

	public ContextUtil playerFactory(PlayerFactory playerFactory) {
		this.playerFactory = playerFactory;
		return this;
	};

	public ContextUtil generationEntry(GenerationEntry generationEntry) {
		this.generationEntry = generationEntry;
		return this;
	}

	public ContextUtil inject() {
		if (feedForward instanceof FeedForwardImpl) {
			((FeedForwardImpl) feedForward).setGeneService(geneService);
		}

		if (generationService instanceof GenerationServiceImpl) {
			((GenerationServiceImpl) generationService).setGeneService(geneService);
			((GenerationServiceImpl) generationService).setSpeciesService(speciesService);
		}

		if (speciesService instanceof SpeciesServiceImpl) {
			((SpeciesServiceImpl) speciesService).setCompatibilityDistance(compatibilityDistance);
		}

		if (geneService instanceof GeneServiceImpl) {
		}
		if (compatibilityDistance instanceof CompatibilityDistanceImpl) {
			((CompatibilityDistanceImpl) compatibilityDistance).setGeneService(geneService);
		}
		if (evolutionAnalysis instanceof EvolutionAnalysis) {
			((EvolutionAnalysis) evolutionAnalysis).setGenerationEntry(generationEntry);
			((EvolutionAnalysis) evolutionAnalysis).setGeneService(geneService);
		}
		if (playerFactory != null && playerFactory instanceof XorPlayerFactory) {
			((XorPlayerFactory) playerFactory).setFeedForward(feedForward);
			((XorPlayerFactory) playerFactory).setGeneService(geneService);
		}
		if (generationEntry instanceof GenerationEntry) {
			((GenerationEntry) generationEntry).setGeneService(geneService);
			((GenerationEntry) generationEntry).setCompatibilityDistance(compatibilityDistance);
		}
		return this;
	}

	public FeedForward getFeedForward() {
		return feedForward;
	}

	public GenerationServiceImpl getGenerationService() {
		return generationService;
	}

	public SpeciesServiceImpl getSpeciesService() {
		return speciesService;
	}

	public GeneServiceImpl getGeneService() {
		return geneService;
	}

	public CompatibilityDistance getCompatibilityDistance() {
		return compatibilityDistance;
	}

	public EvolutionAnalysis getEvolutionAnalysis() {
		return evolutionAnalysis;
	}

	public PlayerFactory getPlayerFactory() {
		return playerFactory;
	}

	public GenerationEntry getGenerationEntry() {
		return generationEntry;
	}

}
