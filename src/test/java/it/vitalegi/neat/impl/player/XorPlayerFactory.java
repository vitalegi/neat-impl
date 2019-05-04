package it.vitalegi.neat.impl.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.feedforward.FeedForward;
import it.vitalegi.neat.impl.service.GeneServiceImpl;

@Service
public class XorPlayerFactory implements PlayerFactory {

	private double[] biases;
	@Autowired
	FeedForward feedForward;
	private int generation;
	@Autowired
	GeneServiceImpl geneService;

	public XorPlayerFactory(double[] biases) {
		super();
		this.biases = biases;
	}

	@Override
	public XorPlayer newPlayer(Gene gene) {
		XorPlayer p = new XorPlayer(feedForward, geneService, generation, biases);
		p.setGene(gene);
		return p;
	}

	public void setFeedForward(FeedForward feedForward) {
		this.feedForward = feedForward;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}

	public void setGeneService(GeneServiceImpl geneService) {
		this.geneService = geneService;
	}
}