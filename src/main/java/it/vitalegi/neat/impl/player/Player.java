package it.vitalegi.neat.impl.player;

import it.vitalegi.neat.impl.Gene;

public interface Player {

	public double[] feedForward(double[] inputs, double[] biases);

	public double getFitness();

	public Gene getGene();

	public long getGeneId();
}