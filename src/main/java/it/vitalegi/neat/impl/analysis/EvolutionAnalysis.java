package it.vitalegi.neat.impl.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.vitalegi.neat.impl.Connection;
import it.vitalegi.neat.impl.Generation;
import it.vitalegi.neat.impl.player.Player;
import it.vitalegi.neat.impl.service.GeneService;
import it.vitalegi.neat.impl.util.Pair;
import it.vitalegi.neat.impl.util.StringUtil;
import it.vitalegi.neat.impl.util.TablePrinter;

@Service
public class EvolutionAnalysis {

	@Autowired
	GenerationEntry generationEntry;

	@Autowired
	GeneService geneService;

	public void getNetworks(List<Generation> generations, Logger log) {
		TablePrinter printer = TablePrinter.newPrinter();
		printer.setHeaders(Arrays.asList("Gen", "Score", "Graphs"));

		for (Generation gen : generations) {
			for (int i = 0; i < gen.getPlayers().size(); i++) {
				Player p = gen.getPlayers().get(i);
				printer.addRow(Arrays.asList(//
						String.valueOf(gen.getGenNumber()), //
						StringUtil.format(p.getFitness()), //
						geneService.stringify(p.getGene(), true, false, false, //
								Comparator.comparing(Connection::getFromNodeId).thenComparing(Connection::getToNodeId))//
				));
			}
		}
		printer.log(log);
	}

	public void logAnalysis(List<Generation> generations, Logger log) {
		List<Pair<String, Metric>> cols = generationEntry.getColumns();

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

	public void setGenerationEntry(GenerationEntry generationEntry) {
		this.generationEntry = generationEntry;
	}

	public void setGeneService(GeneService geneService) {
		this.geneService = geneService;
	}
}
