package it.vitalegi.neat.impl.player;

import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.feedforward.FeedForward;
import it.vitalegi.neat.impl.service.GeneService;

public class DummyPlayer extends AbstractPlayer {

	protected double fitness;

	public DummyPlayer(FeedForward feedForward, GeneService geneService) {
		this(feedForward, geneService, null, 0);
	}

	public DummyPlayer(FeedForward feedForward, GeneService geneService, Gene gene, double fitness) {
		super(feedForward, geneService);
		this.gene = gene;
		this.fitness = fitness;
	}

	@Override
	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

}