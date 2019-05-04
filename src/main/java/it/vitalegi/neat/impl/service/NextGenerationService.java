package it.vitalegi.neat.impl.service;

import it.vitalegi.neat.impl.Generation;
import it.vitalegi.neat.impl.Species;
import it.vitalegi.neat.impl.configuration.NeatConfig;

public interface NextGenerationService {

	boolean hasMinimumGrowth(NeatConfig neatConfig, Generation gen, Species species);

	Generation nextGeneration(NeatConfig config, Generation gen);

}