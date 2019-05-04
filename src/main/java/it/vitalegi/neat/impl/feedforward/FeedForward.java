package it.vitalegi.neat.impl.feedforward;

import it.vitalegi.neat.impl.Gene;

public interface FeedForward {

	double[] initInputs(double[] inputs, double[] biases);

	double[] feedForward(Gene gene, double[] inputs);
}