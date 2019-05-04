package it.vitalegi.neat.impl;

import java.util.ArrayList;
import java.util.List;

import it.vitalegi.neat.impl.player.Player;

public class Species {

	protected List<Double> historyBestFitnesses;
	protected long id;

	protected List<Player> players;

	protected int startGeneration;

	public Species() {
	}

	public Species(long id, int startGeneration) {
		this.id = id;
		this.startGeneration = startGeneration;
		historyBestFitnesses = new ArrayList<>();
		players = new ArrayList<>();
	}

	public void addFitness(double fitness) {
		historyBestFitnesses.add(fitness);
	}

	public void addPlayer(Player player) {
		players.add(player);
	}

	public List<Double> getHistoryBestFitnesses() {
		return historyBestFitnesses;
	}

	public long getId() {
		return id;
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

	public double getLastFitness() {
		if (getHistoryBestFitnesses().isEmpty()) {
			return 0;
		}
		return getHistoryBestFitnesses().get(getHistoryBestFitnesses().size() - 1);
	}

}
