package it.vitalegi.neat.impl.player;

import it.vitalegi.neat.impl.Gene;

public class DummyPlayer extends AbstractPlayer {

	protected double fitness;

	public DummyPlayer(Gene gene, double fitness) {
		super();
		this.gene = gene;
		this.fitness = fitness;
	}

	public DummyPlayer() {
		this(null, 0);
	}

	@Override
	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

}