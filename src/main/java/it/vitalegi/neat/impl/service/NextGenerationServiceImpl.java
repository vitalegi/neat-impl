package it.vitalegi.neat.impl.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.Generation;
import it.vitalegi.neat.impl.Random;
import it.vitalegi.neat.impl.Species;
import it.vitalegi.neat.impl.configuration.NeatConfig;
import it.vitalegi.neat.impl.function.SharedFitnessValue;
import it.vitalegi.neat.impl.player.Player;

@Service
public class NextGenerationServiceImpl implements NextGenerationService {

	@Autowired
	GeneMutationService geneMutationService;
	@Autowired
	GenerationService generationService;
	@Autowired
	GeneService geneService;
	Logger log = LoggerFactory.getLogger(NextGenerationServiceImpl.class);
	@Autowired
	SpeciesService speciesService;

	protected List<Player> getBestPlayers(NeatConfig config, Generation gen, Species species) {
		if (isYoung(config, gen, species)) {
			return speciesService.getBestPlayers(species, species.getPlayers().size());
		}
		int newSize = (int) Math.round(species.getPlayers().size() * config.getRemoveLowPerformancesRatio());

		return speciesService.getBestPlayers(species, Math.max(1, newSize));
	}

	protected Player getNextMutatedPlayer(NeatConfig config, Generation gen, Map<Long, List<Player>> selectedPlayers) {
		if (selectedPlayers.values().stream().mapToInt(List::size).sum() == 0) {
			log.error("Non ho altri giocatori disponibili. Generazione {}", gen.getGenNumber());
		}
		List<Player> species = getRandomSpecies(selectedPlayers);
		Player player1 = getRandomPlayer(species);
		Gene newGene1 = geneService.clone(player1.getGene());
		newGene1 = geneMutationService.mutate(config, newGene1);

		if (Random.nextBoolean(0.8)) {
			return gen.getFactory().newPlayer(newGene1);
		}
		Player player2;

		if (Random.nextBoolean(0.03)) {
			if (log.isDebugEnabled()) {
				log.debug("Inter-species offspring");
			}
			player2 = getRandomPlayer(selectedPlayers);
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Intra-species offspring");
			}
			player2 = getRandomPlayer(species);
		}
		Gene newGene2 = geneService.clone(player2.getGene());

		newGene2 = geneMutationService.mutate(config, newGene2);

		Gene offspring = geneMutationService.offspring(newGene1, newGene2);
		return gen.getFactory().newPlayer(offspring);
	}

	protected Player getRandomPlayer(List<Player> players) {
		Map<Long, List<Player>> map = new HashMap<>();
		map.put(1L, players);
		return getRandomPlayer(map);
	}

	protected Player getRandomPlayer(List<Player> players, double[] weights) {

		int selectedIndex = Random.nextRandom(weights);

		return players.get(selectedIndex);
	}

	protected Player getRandomPlayer(Map<Long, List<Player>> players) {
		int size = players.values().stream().mapToInt(List::size).sum();
		double[] weights = new double[size];
		List<Player> flatList = new ArrayList<>(size);

		int index = 0;
		for (List<Player> ps : players.values()) {
			for (Player p : ps) {
				weights[index] = SharedFitnessValue.getFitness(p, ps.size());
				flatList.add(index, p);
				index++;
			}
		}
		return getRandomPlayer(flatList, weights);
	}

	protected List<Player> getRandomSpecies(Map<Long, List<Player>> players) {
		double[] weights = new double[players.size()];
		List<List<Player>> collapse = players.values().stream().collect(Collectors.toList());

		int index = 0;
		for (List<Player> ps : players.values()) {
			for (Player p : ps) {
				weights[index] = SharedFitnessValue.getFitness(p, ps.size());
			}
			collapse.add(ps);
			index++;
		}
		int selectedIndex = Random.nextRandom(weights);

		return collapse.get(selectedIndex);
	}

	protected List<Species> getSpeciesToPreserve(NeatConfig neatConfig, Generation gen) {
		List<Species> speciesToPreserve = new ArrayList<>();
		for (Species s : gen.getSpecies()) {
			if (isPreservableSpecies(neatConfig, gen, s)) {
				if (log.isDebugEnabled()) {
					log.debug("Mantengo la specie {}, che contiene {} geni", s.getId(), s.getPlayers().size());
				}
				speciesToPreserve.add(s);
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Scarto la specie {}, che contiene {} geni", s.getId(), s.getPlayers().size());
				}
			}
		}
		return speciesToPreserve.stream().//
				sorted(Comparator.comparing(Species::getLastFitness).reversed()).//
				collect(Collectors.toList());
	}

	@Override
	public boolean hasMinimumGrowth(NeatConfig neatConfig, Generation gen, Species species) {

		double currFitness = species.getLastFitness();
		double cmpFitness = speciesService.getFitness(species, gen.getGenNumber() - neatConfig.getCompareAgainstGen());
		double ratio = 0;
		if (cmpFitness > 0) {
			ratio = currFitness / cmpFitness;
		} else {
			ratio = 1;
		}

		if (ratio < neatConfig.getMinGrowthRatio()) {
			if (log.isDebugEnabled()) {
				log.debug("Remove species {}. Curr fitness: {}. {} gen ago's fitness: {}. Ratio: {}", species.getId(),
						currFitness, cmpFitness, neatConfig.getCompareAgainstGen(), ratio);
			}
			return false;
		}
		if (log.isDebugEnabled()) {
			log.debug("Specie {} ha crescita minima, la mantengo.", species.getId());
		}
		return true;
	}

	protected boolean isPreservableSpecies(NeatConfig neatConfig, Generation gen, Species s) {
		if (isTopScoreSpecies(neatConfig, gen, s)) {
			return true;
		}
		// first generations, randomize
		if (gen.getGenNumber() < neatConfig.getYoungGen()) {
			return Random.nextBoolean(0.5);
		}
		if (isYoung(neatConfig, gen, s)) {
			return true;
		}
		return hasMinimumGrowth(neatConfig, gen, s);
	}

	protected boolean isTopScoreSpecies(NeatConfig neatConfig, Generation gen, Species species) {
		boolean isTopScore = gen.getSpecies().stream()//
				.sorted(Comparator.comparing(Species::getLastFitness).reversed())//
				.limit(neatConfig.getBestSpeciesToPreserve())//
				.anyMatch(b -> b.getId() == species.getId());

		if (log.isDebugEnabled()) {
			log.debug("Specie {} e' tra le migliori {}, la mantengo.", species.getId(),
					neatConfig.getBestSpeciesToPreserve());
		}
		return isTopScore;
	}

	protected boolean isYoung(NeatConfig neatConfig, Generation gen, Species species) {

		boolean young = gen.getGenNumber() - species.getStartGeneration() <= neatConfig.getYoungGen();
		if (log.isDebugEnabled()) {
			log.debug("Specie {} giovane? {}", species.getId(), young);
		}
		return young;
	}

	@Override
	public Generation nextGeneration(NeatConfig config, Generation gen) {
		Generation nextGen = new Generation(gen.getUniqueId(), gen.getFactory(), gen.getGenNumber() + 1);
		gen.getUniqueId().clearConnectionIds();
		List<Species> speciesToPreserve = getSpeciesToPreserve(config, gen);

		for (Species s : speciesToPreserve) {
			preserveSpecies(config, nextGen, s, speciesService.getChampion(s).getGene());
		}

		Map<Long, List<Player>> selectedPlayers = new HashMap<>();

		speciesToPreserve.forEach(s -> {
			selectedPlayers.put(s.getId(), new ArrayList<>());
			getBestPlayers(config, gen, s)//
					.forEach(p -> selectedPlayers.get(s.getId()).add(p));
		});

		while (nextGen.getPlayers().size() != gen.getPlayers().size()) {
			generationService.addPlayer(nextGen, getNextMutatedPlayer(config, nextGen, selectedPlayers));
		}

		return nextGen;
	}

	protected Species preserveSpecies(NeatConfig config, Generation gen, Species speciesToPreserve, Gene champion) {

		Species nextSpeciesGen = speciesService.newInstance(//
				speciesToPreserve.getId(), speciesToPreserve.getStartGeneration());

		nextSpeciesGen.getHistoryBestFitnesses().addAll(speciesToPreserve.getHistoryBestFitnesses());

		generationService.addSpecies(gen, nextSpeciesGen);
		if (speciesService.isRelevantSpecies(config, speciesToPreserve)) {
			if (log.isDebugEnabled()) {
				log.debug("Specie {} ha rappresentanza minima, copio campione.", speciesToPreserve.getId());
			}
			generationService.addPlayer(gen, gen.getFactory().newPlayer(geneService.clone(champion)), nextSpeciesGen);
		} else if (isTopScoreSpecies(config, gen, speciesToPreserve)) {
			if (log.isDebugEnabled()) {
				log.debug("Specie {} e' tra le migliori, copio campione.", speciesToPreserve.getId());
			}
			generationService.addPlayer(gen, gen.getFactory().newPlayer(geneService.clone(champion)), nextSpeciesGen);
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Specie {} NON ha rappresentanza minima, muto campione.", speciesToPreserve.getId());
			}
			Gene cloned = geneService.clone(champion);
			Gene mutated = geneMutationService.mutate(config, cloned);
			generationService.addPlayer(gen, gen.getFactory().newPlayer(mutated), nextSpeciesGen);
		}
		return nextSpeciesGen;
	}

	public void setGeneMutationService(GeneMutationService geneMutationService) {
		this.geneMutationService = geneMutationService;
	}

	public void setGenerationService(GenerationService generationService) {
		this.generationService = generationService;
	}

	public void setGeneService(GeneService geneService) {
		this.geneService = geneService;
	}

	public void setSpeciesService(SpeciesService speciesService) {
		this.speciesService = speciesService;
	}

}
