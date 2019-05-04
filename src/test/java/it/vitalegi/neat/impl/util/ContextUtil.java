package it.vitalegi.neat.impl.util;

import it.vitalegi.neat.impl.analysis.EvolutionAnalysis;
import it.vitalegi.neat.impl.analysis.GenerationEntry;
import it.vitalegi.neat.impl.configuration.NeatConfig;
import it.vitalegi.neat.impl.configuration.NeatConfigFactory;
import it.vitalegi.neat.impl.feedforward.FeedForward;
import it.vitalegi.neat.impl.feedforward.FeedForwardImpl;
import it.vitalegi.neat.impl.function.CompatibilityDistance;
import it.vitalegi.neat.impl.function.CompatibilityDistanceImpl;
import it.vitalegi.neat.impl.player.PlayerFactory;
import it.vitalegi.neat.impl.player.XorPlayerFactory;
import it.vitalegi.neat.impl.service.FirstGenerationService;
import it.vitalegi.neat.impl.service.FirstGenerationServiceImpl;
import it.vitalegi.neat.impl.service.GeneMutationService;
import it.vitalegi.neat.impl.service.GeneMutationServiceImpl;
import it.vitalegi.neat.impl.service.GeneService;
import it.vitalegi.neat.impl.service.GeneServiceImpl;
import it.vitalegi.neat.impl.service.GenerationService;
import it.vitalegi.neat.impl.service.GenerationServiceImpl;
import it.vitalegi.neat.impl.service.NextGenerationService;
import it.vitalegi.neat.impl.service.NextGenerationServiceImpl;
import it.vitalegi.neat.impl.service.SpeciesService;
import it.vitalegi.neat.impl.service.SpeciesServiceImpl;

public class ContextUtil {

	public static ContextUtil builder() {
		ContextUtil context = new ContextUtil();

		context.feedForward = new FeedForwardImpl();
		context.generationService = new GenerationServiceImpl();
		context.speciesService = new SpeciesServiceImpl();
		context.geneService = new GeneServiceImpl();
		context.compatibilityDistance = new CompatibilityDistanceImpl(0.1, 1, 2);
		context.evolutionAnalysis = new EvolutionAnalysis();
		context.generationEntry = new GenerationEntry();
		context.geneMutationService = new GeneMutationServiceImpl();
		context.nextGenerationService = new NextGenerationServiceImpl();
		context.neatConfig = NeatConfigFactory.create().build();
		context.firstGenerationService = new FirstGenerationServiceImpl();
		return context;
	}

	CompatibilityDistance compatibilityDistance;
	EvolutionAnalysis evolutionAnalysis;
	FeedForward feedForward;
	FirstGenerationService firstGenerationService;
	GeneMutationService geneMutationService;
	GenerationEntry generationEntry;
	GenerationService generationService;
	GeneService geneService;
	NeatConfig neatConfig;
	NextGenerationService nextGenerationService;
	PlayerFactory playerFactory;
	SpeciesService speciesService;

	public ContextUtil compatibilityDistance(CompatibilityDistance compatibilityDistance) {
		this.compatibilityDistance = compatibilityDistance;
		return this;
	}

	public ContextUtil evolutionAnalysis(EvolutionAnalysis evolutionAnalysis) {
		this.evolutionAnalysis = evolutionAnalysis;
		return this;
	}

	public ContextUtil feedForward(FeedForward feedForward) {
		this.feedForward = feedForward;
		return this;
	};

	public ContextUtil firstGenerationService(FirstGenerationService firstGenerationService) {
		this.firstGenerationService = firstGenerationService;
		return this;
	};

	public ContextUtil geneMutationService(GeneMutationService geneMutationService) {
		this.geneMutationService = geneMutationService;
		return this;
	};

	public ContextUtil generationEntry(GenerationEntry generationEntry) {
		this.generationEntry = generationEntry;
		return this;
	}

	public ContextUtil generationService(GenerationService generationService) {
		this.generationService = generationService;
		return this;
	};

	public ContextUtil geneService(GeneService geneService) {
		this.geneService = geneService;
		return this;
	};

	public CompatibilityDistance getCompatibilityDistance() {
		return compatibilityDistance;
	};

	public EvolutionAnalysis getEvolutionAnalysis() {
		return evolutionAnalysis;
	};

	public FeedForward getFeedForward() {
		return feedForward;
	}

	public FirstGenerationService getFirstGenerationService() {
		return firstGenerationService;
	};

	public GeneMutationService getGeneMutationService() {
		return geneMutationService;
	}

	public GenerationEntry getGenerationEntry() {
		return generationEntry;
	}

	public GenerationService getGenerationService() {
		return generationService;
	}

	public GeneService getGeneService() {
		return geneService;
	}

	public NeatConfig getNeatConfig() {
		return neatConfig;
	}

	public NextGenerationService getNextGenerationService() {
		return nextGenerationService;
	}

	public PlayerFactory getPlayerFactory() {
		return playerFactory;
	}

	public SpeciesService getSpeciesService() {
		return speciesService;
	}

	public ContextUtil inject() {
		if (feedForward instanceof FeedForwardImpl) {
			((FeedForwardImpl) feedForward).setGeneService(geneService);
		}
		if (generationService instanceof GenerationServiceImpl) {
			((GenerationServiceImpl) generationService).setGeneService(geneService);
			((GenerationServiceImpl) generationService).setSpeciesService(speciesService);
		}
		if (nextGenerationService instanceof NextGenerationServiceImpl) {
			((NextGenerationServiceImpl) nextGenerationService).setGeneMutationService(geneMutationService);
			((NextGenerationServiceImpl) nextGenerationService).setGenerationService(generationService);
			((NextGenerationServiceImpl) nextGenerationService).setGeneService(geneService);
			((NextGenerationServiceImpl) nextGenerationService).setSpeciesService(speciesService);
		}
		if (firstGenerationService instanceof FirstGenerationServiceImpl) {
			((FirstGenerationServiceImpl) firstGenerationService).setGenerationService(generationService);
			((FirstGenerationServiceImpl) firstGenerationService).setGeneService(geneService);
		}
		if (speciesService instanceof SpeciesServiceImpl) {
			((SpeciesServiceImpl) speciesService).setCompatibilityDistance(compatibilityDistance);
		}

		if (geneService instanceof GeneServiceImpl) {
		}
		if (geneMutationService instanceof GeneMutationServiceImpl) {
			((GeneMutationServiceImpl) geneMutationService).setGeneService(geneService);
		}
		if (compatibilityDistance instanceof CompatibilityDistanceImpl) {
			((CompatibilityDistanceImpl) compatibilityDistance).setGeneService(geneService);
		}
		if (evolutionAnalysis instanceof EvolutionAnalysis) {
			evolutionAnalysis.setGenerationEntry(generationEntry);
			evolutionAnalysis.setGeneService(geneService);
		}
		if (playerFactory != null && playerFactory instanceof XorPlayerFactory) {
			((XorPlayerFactory) playerFactory).setFeedForward(feedForward);
			((XorPlayerFactory) playerFactory).setGeneService(geneService);
		}
		if (generationEntry instanceof GenerationEntry) {
			generationEntry.setGeneService(geneService);
			generationEntry.setCompatibilityDistance(compatibilityDistance);
		}
		return this;
	}

	public ContextUtil neatConfig(NeatConfig neatConfig) {
		this.neatConfig = neatConfig;
		return this;
	}

	public ContextUtil nextGenerationService(NextGenerationService nextGenerationService) {
		this.nextGenerationService = nextGenerationService;
		return this;
	}

	public ContextUtil playerFactory(PlayerFactory playerFactory) {
		this.playerFactory = playerFactory;
		return this;
	}

	public void setNextGenerationService(NextGenerationService nextGenerationService) {
		this.nextGenerationService = nextGenerationService;
	}

	public ContextUtil speciesService(SpeciesService speciesService) {
		this.speciesService = speciesService;
		return this;
	}

}
