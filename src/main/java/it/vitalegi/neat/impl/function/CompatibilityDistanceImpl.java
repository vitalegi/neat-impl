package it.vitalegi.neat.impl.function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.service.GeneServiceImpl;

@Service
public class CompatibilityDistanceImpl implements CompatibilityDistance {

	protected double c1;

	protected double c2;
	protected double deltaT;
	@Autowired
	GeneServiceImpl geneService;

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

	public double getDeltaT() {
		return deltaT;
	}

	@Override
	public double getDistance(Gene gene1, Gene gene2) {
		int matchingGenes = geneService.getMatchingGenesCount(gene1, gene2);
		int disjointGenes = geneService.getDisjointGenesCount(gene1, gene2);
		double avgWeightDiff = geneService.getAvgWeightDifference(gene1, gene2, matchingGenes);

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

	public void setDeltaT(double deltaT) {
		this.deltaT = deltaT;
	}

	public void setGeneService(GeneServiceImpl geneService) {
		this.geneService = geneService;
	}

}
