package it.vitalegi.neat.impl.analysis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.Generation;
import it.vitalegi.neat.impl.Player;
import it.vitalegi.neat.impl.Species;
import it.vitalegi.neat.impl.util.StringUtil;

public class GenerationEntry {

	public static List<String> getTextAnalysisHeaders() {
		List<String> headers = new ArrayList<>();
		headers.add("Gen");
		headers.add("Pop");
		headers.add("Best Id");
		headers.add("Best Score");
		headers.add("Avg Score");
		headers.add("# Species");
		headers.add("Avg Species Size");
		headers.add("# Species Add");
		headers.add("# Species Remove");
		headers.add("# Nodes");
		headers.add("Avg Nodes");
		headers.add("# Conns");
		headers.add("Avg Conns");
		headers.add("Species Ids");
		return headers;

	}

	public static GenerationEntry newInstance(Generation prevGeneration, Generation generation) {

		Player bestPlayer = generation.getPlayers().stream()//
				.sorted(Comparator.comparing(Player::getFitness).reversed())//
				.findFirst().orElseThrow(() -> new RuntimeException("No best player found"));

		GenerationEntry entry = new GenerationEntry();
		entry.generation = generation.getGenNumber();
		entry.population = generation.getPlayers().size();
		entry.bestPlayer = bestPlayer;
		entry.bestScore = bestPlayer.getFitness();
		entry.avgScore = generation.getPlayers().stream()//
				.mapToDouble(Player::getFitness)//
				.average().getAsDouble();

		entry.species = (generation.getSpecies().size());

		entry.avgSpecies = generation.getSpecies().stream()//
				.map(Species::getPlayers)//
				.mapToInt(List::size)//
				.average().getAsDouble();

		List<Long> currSpeciesIds = generation.getSpecies().stream().map(Species::getId).collect(Collectors.toList());
		List<Long> prevSpeciesIds;
		if (prevGeneration != null) {
			prevSpeciesIds = prevGeneration.getSpecies().stream().map(Species::getId).collect(Collectors.toList());
		} else {
			prevSpeciesIds = new ArrayList<>();
		}
		entry.addSpecies = (int) currSpeciesIds.stream()//
				.filter(id -> !prevSpeciesIds.contains(id))//
				.count();

		entry.removeSpecies = (int) prevSpeciesIds.stream()//
				.filter(id -> !currSpeciesIds.contains(id))//
				.count();

		entry.speciesIds = currSpeciesIds.stream().sorted().map(String::valueOf).collect(Collectors.joining(", "));

		entry.nodes = generation.getPlayers().stream()//
				.map(Player::getGene)//
				.map(Gene::getNodes)//
				.mapToInt(List::size)//
				.sum();

		entry.avgNodes = generation.getPlayers().stream()//
				.map(Player::getGene)//
				.map(Gene::getNodes)//
				.mapToInt(List::size)//
				.average().getAsDouble();

		entry.connections = generation.getPlayers().stream()//
				.map(Player::getGene)//
				.map(Gene::getConnections)//
				.mapToInt(List::size)//
				.sum();

		entry.avgConnections = generation.getPlayers().stream()//
				.map(Player::getGene)//
				.map(Gene::getConnections)//
				.mapToInt(List::size)//
				.average().getAsDouble();

		return entry;
	}

	private double avgConnections;
	private double avgNodes;
	private double avgScore;
	private Player bestPlayer;
	private double bestScore;
	private int connections;
	private int generation;
	private int nodes;
	private int population;
	private int species;
	private int addSpecies;
	private int removeSpecies;
	private double avgSpecies;
	private String speciesIds;

	public List<String> getTextAnalysis() {

		List<String> row = new ArrayList<>();
		row.add(String.valueOf(generation));
		row.add(String.valueOf(population));
		row.add(String.valueOf(bestPlayer.getGeneId()));
		row.add(String.valueOf(StringUtil.format(bestScore)));
		row.add(String.valueOf(StringUtil.format(avgScore)));
		row.add(String.valueOf(species));
		row.add(String.valueOf(StringUtil.format(avgSpecies)));
		row.add(String.valueOf(addSpecies));
		row.add(String.valueOf(removeSpecies));
		row.add(String.valueOf(nodes));
		row.add(String.valueOf(StringUtil.format(avgNodes)));
		row.add(String.valueOf(connections));
		row.add(String.valueOf(StringUtil.format(avgConnections)));
		row.add(speciesIds);
		return row;
	}

}