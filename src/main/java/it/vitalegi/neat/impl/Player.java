package it.vitalegi.neat.impl;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.vitalegi.neat.impl.feedforward.FeedForward;

public class Player {

	private static Logger log = LoggerFactory.getLogger(Player.class);

	public static Player newPlayer(Gene gene) {
		return newPlayer(gene, 0);
	}
	public static Player newPlayer(Gene gene, double fitness) {
		Player player = new Player();
		player.setGene(gene);
		player.setFitness(fitness);
		return player;
	}

	protected double fitness;

	protected Gene gene;

	public double[] feedForward(double[] inputs) {
		if (log.isDebugEnabled()) {
			log.debug("Start Execute feedForward({}) with gene: {}", Arrays.toString(inputs), gene.stringify(true));
		}

		double[] outputs = new FeedForward(gene).feedForward(inputs);

		if (log.isDebugEnabled()) {
			log.debug("End Execute feedForward({})={} with gene: {}", Arrays.toString(inputs), Arrays.toString(outputs),
					gene.stringify(true));
		}
		return outputs;
	}

	public double getFitness() {
		return fitness;
	}

	public Gene getGene() {
		return gene;
	}

	public long getGeneId() {
		return gene.getId();
	}

	public void run() {

	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public void setGene(Gene gene) {
		this.gene = gene;
	}

}
