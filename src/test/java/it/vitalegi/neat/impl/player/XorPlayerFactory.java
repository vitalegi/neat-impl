package it.vitalegi.neat.impl.player;

import it.vitalegi.neat.impl.Gene;

public class XorPlayerFactory implements PlayerFactory {

	private int generation;
	private double[] biases;

	public XorPlayerFactory(double[] biases) {
		super();
		this.biases = biases;
	}

	@Override
	public XorPlayer newPlayer(Gene gene) {
		XorPlayer p = new XorPlayer(generation, biases);
		p.setGene(gene);
		return p;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}
}