package it.vitalegi.neat.impl;

public class DummyPlayerFactory implements PlayerFactory {

	private double fitness;

	@Override
	public Player newPlayer(Gene gene) {
		Player p = Player.newPlayer(gene);
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
