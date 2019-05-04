package it.vitalegi.neat.impl.player;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.feedforward.FeedForward;
import it.vitalegi.neat.impl.service.GeneServiceImpl;

public abstract class AbstractPlayer implements Player {

	private static Logger log = LoggerFactory.getLogger(AbstractPlayer.class);

	FeedForward feedForward;

	protected Gene gene;

	GeneServiceImpl geneService;
	public AbstractPlayer(FeedForward feedForward, GeneServiceImpl geneService) {
		this.feedForward = feedForward;
		this.geneService = geneService;
	}

	@Override
	public double[] feedForward(double[] inputs, double[] biases) {

		if (log.isDebugEnabled()) {
			log.debug("Start Execute feedForward({},{}) with gene: {}", Arrays.toString(inputs),
					Arrays.toString(biases), getGeneService().stringify(gene, true));
		}
		FeedForward ff = getFeedForward();
		double[] fullInputs = ff.initInputs(inputs, biases);
		double[] outputs = ff.feedForward(gene, fullInputs);

		if (log.isDebugEnabled()) {
			log.debug("End Execute feedForward({})={} with gene: {}", Arrays.toString(fullInputs),
					Arrays.toString(outputs), getGeneService().stringify(gene, true));
		}
		return outputs;
	}

	public FeedForward getFeedForward() {
		return feedForward;
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

	public GeneServiceImpl getGeneService() {
		return geneService;
	}

	public void setGene(Gene gene) {
		this.gene = gene;
	}
}
