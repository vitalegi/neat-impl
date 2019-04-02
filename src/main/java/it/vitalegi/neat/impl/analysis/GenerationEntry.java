package it.vitalegi.neat.impl.analysis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.Generation;
import it.vitalegi.neat.impl.Player;
import it.vitalegi.neat.impl.util.StringUtil;

public class GenerationEntry {

	private int generation;
	private int population;
	private Player bestPlayer;

	private double bestScore;
	private double avgScore;

	private int nodes;
	private double avgNodes;

	private int connections;
	private double avgConnections;

	public static GenerationEntry newInstance(Generation generation) {

		Player bestPlayer = generation.getPlayers().stream()//
				.sorted(Comparator.comparing(Player::getFitness).reversed())//
				.findFirst().orElseThrow(() -> new RuntimeException("No best player found"));

		GenerationEntry entry = new GenerationEntry();
		entry.setGeneration(generation.getGenNumber());
		entry.setPopulation(generation.getPlayers().size());
		entry.setBestPlayer(bestPlayer);
		entry.setBestScore(bestPlayer.getFitness());
		entry.setAvgScore(generation.getPlayers().stream()//
				.mapToDouble(Player::getFitness)//
				.average().getAsDouble());

		entry.setNodes(generation.getPlayers().stream()//
				.map(Player::getGene)//
				.map(Gene::getNodes)//
				.mapToInt(List::size)//
				.sum());

		entry.setAvgNodes(generation.getPlayers().stream()//
				.map(Player::getGene)//
				.map(Gene::getNodes)//
				.mapToInt(List::size)//
				.average().getAsDouble());

		entry.setConnections(generation.getPlayers().stream()//
				.map(Player::getGene)//
				.map(Gene::getConnections)//
				.mapToInt(List::size)//
				.sum());

		entry.setAvgConnections(generation.getPlayers().stream()//
				.map(Player::getGene)//
				.map(Gene::getConnections)//
				.mapToInt(List::size)//
				.average().getAsDouble());

		return entry;
	}

	public List<String> getTextAnalysis() {

		List<String> row = new ArrayList<>();
		row.add(String.valueOf(generation));
		row.add(String.valueOf(population));
		row.add(String.valueOf(bestPlayer.getGeneId()));
		row.add(String.valueOf(StringUtil.format(bestScore)));
		row.add(String.valueOf(StringUtil.format(avgScore)));
		row.add(String.valueOf(nodes));
		row.add(String.valueOf(StringUtil.format(avgNodes)));
		row.add(String.valueOf(connections));
		row.add(String.valueOf(StringUtil.format(avgConnections)));
		return row;
	}

	public static List<String> getTextAnalysisHeaders() {
		List<String> headers = new ArrayList<>();
		headers.add("Gen");
		headers.add("Pop");
		headers.add("Best Id");
		headers.add("Best Score");
		headers.add("Avg Score");
		headers.add("Tot Nodes");
		headers.add("Avg Nodes");
		headers.add("Tot Conns");
		headers.add("Avg Conns");
		return headers;

	}

	public int getPopulation() {
		return population;
	}

	public void setPopulation(int population) {
		this.population = population;
	}

	public Player getBestPlayer() {
		return bestPlayer;
	}

	public void setBestPlayer(Player bestPlayer) {
		this.bestPlayer = bestPlayer;
	}

	public double getBestScore() {
		return bestScore;
	}

	public void setBestScore(double bestScore) {
		this.bestScore = bestScore;
	}

	public double getAvgScore() {
		return avgScore;
	}

	public void setAvgScore(double avgScore) {
		this.avgScore = avgScore;
	}

	public int getNodes() {
		return nodes;
	}

	public void setNodes(int nodes) {
		this.nodes = nodes;
	}

	public double getAvgNodes() {
		return avgNodes;
	}

	public void setAvgNodes(double avgNodes) {
		this.avgNodes = avgNodes;
	}

	public int getConnections() {
		return connections;
	}

	public void setConnections(int connections) {
		this.connections = connections;
	}

	public double getAvgConnections() {
		return avgConnections;
	}

	public void setAvgConnections(double avgConnections) {
		this.avgConnections = avgConnections;
	}

	public int getGeneration() {
		return generation;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}

}