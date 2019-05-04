package it.vitalegi.neat.impl.analysis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.vitalegi.neat.impl.Connection;
import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.Generation;
import it.vitalegi.neat.impl.Species;
import it.vitalegi.neat.impl.function.CompatibilityDistance;
import it.vitalegi.neat.impl.player.Player;
import it.vitalegi.neat.impl.service.GeneServiceImpl;
import it.vitalegi.neat.impl.util.Pair;

@Service
public class GenerationEntry {

	@Autowired
	GeneServiceImpl geneService;

	@Autowired
	CompatibilityDistance compatibilityDistance;

	public List<Pair<String, Metric>> getColumns() {
		List<Pair<String, Metric>> columns = new ArrayList<>();
		columns.add(Pair.newInstance("Gen", (prev, curr) -> curr.getGenNumber()));

		columns.add(Pair.newInstance("Pop", (prev, curr) -> curr.getPlayers().size()));
		columns.add(Pair.newInstance("Best Id", (prev, curr) -> //
		curr.getPlayers().stream()//
				.sorted(Comparator.comparing(Player::getFitness).reversed())//
				.findFirst().orElse(null)//
				.getGene().getId()));

		columns.add(Pair.newInstance("Best Score", (prev, curr) -> //
		curr.getPlayers().stream()//
				.sorted(Comparator.comparing(Player::getFitness).reversed())//
				.findFirst().orElse(null)//
				.getFitness()));

		columns.add(Pair.newInstance("Avg Score", (prev, curr) -> curr.getPlayers().stream()//
				.mapToDouble(Player::getFitness)//
				.average().getAsDouble()));

		columns.add(Pair.newInstance("# Species", (prev, curr) -> curr.getSpecies().size()));
		columns.add(Pair.newInstance("Avg Species Size", (prev, curr) -> curr.getSpecies().stream()//
				.map(Species::getPlayers)//
				.mapToInt(List::size)//
				.average().getAsDouble()));

		columns.add(Pair.newInstance("# Species Add", (prev, curr) -> {
			List<Long> currSpeciesIds = curr.getSpecies().stream().map(Species::getId).collect(Collectors.toList());
			List<Long> prevSpeciesIds;
			if (prev != null) {
				prevSpeciesIds = prev.getSpecies().stream().map(Species::getId).collect(Collectors.toList());
			} else {
				prevSpeciesIds = new ArrayList<>();
			}
			return currSpeciesIds.stream()//
					.filter(id -> !prevSpeciesIds.contains(id))//
					.count();
		}));
		columns.add(Pair.newInstance("# Species Remove", (prev, curr) -> {
			List<Long> currSpeciesIds = curr.getSpecies().stream().map(Species::getId).collect(Collectors.toList());
			List<Long> prevSpeciesIds;
			if (prev != null) {
				prevSpeciesIds = prev.getSpecies().stream().map(Species::getId).collect(Collectors.toList());
			} else {
				prevSpeciesIds = new ArrayList<>();
			}
			return prevSpeciesIds.stream()//
					.filter(id -> !currSpeciesIds.contains(id))//
					.count();
		}));
		columns.add(Pair.newInstance("# Nodes", (prev, curr) -> curr.getPlayers().stream()//
				.map(Player::getGene)//
				.map(Gene::getNodes)//
				.mapToInt(List::size)//
				.sum()));
		columns.add(Pair.newInstance("Avg Nodes", (prev, curr) -> curr.getPlayers().stream()//
				.map(Player::getGene)//
				.map(Gene::getNodes)//
				.mapToInt(List::size)//
				.average().getAsDouble()));

		columns.add(Pair.newInstance("# Conns x Player", (prev, curr) -> curr.getPlayers().stream()//
				.map(Player::getGene)//
				.map(Gene::getConnections)//
				.mapToInt(List::size)//
				.sum()));

		columns.add(Pair.newInstance("Avg Conns x Player", (prev, curr) -> curr.getPlayers().stream()//
				.map(Player::getGene)//
				.map(Gene::getConnections)//
				.mapToInt(List::size)//
				.average().getAsDouble()));

		columns.add(Pair.newInstance("# Active Conns x Player", (prev, curr) -> curr.getPlayers().stream()//
				.map(Player::getGene)//
				.mapToLong(g -> g.getConnections().stream().filter(Connection::isEnabled).count())//
				.count()));

		columns.add(Pair.newInstance("Avg Active Conns x Player", (prev, curr) -> curr.getPlayers().stream()//
				.map(Player::getGene)//
				.mapToLong(g -> g.getConnections().stream().filter(Connection::isEnabled).count())//
				.average().getAsDouble()));

		columns.add(Pair.newInstance("Min Conn W", (prev, curr) -> getConnectionWeights(curr).min().getAsDouble()));
		columns.add(Pair.newInstance("Max Conn W", (prev, curr) -> getConnectionWeights(curr).max().getAsDouble()));
		columns.add(Pair.newInstance("Avg Conn W", (prev, curr) -> getConnectionWeights(curr).average().getAsDouble()));
		columns.add(Pair.newInstance("Min distance", (prev, curr) -> getDistanceStream(curr).min().getAsDouble()));
		columns.add(Pair.newInstance("Max distance", (prev, curr) -> getDistanceStream(curr).max().getAsDouble()));
		columns.add(Pair.newInstance("Avg distance", (prev, curr) -> getDistanceStream(curr).average().getAsDouble()));
		columns.add(Pair.newInstance("Min Matching", (prev, curr) -> getMatchingGenes(curr).min().getAsDouble()));
		columns.add(Pair.newInstance("Max Matching", (prev, curr) -> getMatchingGenes(curr).max().getAsDouble()));
		columns.add(Pair.newInstance("Avg Matching", (prev, curr) -> getMatchingGenes(curr).average().getAsDouble()));
		columns.add(Pair.newInstance("Min Disjoint", (prev, curr) -> getDisjointGenes(curr).min().getAsDouble()));
		columns.add(Pair.newInstance("Max Disjoint", (prev, curr) -> getDisjointGenes(curr).max().getAsDouble()));
		columns.add(Pair.newInstance("Avg Disjoint", (prev, curr) -> getDisjointGenes(curr).average().getAsDouble()));
		columns.add(
				Pair.newInstance("Min Avg Weight Diff", (prev, curr) -> getAvgWeightDiff(curr).min().getAsDouble()));
		columns.add(
				Pair.newInstance("Max Avg Weight Diff", (prev, curr) -> getAvgWeightDiff(curr).max().getAsDouble()));
		columns.add(Pair.newInstance("Avg Avg Weight Diff",
				(prev, curr) -> getAvgWeightDiff(curr).average().getAsDouble()));

		return columns;
	}

	private DoubleStream getConnectionWeights(Generation generation) {

		return generation.getPlayers().stream()//
				.map(Player::getGene)//
				.flatMap(g -> g.getConnections().stream())//
				.mapToDouble(Connection::getWeight);
	}

	private DoubleStream getDistanceStream(Generation generation) {

		return getOperationStream(generation, (g1, g2) -> compatibilityDistance.getDistance(g1, g2));
	}

	private DoubleStream getMatchingGenes(Generation generation) {

		return getOperationStream(generation, (g1, g2) -> (double) geneService.getMatchingGenesCount(g1, g2));
	}

	private DoubleStream getDisjointGenes(Generation generation) {

		return getOperationStream(generation, (g1, g2) -> (double) geneService.getDisjointGenesCount(g1, g2));
	}

	private DoubleStream getAvgWeightDiff(Generation generation) {

		return getOperationStream(generation, (g1,
				g2) -> (double) geneService.getAvgWeightDifference(g1, g2, geneService.getMatchingGenesCount(g1, g2)));
	}

	private DoubleStream getOperationStream(Generation generation, BiFunction<Gene, Gene, Double> function) {

		return generation.getPlayers().stream()//
				.map(Player::getGene)//
				.flatMapToDouble(gene1 -> generation.getPlayers().stream()//
						.map(Player::getGene)//
						.filter(gene2 -> gene2.getId() != gene1.getId()) //
						.mapToDouble(gene2 -> function.apply(gene1, gene2)));
	}

	public void setGeneService(GeneServiceImpl geneService) {
		this.geneService = geneService;
	}

	public void setCompatibilityDistance(CompatibilityDistance compatibilityDistance) {
		this.compatibilityDistance = compatibilityDistance;
	}
}