package it.vitalegi.neat.impl.feedforward;

import it.vitalegi.neat.impl.Gene;

public interface FeedForward {

	double[] feedForward(Gene gene, double[] inputs);

	double[] initInputs(double[] inputs, double[] biases);
}