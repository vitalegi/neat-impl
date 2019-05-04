package it.vitalegi.neat.impl.service;

import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.configuration.NeatConfig;

public interface GeneMutationService {

	Gene mutate(NeatConfig neatConfig, Gene gene);

	Gene mutateAddRandomConnection(NeatConfig neatConfig, Gene gene);

	Gene offspring(Gene gene1, Gene gene2);

}