package it.vitalegi.neat.impl;

public class Random {

	private static java.util.Random random;

	private static void checkInit() {
		if (random == null) {
			init();
		}
	}

	public static void init() {
		random = new java.util.Random();
	}

	public static boolean nextBoolean() {
		checkInit();
		return nextBoolean(0.5);
	}

	public static boolean nextBoolean(double trueProb) {
		checkInit();
		return random.nextDouble() <= trueProb;
	}

	public static double nextDouble() {
		checkInit();
		return random.nextDouble();
	}

	public static double nextDouble(double from, double to) {
		checkInit();
		return -from + (to - from) * random.nextDouble();
	}

	public static int nextInt(int bound) {
		checkInit();
		return random.nextInt(bound);
	}

	/**
	 * restituisce un indice dell'array, selezionato in modo casuale, pesando
	 * con i valori dell'array
	 *
	 * @param weights
	 * @return
	 */
	public static int nextRandom(double[] weights) {
		checkInit();
		double sum = 0;
		for (int i = 0; i < weights.length; i++) {
			sum += weights[i];
		}
		double value = nextDouble(0, sum);

		sum = 0;
		for (int i = 0; i < weights.length; i++) {
			sum += weights[i];
			if (value <= sum) {
				return i;
			}
		}
		return weights.length - 1;
	}
}
