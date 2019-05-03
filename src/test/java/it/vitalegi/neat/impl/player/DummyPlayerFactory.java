package it.vitalegi.neat.impl.player;

import it.vitalegi.neat.impl.Gene;

public class DummyPlayerFactory implements PlayerFactory {

	private double fitness;

	@Override
	public Player newPlayer(Gene gene) {
		DummyPlayer p = new DummyPlayer();
		p.setGene(gene);
		p.setFitness(fitness);
		return p;
	}

	public Player newPlayer(Gene gene, double fitness) {
		setFitness(fitness);
		return newPlayer(gene);
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
}
