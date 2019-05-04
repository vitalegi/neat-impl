package it.vitalegi.neat.impl.service;

import java.util.List;

import it.vitalegi.neat.impl.Species;
import it.vitalegi.neat.impl.configuration.NeatConfig;
import it.vitalegi.neat.impl.player.Player;

public interface SpeciesService {

	List<Player> getBestPlayers(Species species, int size);

	Player getChampion(Species species);

	double getFitness(Species species, int generation);

	Player getPlayerByGeneId(Species species, long id);

	boolean isCompatible(Species species, Player player);

	boolean isRelevantSpecies(NeatConfig config, Species species);

	Species newInstance(long id, int startGeneration);

}