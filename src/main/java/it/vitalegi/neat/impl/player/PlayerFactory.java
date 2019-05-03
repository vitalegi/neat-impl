package it.vitalegi.neat.impl.player;

import it.vitalegi.neat.impl.Gene;

public interface PlayerFactory {

	Player newPlayer(Gene gene);

}