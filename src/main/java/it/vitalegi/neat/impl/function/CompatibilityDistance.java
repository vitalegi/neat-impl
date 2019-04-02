package it.vitalegi.neat.impl.function;

import it.vitalegi.neat.impl.Gene;

public interface CompatibilityDistance {

	public double getDistance(Gene gene1, Gene gene2);

	public boolean isCompatible(Gene gene1, Gene gene2);
}