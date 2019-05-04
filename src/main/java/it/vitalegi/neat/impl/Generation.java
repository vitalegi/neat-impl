package it.vitalegi.neat.impl;

import java.util.ArrayList;
import java.util.List;

import it.vitalegi.neat.impl.player.Player;
import it.vitalegi.neat.impl.player.PlayerFactory;

public class Generation {

	// numero di specie da mantenere a prescindere dal miglioramento di rate
	public static final int BEST_SPECIES_TO_PRESERVE = 2;
	// nel caso di valutazione della crescita, generazione di riferimento
	public static final int COMPARE_AGAINST_GEN = 10;
	// nel caso di valutazione della crescita, tasso di crescita minimo rispetto
	// la generazione di riferimento
	public static final double MIN_GROWTH_RATIO = 1.05;
	// dimensione minima per considerare rilevante una specie
	public static final int MIN_SPECIES_SIZE_TO_BE_RELEVANT = 5;
	// probabilita' di aggiungere un nodo
	public static final double MUTATE_ADD_NODE_PROBABILITY = 0.15;
	// probabilita' di aggiungere una connessione
	public static final double MUTATE_CONNECTION_PROBABILITY = 0.20;
	// probabilita' di abilitare/disabilitare una connessione
	public static final double MUTATE_ENABLE_PROBABILITY = 0.05;
	// probability to remove a node
	public static final double MUTATE_REMOVE_NODE_PROBABILITY = 0;
	// percentuale di geni da eliminare ad ogni nuova generazione
	public static final double REMOVE_LOW_PERFORMANCES_RATIO = 0.1;
	// numero di generazioni entro cui considerare vecchia una specie
	public static final int YOUNG_GEN = 15;

	private PlayerFactory factory;
	private int genNumber;
	private List<Player> players;

	private List<Species> species;

	private UniqueId uniqueId;

	public Generation(UniqueId uniqueId, PlayerFactory factory, int genNumber) {
		this.uniqueId = uniqueId;
		this.genNumber = genNumber;
		this.factory = factory;
		players = new ArrayList<>();
		species = new ArrayList<>();
	}

	public PlayerFactory getFactory() {
		return factory;
	}

	public int getGenNumber() {
		return genNumber;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public List<Species> getSpecies() {
		return species;
	}

	public UniqueId getUniqueId() {
		return uniqueId;
	}

	public void setFactory(PlayerFactory factory) {
		this.factory = factory;
	}

	public void setGenNumber(int genNumber) {
		this.genNumber = genNumber;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public void setSpecies(List<Species> species) {
		this.species = species;
	}

	public void setUniqueId(UniqueId uniqueId) {
		this.uniqueId = uniqueId;
	}

}