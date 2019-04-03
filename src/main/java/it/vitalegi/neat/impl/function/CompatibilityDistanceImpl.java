package it.vitalegi.neat.impl.function;

import it.vitalegi.neat.impl.Gene;

public class CompatibilityDistanceImpl implements CompatibilityDistance {

	protected double c1;
	protected double c2;
	protected double deltaT;

	public CompatibilityDistanceImpl() {
		super();
	}

	public CompatibilityDistanceImpl(double deltaT, double c1, double c2) {
		this.c1 = c1;
		this.c2 = c2;
		this.deltaT = deltaT;
	}

	public double getC1() {
		return c1;
	}

	public double getC2() {
		return c2;
	}

	@Override
	public double getDistance(Gene gene1, Gene gene2) {
		int matchingGenes = gene1.getMatchingGenesCount(gene2);
		int disjointGenes = gene1.getDisjointGenesCount(gene2);
		double avgWeightDiff = gene1.getAvgWeightDifference(gene2, matchingGenes);

		int n = Math.max(gene1.getSize(), gene2.getSize());
		return getDistance(n, disjointGenes, avgWeightDiff);
	}

	protected double getDistance(int n, int disjointGenes, double avgWeightDiff) {
		if (n == 0) {
			return c2 * avgWeightDiff;
		} else {
			return c1 * disjointGenes / n + c2 * avgWeightDiff;
		}
	}

	@Override
	public boolean isCompatible(Gene gene1, Gene gene2) {
		return getDistance(gene1, gene2) < deltaT;
	}

	public void setC1(double c1) {
		this.c1 = c1;
	}

	public void setC2(double c2) {
		this.c2 = c2;
	}

}
