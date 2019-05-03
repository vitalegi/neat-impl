package it.vitalegi.neat.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.vitalegi.neat.impl.function.CompatibilityDistance;
import it.vitalegi.neat.impl.player.Player;

public class Species {
	public static Species newInstance(long id, int startGeneration, CompatibilityDistance compatibilityDistance) {
		return new Species(id, startGeneration, compatibilityDistance);
	}

	protected CompatibilityDistance compatibilityDistance;
	protected List<Double> historyBestFitnesses;
	protected long id;
	Logger log = LoggerFactory.getLogger(Species.class);

	protected List<Player> players;

	protected int startGeneration;

	public Species() {
	}

	protected Species(long id, int startGeneration, CompatibilityDistance compatibilityDistance) {
		this.id = id;
		this.startGeneration = startGeneration;
		historyBestFitnesses = new ArrayList<>();
		players = new ArrayList<>();
		this.compatibilityDistance = compatibilityDistance;
	}

	public void addFitness(double fitness) {
		historyBestFitnesses.add(fitness);
	}

	public void addPlayer(Player player) {
		players.add(player);
	}

	public List<Player> getBestPlayers(Species species, int size) {
		if (log.isDebugEnabled()) {
			if (size < species.getPlayers().size()) {
				log.debug("Specie {}: rimuovo {} elementi", getId(), getPlayers().size() - size);
			} else {
				log.debug("Specie {}: mantengo tutti gli elementi ({})", getId(), getPlayers().size());
			}
		}
		return species.getPlayers().stream()//
				.sorted(Comparator.comparing(Player::getFitness).reversed()) //
				.limit(size)//
				.collect(Collectors.toList());
	}

	public Player getChampion() {
		return players.stream().sorted(Comparator.comparing(Player::getFitness).reversed()).findFirst().orElse(null);
	}

	public CompatibilityDistance getCompatibilityDistance() {
		return compatibilityDistance;
	}

	public double getFitness(int generation) {
		if (generation < startGeneration) {
			throw new IllegalArgumentException(
					"Too early generation. Minimum: " + startGeneration + " actual: " + generation);
		}
		int relativeGeneration = generation - startGeneration;
		if (relativeGeneration >= historyBestFitnesses.size()) {
			throw new IllegalArgumentException("Not yet computed generation. Last gen available: "
					+ (startGeneration + historyBestFitnesses.size() - 1) + " required: " + generation);
		}
		return historyBestFitnesses.get(relativeGeneration);
	}

	public List<Double> getHistoryBestFitnesses() {
		return historyBestFitnesses;
	}

	public long getId() {
		return id;
	}

	public double getLastFitness() {
		if (historyBestFitnesses.isEmpty()) {
			return 0;
		}
		return historyBestFitnesses.get(historyBestFitnesses.size() - 1);
	}

	public Player getPlayerByGeneId(long id) {
		return players.stream().filter(p -> p.getGeneId() == id).findFirst().orElse(null);
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Player getRepresentative() {
		return players.get(0);
	}

	public int getStartGeneration() {
		return startGeneration;
	}

	public boolean isCompatible(Player player) {
		return compatibilityDistance.isCompatible(getRepresentative().getGene(), player.getGene());
	}

	public boolean isRelevantSpecies() {
		return getPlayers().size() >= Generation.MIN_SPECIES_SIZE_TO_BE_RELEVANT;
	}

	public void setCompatibilityDistance(CompatibilityDistance compatibilityDistance) {
		this.compatibilityDistance = compatibilityDistance;
	}
}
