package it.vitalegi.neat.impl;

import java.util.ArrayList;
import java.util.List;

import it.vitalegi.neat.impl.player.Player;
import it.vitalegi.neat.impl.player.PlayerFactory;

public class Generation {

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