package it.vitalegi.neat.impl.service;

import it.vitalegi.neat.impl.Generation;
import it.vitalegi.neat.impl.Species;
import it.vitalegi.neat.impl.player.Player;

public interface GenerationService {

	Species addPlayer(Generation gen, Player player);

	void addPlayer(Generation gen, Player player, Species species);

	void addSpecies(Generation gen, Species species);

	void computeFitnesses(Generation gen);

	Species getCompatibleSpecies(Generation gen, Player player);

	String stringify(Generation gen);
}