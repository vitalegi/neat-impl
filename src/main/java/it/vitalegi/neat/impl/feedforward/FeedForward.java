package it.vitalegi.neat.impl.feedforward;

public interface FeedForward {

	double[] initInputs(double[] inputs, double[] biases);

	double[] feedForward(double[] inputs);

	String graphToString();

}