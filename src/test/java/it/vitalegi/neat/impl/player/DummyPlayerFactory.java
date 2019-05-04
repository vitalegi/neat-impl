package it.vitalegi.neat.impl.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.feedforward.FeedForward;
import it.vitalegi.neat.impl.service.GeneService;

@Service
public class DummyPlayerFactory implements PlayerFactory {

	@Autowired
	FeedForward feedForward;
	private double fitness;
	@Autowired
	GeneService geneService;

	@Override
	public Player newPlayer(Gene gene) {
		DummyPlayer p = new DummyPlayer(feedForward, geneService);
		p.setGene(gene);
		p.setFitness(fitness);
		return p;
	}

	public Player newPlayer(Gene gene, double fitness) {
		setFitness(fitness);
		return newPlayer(gene);
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
}
