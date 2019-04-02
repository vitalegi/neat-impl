package it.vitalegi.neat.impl.analysis;

import java.util.ArrayList;
import java.util.List;

import it.vitalegi.neat.impl.Generation;
import it.vitalegi.neat.impl.util.TablePrinter;

public class EvolutionAnalysis {

	private List<GenerationEntry> generations;

	public EvolutionAnalysis() {
		generations = new ArrayList<>();
	}

	public void add(Generation entry) {
		generations.add(GenerationEntry.newInstance(entry));
	}

	public String getAnalysis() {
		TablePrinter printer = TablePrinter.newPrinter();
		printer.setHeaders(GenerationEntry.getTextAnalysisHeaders());
		generations.stream().map(GenerationEntry::getTextAnalysis).forEach(printer::addRow);
		return printer.print().toString();
	}

}
