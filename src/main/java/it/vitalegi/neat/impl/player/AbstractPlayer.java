package it.vitalegi.neat.impl.player;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.feedforward.FeedForward;

public abstract class AbstractPlayer implements Player {

	private static Logger log = LoggerFactory.getLogger(AbstractPlayer.class);

	protected Gene gene;

	@Override
	public double[] feedForward(double[] inputs, double[] biases) {

		if (log.isDebugEnabled()) {
			log.debug("Start Execute feedForward({},{}) with gene: {}", Arrays.toString(inputs),
					Arrays.toString(biases), gene.stringify(true));
		}
		FeedForward ff = gene.getFeedForward();
		double[] fullInputs = ff.initInputs(inputs, biases);
		double[] outputs = ff.feedForward(fullInputs);

		if (log.isDebugEnabled()) {
			log.debug("End Execute feedForward({})={} with gene: {}", Arrays.toString(fullInputs),
					Arrays.toString(outputs), gene.stringify(true));
		}
		return outputs;
	}

	@Override
	public String feedForwardEndStatus(double[] inputs, double[] biases) {
		FeedForward ff = gene.getFeedForward();
		double[] fullInputs = ff.initInputs(inputs, biases);

		ff.feedForward(fullInputs);
		return ff.graphToString();
	}

	@Override
	public abstract double getFitness();

	@Override
	public Gene getGene() {
		return gene;
	}

	@Override
	public long getGeneId() {
		return gene.getId();
	}

	public void setGene(Gene gene) {
		this.gene = gene;
	}

}
