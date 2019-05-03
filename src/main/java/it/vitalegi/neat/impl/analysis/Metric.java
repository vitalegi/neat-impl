package it.vitalegi.neat.impl.analysis;

import it.vitalegi.neat.impl.Generation;

public interface Metric {

	public Number calculate(Generation prev, Generation curr);
}
