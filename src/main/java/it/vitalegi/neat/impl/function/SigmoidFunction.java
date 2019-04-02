package it.vitalegi.neat.impl.function;

public class SigmoidFunction {

	public static double customSigmoid(double x) {

		return sigmoid(4.9 * x);
	}

	public static double sigmoid(double x) {

		return 1 / (1 + Math.pow(Math.E, -x));
	}
}
