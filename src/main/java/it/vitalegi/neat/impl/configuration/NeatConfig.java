package it.vitalegi.neat.impl.configuration;

public interface NeatConfig {

	int getBestSpeciesToPreserve();

	int getCompareAgainstGen();

	double getMaxWeight();

	double getMinGrowthRatio();

	int getMinSpeciesSizeToBeRelevant();

	double getMinWeight();

	double getMutateAddNodeProbability();

	double getMutateConnectionProbability();

	double getMutateEnableProbability();

	double getMutateProbability();

	double getMutateRemoveNodeProbability();

	double getRemoveLowPerformancesRatio();

	double getUniformPerturbation();

	double getUniformPerturbationProbability();

	int getYoungGen();

}