package it.vitalegi.neat.impl.service;

import it.vitalegi.neat.impl.Generation;
import it.vitalegi.neat.impl.configuration.NeatConfig;
import it.vitalegi.neat.impl.player.PlayerFactory;

public interface FirstGenerationService {

	Generation create(NeatConfig config, PlayerFactory factory, int inputs, int outputs, int biases, int size);

}