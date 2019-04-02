package it.vitalegi.neat.impl.function;

import java.util.List;

import it.vitalegi.neat.impl.Player;

public class SharedFitnessValue {

	public static double getFitness(Player player, int speciesSize) {
		double fi = player.getFitness();

		return fi / speciesSize;
	}

	public static double getFitness(CompatibilityDistance compatibilityDistance, List<Player> players, int i) {
		Player player = players.get(i);
		double fi = player.getFitness();

		double sumSharingFunction = 0;
		for (int j = 0; j < players.size(); j++) {
			sumSharingFunction += sharingFunction(compatibilityDistance, player, players.get(j));
		}
		// p(i) e' compatibile con se' stesso, quindi sum >= 1
		return fi / sumSharingFunction;
	}

	protected static double sharingFunction(CompatibilityDistance compatibilityDistance, Player player1,
			Player player2) {
		if (compatibilityDistance.isCompatible(player1.getGene(), player2.getGene())) {
			return 1;
		}
		return 0;
	}
}
