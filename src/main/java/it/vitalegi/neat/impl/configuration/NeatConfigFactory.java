package it.vitalegi.neat.impl.configuration;

public class NeatConfigFactory {

	public static NeatConfigFactory create() {
		NeatConfigFactory factory = new NeatConfigFactory();
		factory.neatConfig = new NeatConfigImpl();

		factory.mutateProbability(0.8)//
				.uniformPerturbationProbability(0.9)//
				.uniformPerturbation(0.1)//
				.bestSpeciesToPreserve(2)//
				.compareAgainstGen(10)//
				.minGrowthRatio(1.05)//
				.minSpeciesSizeToBeRelevant(5)//
				.mutateAddNodeProbability(0.15)//
				.mutateConnectionProbability(0.20)//
				.mutateEnableProbability(0.05)//
				.removeLowPerformancesRatio(0.1)//
				.youngGen(15)//
				.maxWeight(10.0)//
				.minWeight(-10.0);

		return factory;
	}

	NeatConfigImpl neatConfig;

	public NeatConfigFactory bestSpeciesToPreserve(int bestSpeciesToPreserve) {
		neatConfig.setBestSpeciesToPreserve(bestSpeciesToPreserve);
		return this;
	}

	public NeatConfig build() {
		return neatConfig;
	}

	public NeatConfigFactory compareAgainstGen(int compareAgainstGen) {
		neatConfig.setCompareAgainstGen(compareAgainstGen);
		return this;
	}

	public NeatConfigFactory maxWeight(double maxWeight) {
		neatConfig.setMaxWeight(maxWeight);
		return this;
	}

	public NeatConfigFactory minGrowthRatio(double minGrowthRatio) {
		neatConfig.setMinGrowthRatio(minGrowthRatio);
		return this;
	}

	public NeatConfigFactory minSpeciesSizeToBeRelevant(int minSpeciesSizeToBeRelevant) {
		neatConfig.setMinSpeciesSizeToBeRelevant(minSpeciesSizeToBeRelevant);
		return this;
	}

	public NeatConfigFactory minWeight(double minWeight) {
		neatConfig.setMinWeight(minWeight);
		return this;
	}

	public NeatConfigFactory mutateAddNodeProbability(double mutateAddNodeProbability) {
		neatConfig.setMutateAddNodeProbability(mutateAddNodeProbability);
		return this;
	}

	public NeatConfigFactory mutateConnectionProbability(double mutateConnectionProbability) {
		neatConfig.setMutateConnectionProbability(mutateConnectionProbability);
		return this;
	}

	public NeatConfigFactory mutateEnableProbability(double mutateEnableProbability) {
		neatConfig.setMutateEnableProbability(mutateEnableProbability);
		return this;
	}

	public NeatConfigFactory mutateProbability(double mutateProbability) {
		neatConfig.setMutateProbability(mutateProbability);
		return this;
	}

	public NeatConfigFactory removeLowPerformancesRatio(double removeLowPerformancesRatio) {
		neatConfig.setRemoveLowPerformancesRatio(removeLowPerformancesRatio);
		return this;
	}

	public NeatConfigFactory uniformPerturbation(double uniformPerturbation) {
		neatConfig.setUniformPerturbation(uniformPerturbation);
		return this;
	}

	public NeatConfigFactory uniformPerturbationProbability(double uniformPerturbationProbability) {
		neatConfig.setUniformPerturbationProbability(uniformPerturbationProbability);
		return this;
	}

	public NeatConfigFactory youngGen(int youngGen) {
		neatConfig.setYoungGen(youngGen);
		return this;
	}
}
