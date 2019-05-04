package it.vitalegi.neat.impl.player;

import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.feedforward.FeedForward;
import it.vitalegi.neat.impl.service.GeneServiceImpl;

public class DummyPlayer extends AbstractPlayer {

	protected double fitness;

	public DummyPlayer(FeedForward feedForward, GeneServiceImpl geneService, Gene gene, double fitness) {
		super(feedForward, geneService);
		this.gene = gene;
		this.fitness = fitness;
	}

	public DummyPlayer(FeedForward feedForward, GeneServiceImpl geneService) {
		this(feedForward, geneService, null, 0);
	}

	@Override
	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

}