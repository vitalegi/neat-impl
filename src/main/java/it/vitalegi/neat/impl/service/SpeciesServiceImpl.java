package it.vitalegi.neat.impl.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.vitalegi.neat.impl.Generation;
import it.vitalegi.neat.impl.Species;
import it.vitalegi.neat.impl.function.CompatibilityDistance;
import it.vitalegi.neat.impl.player.Player;

@Service
public class SpeciesServiceImpl {
	@Autowired
	CompatibilityDistance compatibilityDistance;
	Logger log = LoggerFactory.getLogger(SpeciesServiceImpl.class);

	public List<Player> getBestPlayers(Species species, int size) {
		if (log.isDebugEnabled()) {
			if (size < species.getPlayers().size()) {
				log.debug("Specie {}: rimuovo {} elementi", species.getId(), species.getPlayers().size() - size);
			} else {
				log.debug("Specie {}: mantengo tutti gli elementi ({})", species.getId(), species.getPlayers().size());
			}
		}
		return species.getPlayers().stream()//
				.sorted(Comparator.comparing(Player::getFitness).reversed()) //
				.limit(size)//
				.collect(Collectors.toList());
	}

	public Player getChampion(Species species) {
		return species.getPlayers().stream()//
				.sorted(Comparator.comparing(Player::getFitness).reversed())//
				.findFirst().orElse(null);
	}

	public double getFitness(Species species, int generation) {
		if (generation < species.getStartGeneration()) {
			throw new IllegalArgumentException(
					"Too early generation. Minimum: " + species.getStartGeneration() + " actual: " + generation);
		}
		int relativeGeneration = generation - species.getStartGeneration();
		if (relativeGeneration >= species.getHistoryBestFitnesses().size()) {
			throw new IllegalArgumentException("Not yet computed generation. Last gen available: "
					+ (species.getStartGeneration() + species.getHistoryBestFitnesses().size() - 1) + " required: "
					+ generation);
		}
		return species.getHistoryBestFitnesses().get(relativeGeneration);
	}

	public Player getPlayerByGeneId(Species species, long id) {
		return species.getPlayers().stream().filter(p -> p.getGeneId() == id).findFirst().orElse(null);
	}

	public boolean isCompatible(Species species, Player player) {
		return compatibilityDistance.isCompatible(species.getRepresentative().getGene(), player.getGene());
	}

	public boolean isRelevantSpecies(Species species) {
		return species.getPlayers().size() >= Generation.MIN_SPECIES_SIZE_TO_BE_RELEVANT;
	}

	public Species newInstance(long id, int startGeneration) {
		return new Species(id, startGeneration);
	}

	public void setCompatibilityDistance(CompatibilityDistance compatibilityDistance) {
		this.compatibilityDistance = compatibilityDistance;
	}
}
