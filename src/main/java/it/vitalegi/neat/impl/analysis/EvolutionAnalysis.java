package it.vitalegi.neat.impl.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;

import it.vitalegi.neat.impl.Connection;
import it.vitalegi.neat.impl.Generation;
import it.vitalegi.neat.impl.player.Player;
import it.vitalegi.neat.impl.util.Pair;
import it.vitalegi.neat.impl.util.StringUtil;
import it.vitalegi.neat.impl.util.TablePrinter;

public class EvolutionAnalysis {

	private List<Generation> generations;

	public EvolutionAnalysis() {
		generations = new ArrayList<>();
	}

	public void add(Generation entry) {
		generations.add(entry);
	}

	public void logAnalysis(Logger log) {
		List<Pair<String, Metric>> cols = GenerationEntry.getColumns();

		TablePrinter printer = TablePrinter.newPrinter();

		List<String> headers = new ArrayList<>();
		for (Pair<String, Metric> col : cols) {
			headers.add(col.getFirst());
		}
		printer.setHeaders(headers);

		Generation last = null;
		for (Generation gen : generations) {
			List<String> row = new ArrayList<>();
			for (Pair<String, Metric> col : cols) {
				Number n = col.getSecond().calculate(last, gen);
				String v;
				if (n instanceof Double) {
					v = StringUtil.format((double) n);
				} else {
					v = String.valueOf(n);
				}
				row.add(v);
			}
			printer.addRow(row);
			last = gen;
		}
		printer.log(log);
	}

	public void getNetworks(double[] inputs, double[] biases, Logger log) {
		TablePrinter printer = TablePrinter.newPrinter();
		printer.setHeaders(Arrays.asList("Gen", "Score", "Graphs", "Status (1-1)"));

		for (Generation gen : generations) {
			for (int i = 0; i < gen.getPlayers().size(); i++) {
				Player p = gen.getPlayers().get(i);
				printer.addRow(Arrays.asList(//
						String.valueOf(gen.getGenNumber()), //
						StringUtil.format(p.getFitness()), //
						p.getGene().stringify(true, false, false, //
								Comparator.comparing(Connection::getFromNodeId).thenComparing(Connection::getToNodeId)), //
						p.feedForwardEndStatus(inputs, biases)));
			}
		}
		printer.log(log);
	}

	public List<Generation> getGenerations() {
		return generations;
	}

	public void setGenerations(List<Generation> generations) {
		this.generations = generations;
	}
}
