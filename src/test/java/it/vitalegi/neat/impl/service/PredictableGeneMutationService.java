package it.vitalegi.neat.impl.service;

import it.vitalegi.neat.impl.configuration.NeatConfig;

public class PredictableGeneMutationService extends GeneMutationServiceImpl {

	boolean isMutateWeights;
	boolean isUniformPerturbation;
	boolean isMutateAddRandomNode;
	boolean isMutateAddRandomConnection;
	boolean isMutateChangeRandomEnableConnection;

	public PredictableGeneMutationService uniformWeightsPerturbation() {
		isMutateWeights = true;
		isUniformPerturbation = true;
		return this;
	}

	public PredictableGeneMutationService randomWeightsPerturbation() {
		isMutateWeights = true;
		isUniformPerturbation = true;
		return this;
	}

	@Override
	protected boolean isMutateWeights(NeatConfig neatConfig) {
		return isMutateWeights;
	}

	@Override
	protected boolean isMutateAddRandomNode(NeatConfig neatConfig) {
		return isMutateAddRandomNode;
	}

	@Override
	protected boolean isMutateAddRandomConnection(NeatConfig neatConfig) {
		return isMutateAddRandomConnection;
	}

	@Override
	protected boolean isMutateChangeRandomEnableConnection(NeatConfig neatConfig) {
		return isMutateChangeRandomEnableConnection;
	}

	protected boolean isUniformPerturbation(NeatConfig neatConfig) {
		return isUniformPerturbation;
	}
}