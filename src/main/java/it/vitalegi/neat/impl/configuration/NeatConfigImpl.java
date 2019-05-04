package it.vitalegi.neat.impl.configuration;

public class NeatConfigImpl implements NeatConfig {

	/**
	 * numero di specie da mantenere a prescindere dal miglioramento di rate
	 */
	protected int bestSpeciesToPreserve;
	/**
	 * nel caso di valutazione della crescita, generazione di riferimento
	 */
	protected int compareAgainstGen;
	/**
	 * max node weight
	 */
	protected double maxWeight;

	/**
	 * nel caso di valutazione della crescita, tasso di crescita minimo rispetto la
	 * generazione di riferimento
	 */
	protected double minGrowthRatio;
	/**
	 * dimensione minima per considerare rilevante una specie
	 */
	protected int minSpeciesSizeToBeRelevant;
	/**
	 * min node weight
	 */
	protected double minWeight;
	/**
	 * probabilita' di aggiungere un nodo
	 */
	protected double mutateAddNodeProbability;
	/**
	 * probabilita' di aggiungere una connessione
	 */
	protected double mutateConnectionProbability;
	/**
	 * probabilita' di abilitare/disabilitare una connessione
	 */
	protected double mutateEnableProbability;
	/**
	 * Probability to mutate a gene
	 */
	protected double mutateProbability;
	/**
	 * percentuale di geni da eliminare ad ogni nuova generazione
	 */
	protected double removeLowPerformancesRatio;
	/**
	 * Range of the uniform perturbation
	 */
	protected double uniformPerturbation;
	/**
	 * Probability to mutate each node in a gene in a small range
	 */
	protected double uniformPerturbationProbability;
	/**
	 * numero di generazioni entro cui considerare vecchia una specie
	 */
	protected int youngGen;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * it.vitalegi.neat.impl.configuration.NeatConfig#getBestSpeciesToPreserve()
	 */
	@Override
	public int getBestSpeciesToPreserve() {
		return bestSpeciesToPreserve;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see it.vitalegi.neat.impl.configuration.NeatConfig#getCompareAgainstGen()
	 */
	@Override
	public int getCompareAgainstGen() {
		return compareAgainstGen;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see it.vitalegi.neat.impl.configuration.NeatConfig#getMaxWeight()
	 */
	@Override
	public double getMaxWeight() {
		return maxWeight;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see it.vitalegi.neat.impl.configuration.NeatConfig#getMinGrowthRatio()
	 */
	@Override
	public double getMinGrowthRatio() {
		return minGrowthRatio;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * it.vitalegi.neat.impl.configuration.NeatConfig#getMinSpeciesSizeToBeRelevant(
	 * )
	 */
	@Override
	public int getMinSpeciesSizeToBeRelevant() {
		return minSpeciesSizeToBeRelevant;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see it.vitalegi.neat.impl.configuration.NeatConfig#getMinWeight()
	 */
	@Override
	public double getMinWeight() {
		return minWeight;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * it.vitalegi.neat.impl.configuration.NeatConfig#getMutateAddNodeProbability()
	 */
	@Override
	public double getMutateAddNodeProbability() {
		return mutateAddNodeProbability;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * it.vitalegi.neat.impl.configuration.NeatConfig#getMutateConnectionProbability
	 * ()
	 */
	@Override
	public double getMutateConnectionProbability() {
		return mutateConnectionProbability;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * it.vitalegi.neat.impl.configuration.NeatConfig#getMutateEnableProbability()
	 */
	@Override
	public double getMutateEnableProbability() {
		return mutateEnableProbability;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see it.vitalegi.neat.impl.configuration.NeatConfig#getMutateProbability()
	 */
	@Override
	public double getMutateProbability() {
		return mutateProbability;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * it.vitalegi.neat.impl.configuration.NeatConfig#getRemoveLowPerformancesRatio(
	 * )
	 */
	@Override
	public double getRemoveLowPerformancesRatio() {
		return removeLowPerformancesRatio;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see it.vitalegi.neat.impl.configuration.NeatConfig#getUniformPerturbation()
	 */
	@Override
	public double getUniformPerturbation() {
		return uniformPerturbation;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see it.vitalegi.neat.impl.configuration.NeatConfig#
	 * getUniformPerturbationProbability()
	 */
	@Override
	public double getUniformPerturbationProbability() {
		return uniformPerturbationProbability;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see it.vitalegi.neat.impl.configuration.NeatConfig#getYoungGen()
	 */
	@Override
	public int getYoungGen() {
		return youngGen;
	}

	public void setBestSpeciesToPreserve(int bestSpeciesToPreserve) {
		this.bestSpeciesToPreserve = bestSpeciesToPreserve;
	}

	public void setCompareAgainstGen(int compareAgainstGen) {
		this.compareAgainstGen = compareAgainstGen;
	}

	public void setMaxWeight(double maxWeight) {
		this.maxWeight = maxWeight;
	}

	public void setMinGrowthRatio(double minGrowthRatio) {
		this.minGrowthRatio = minGrowthRatio;
	}

	public void setMinSpeciesSizeToBeRelevant(int minSpeciesSizeToBeRelevant) {
		this.minSpeciesSizeToBeRelevant = minSpeciesSizeToBeRelevant;
	}

	public void setMinWeight(double minWeight) {
		this.minWeight = minWeight;
	}

	public void setMutateAddNodeProbability(double mutateAddNodeProbability) {
		this.mutateAddNodeProbability = mutateAddNodeProbability;
	}

	public void setMutateConnectionProbability(double mutateConnectionProbability) {
		this.mutateConnectionProbability = mutateConnectionProbability;
	}

	public void setMutateEnableProbability(double mutateEnableProbability) {
		this.mutateEnableProbability = mutateEnableProbability;
	}

	public void setMutateProbability(double mutateProbability) {
		this.mutateProbability = mutateProbability;
	}

	public void setRemoveLowPerformancesRatio(double removeLowPerformancesRatio) {
		this.removeLowPerformancesRatio = removeLowPerformancesRatio;
	}

	public void setUniformPerturbation(double uniformPerturbation) {
		this.uniformPerturbation = uniformPerturbation;
	}

	public void setUniformPerturbationProbability(double uniformPerturbationProbability) {
		this.uniformPerturbationProbability = uniformPerturbationProbability;
	}

	public void setYoungGen(int youngGen) {
		this.youngGen = youngGen;
	}

}
