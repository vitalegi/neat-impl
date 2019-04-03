package it.vitalegi.neat.impl.analysis;

import java.util.ArrayList;
import java.util.List;

import it.vitalegi.neat.impl.Generation;
import it.vitalegi.neat.impl.util.TablePrinter;

public class EvolutionAnalysis {

	private List<Generation> generations;

	public EvolutionAnalysis() {
		generations = new ArrayList<>();
	}

	public void add(Generation entry) {
		generations.add(entry);
	}

	public String getAnalysis() {
		TablePrinter printer = TablePrinter.newPrinter();
		printer.setHeaders(GenerationEntry.getTextAnalysisHeaders());

		List<GenerationEntry> analyzed = new ArrayList<>();
		Generation last = null;
		for (Generation gen : generations) {
			analyzed.add(GenerationEntry.newInstance(last, gen));
			last = gen;
		}

		analyzed.stream().map(GenerationEntry::getTextAnalysis).forEach(printer::addRow);
		return printer.print().toString();
	}

	public List<Generation> getGenerations() {
		return generations;
	}

	public void setGenerations(List<Generation> generations) {
		this.generations = generations;
	}

}
