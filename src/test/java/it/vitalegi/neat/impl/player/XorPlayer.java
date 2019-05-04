package it.vitalegi.neat.impl.player;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.vitalegi.neat.impl.feedforward.FeedForward;
import it.vitalegi.neat.impl.service.GeneServiceImpl;
import it.vitalegi.neat.impl.util.StringUtil;

public class XorPlayer extends AbstractPlayer {

	static final double ONE = 1;
	static final double ZERO = 0;
	static final double[][] dataset = new double[][] { //
			{ XorPlayer.ZERO, XorPlayer.ZERO, XorPlayer.ZERO }, //
			{ XorPlayer.ONE, XorPlayer.ZERO, XorPlayer.ONE }, //
			{ XorPlayer.ZERO, XorPlayer.ONE, XorPlayer.ONE }, //
			{ XorPlayer.ONE, XorPlayer.ONE, XorPlayer.ZERO } //
	};

	public XorPlayer(FeedForward feedForward, GeneServiceImpl geneService, int generation, double[] biases) {
		super(feedForward, geneService);
		this.generation = generation;
		this.biases = biases;
	}

	double fitness;
	int generation;
	double[] biases;

	public static double expected(double in1, double in2) {
		for (double[] data : dataset) {
			if (data[0] == in1 && data[1] == in2) {
				return data[2];
			}
		}
		throw new IllegalArgumentException();
	}

	public double score(double expected, double actual) {
		if (expected == ONE) {
			if (0.95 <= actual && actual <= 1.1) {
				return 1;
			}
			if (0.90 <= actual) {
				return 0.8;
			}
			return 0;
		}
		if (actual <= 0.6) {
			return 1;
		}
		if (actual <= 0.7) {
			return 0.9;
		}
		if (actual <= 0.6) {
			return 0.8;
		}
		return 0;

	}

	private double[] execute(double in1, double in2) {
		return feedForward(new double[] { in1, in2 }, biases);
	}

	public void execute() {
		fitness = 0;
		for (int i = 0; i < 4; i++) {
			double in1 = i % 2 == 0 ? ONE : ZERO;
			double in2 = i / 2 == 0 ? ONE : ZERO;
			double actual = execute(in1, in2)[0];
			double expected = expected(in1, in2);
			fitness += score(expected, actual);
		}
	}

	public void assertPerfect() {
		assertPerfect(ZERO, ZERO, ZERO);
		assertPerfect(ZERO, ONE, ONE);
		assertPerfect(ONE, ZERO, ONE);
		assertPerfect(ONE, ONE, ZERO);
	}

	protected void assertPerfect(double in1, double in2, double expected) {
		double[] outs = execute(in1, in2);
		log.info("{} XOR {} = expected {} actual {}", in1, in2, expected, StringUtil.format(outs[0]));
		if (expected == ONE) {
			Assert.assertThat(outs[0], Matchers.greaterThan(0.8));
		}
		if (expected == ZERO) {
			Assert.assertThat(outs[0], Matchers.lessThan(0.8));
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" " + StringUtil.format(fitness));
		return sb.toString();
	}

	Logger log = LoggerFactory.getLogger(XorPlayer.class);

	@Override
	public double getFitness() {
		return fitness;
	}
}