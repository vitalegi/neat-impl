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
import it.vitalegi.neat.impl.UniqueId;
import it.vitalegi.neat.impl.function.SharedFitnessValue;
import it.vitalegi.neat.impl.player.Player;
import it.vitalegi.neat.impl.player.PlayerFactory;

@Service
public class GenerationServiceImpl {

	Logger log = LoggerFactory.getLogger(GenerationServiceImpl.class);

	@Autowired
	SpeciesServiceImpl speciesService;
	@Autowired
	GeneServiceImpl geneService;

	public Generation createGen0(PlayerFactory factory, int inputs, int outputs, int biases, int size) {
		Generation gen = new Generation(new UniqueId(), factory, 0);

		long[] inputIds = new long[inputs];
		for (int i = 0; i < inputIds.length; i++) {
			inputIds[i] = gen.getUniqueId().nextNodeId();
		}
		long[] outputIds = new long[outputs];
		for (int i = 0; i < outputIds.length; i++) {
			outputIds[i] = gen.getUniqueId().nextNodeId();
		}
		long[] biasIds = new long[biases];
		for (int i = 0; i < biasIds.length; i++) {
			biasIds[i] = gen.getUniqueId().nextNodeId();
		}
		for (int i = 0; i < size; i++) {

			Gene gene = geneService.newInstance(gen.getUniqueId(), gen.getUniqueId().nextNodeId(), inputIds, outputIds,
					biasIds);
			gene = geneService.mutateAddRandomConnection(gene);
			gene = geneService.mutate(gene, Generation.MUTATE_ADD_NODE_PROBABILITY,
					Generation.MUTATE_REMOVE_NODE_PROBABILITY, Generation.MUTATE_CONNECTION_PROBABILITY,
					Generation.MUTATE_ENABLE_PROBABILITY);

			Player player = factory.newPlayer(gene);
			addPlayer(gen, player);
		}
		return gen;
	}

	public Species addPlayer(Generation gen, Player player) {
		Species compatible = getCompatibleSpecies(gen, player);
		if (compatible == null) {
			compatible = speciesService.newInstance(gen.getUniqueId().nextSpeciesId(), gen.getGenNumber());
			addSpecies(gen, compatible);
		}
		addPlayer(gen, player, compatible);
		return compatible;
	}

	protected void addPlayer(Generation gen, Player player, Species species) {
		gen.getPlayers().add(player);
		species.addPlayer(player);
	}

	protected void addSpecies(Generation gen, Species species) {
		gen.getSpecies().add(species);
	}

	public void computeFitnesses(Generation gen) {
		gen.getSpecies().forEach(s -> {
			s.addFitness(speciesService.getChampion(s).getFitness());
		});
	}

	protected List<Player> getBestPlayers(Generation gen, Species species) {
		if (isYoung(gen, species)) {
			return speciesService.getBestPlayers(species, species.getPlayers().size());
		}
		int newSize = (int) Math.round(species.getPlayers().size() * Generation.REMOVE_LOW_PERFORMANCES_RATIO);

		return speciesService.getBestPlayers(species, Math.max(1, newSize));
	}

	protected Species getCompatibleSpecies(Generation gen, Player player) {
		for (Species species : gen.getSpecies()) {
			if (speciesService.isCompatible(species, player)) {
				return species;
			}
		}
		return null;
	}

	protected Player getNextMutatedPlayer(Generation gen, Map<Long, List<Player>> selectedPlayers) {
		if (selectedPlayers.values().stream().mapToInt(List::size).sum() == 0) {
			log.error("Non ho altri giocatori disponibili. Generazione {}", gen.getGenNumber());
		}
		List<Player> species = getRandomSpecies(selectedPlayers);
		Player player1 = getRandomPlayer(species);
		Gene newGene1 = geneService.clone(player1.getGene());
		newGene1 = geneService.mutate(newGene1, Generation.MUTATE_ADD_NODE_PROBABILITY,
				Generation.MUTATE_REMOVE_NODE_PROBABILITY, Generation.MUTATE_CONNECTION_PROBABILITY,
				Generation.MUTATE_ENABLE_PROBABILITY);

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

		newGene2 = geneService.mutate(newGene2, Generation.MUTATE_ADD_NODE_PROBABILITY,
				Generation.MUTATE_REMOVE_NODE_PROBABILITY, Generation.MUTATE_CONNECTION_PROBABILITY,
				Generation.MUTATE_ENABLE_PROBABILITY);

		Gene offspring = geneService.offspring(newGene1, newGene2);
		return gen.getFactory().newPlayer(offspring);
	}

	protected Player getRandomPlayer(List<Player> players, double[] weights) {

		int selectedIndex = Random.nextRandom(weights);

		return players.get(selectedIndex);
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

	protected Player getRandomPlayer(List<Player> players) {
		Map<Long, List<Player>> map = new HashMap<>();
		map.put(1L, players);
		return getRandomPlayer(map);
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

	protected Species getSpeciesFromPlayer(Generation gen, Player player) {
		for (Species s : gen.getSpecies()) {
			if (speciesService.getPlayerByGeneId(s, player.getGeneId()) != null) {
				return s;
			}
		}
		return null;
	}

	protected List<Species> getSpeciesToPreserve(Generation gen) {
		List<Species> speciesToPreserve = new ArrayList<>();
		for (Species s : gen.getSpecies()) {
			if (isPreservableSpecies(gen, s)) {
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

	protected boolean isPreservableSpecies(Generation gen, Species s) {
		if (isTopScoreSpecies(gen, s)) {
			return true;
		}
		// first generations, randomize
		if (gen.getGenNumber() < Generation.YOUNG_GEN) {
			return Random.nextBoolean(0.5);
		}
		if (isYoung(gen, s)) {
			return true;
		}
		return hasMinimumGrowth(gen, s);
	}

	public boolean hasMinimumGrowth(Generation gen, Species species) {

		double currFitness = species.getLastFitness();
		double cmpFitness = speciesService.getFitness(species, gen.getGenNumber() - Generation.COMPARE_AGAINST_GEN);
		double ratio = 0;
		if (cmpFitness > 0) {
			ratio = currFitness / cmpFitness;
		} else {
			ratio = 1;
		}

		if (ratio < Generation.MIN_GROWTH_RATIO) {
			if (log.isDebugEnabled()) {
				log.debug("Remove species {}. Curr fitness: {}. {} gen ago's fitness: {}. Ratio: {}", species.getId(),
						currFitness, cmpFitness, Generation.COMPARE_AGAINST_GEN, ratio);
			}
			return false;
		}
		if (log.isDebugEnabled()) {
			log.debug("Specie {} ha crescita minima, la mantengo.", species.getId());
		}
		return true;
	}

	protected boolean isTopScoreSpecies(Generation gen, Species species) {
		boolean isTopScore = gen.getSpecies().stream()//
				.sorted(Comparator.comparing(Species::getLastFitness).reversed())//
				.limit(Generation.BEST_SPECIES_TO_PRESERVE)//
				.anyMatch(b -> b.getId() == species.getId());

		if (log.isDebugEnabled()) {
			log.debug("Specie {} e' tra le migliori {}, la mantengo.", species.getId(),
					Generation.BEST_SPECIES_TO_PRESERVE);
		}
		return isTopScore;
	}

	protected boolean isYoung(Generation gen, Species species) {

		boolean young = gen.getGenNumber() - species.getStartGeneration() <= Generation.YOUNG_GEN;
		if (log.isDebugEnabled()) {
			log.debug("Specie {} giovane? {}", species.getId(), young);
		}
		return young;
	}

	public Generation nextGeneration(Generation gen) {
		Generation nextGen = new Generation(gen.getUniqueId(), gen.getFactory(), gen.getGenNumber() + 1);
		gen.getUniqueId().clearConnectionIds();
		List<Species> speciesToPreserve = getSpeciesToPreserve(gen);

		for (Species s : speciesToPreserve) {
			preserveSpecies(nextGen, s, speciesService.getChampion(s).getGene());
		}

		Map<Long, List<Player>> selectedPlayers = new HashMap<>();

		speciesToPreserve.forEach(s -> {
			selectedPlayers.put(s.getId(), new ArrayList<>());
			getBestPlayers(gen, s)//
					.forEach(p -> selectedPlayers.get(s.getId()).add(p));
		});

		while (nextGen.getPlayers().size() != gen.getPlayers().size()) {
			addPlayer(nextGen, getNextMutatedPlayer(nextGen, selectedPlayers));
		}

		return nextGen;
	}

	protected Species preserveSpecies(Generation gen, Species speciesToPreserve, Gene champion) {

		Species nextSpeciesGen = speciesService.newInstance(//
				speciesToPreserve.getId(), speciesToPreserve.getStartGeneration());

		nextSpeciesGen.getHistoryBestFitnesses().addAll(speciesToPreserve.getHistoryBestFitnesses());

		addSpecies(gen, nextSpeciesGen);
		if (speciesService.isRelevantSpecies(speciesToPreserve)) {
			if (log.isDebugEnabled()) {
				log.debug("Specie {} ha rappresentanza minima, copio campione.", speciesToPreserve.getId());
			}
			addPlayer(gen, gen.getFactory().newPlayer(geneService.clone(champion)), nextSpeciesGen);
		} else if (isTopScoreSpecies(gen, speciesToPreserve)) {
			if (log.isDebugEnabled()) {
				log.debug("Specie {} e' tra le migliori, copio campione.", speciesToPreserve.getId());
			}
			addPlayer(gen, gen.getFactory().newPlayer(geneService.clone(champion)), nextSpeciesGen);
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Specie {} NON ha rappresentanza minima, muto campione.", speciesToPreserve.getId());
			}
			Gene cloned = geneService.clone(champion);
			Gene mutated = geneService.mutate(cloned, Generation.MUTATE_ADD_NODE_PROBABILITY,
					Generation.MUTATE_REMOVE_NODE_PROBABILITY, Generation.MUTATE_CONNECTION_PROBABILITY,
					Generation.MUTATE_ENABLE_PROBABILITY);
			addPlayer(gen, gen.getFactory().newPlayer(mutated), nextSpeciesGen);
		}
		return nextSpeciesGen;
	}

	public String stringify(Generation gen) {
		StringBuilder sb = new StringBuilder();
		sb.append("GEN: " + gen.getGenNumber() + "\n");
		sb.append("Species: " + gen.getSpecies().size() + "\n");
		gen.getSpecies().forEach(s -> {
			sb.append(" - ").//
			append(s.getId()).//
			append(" Started on: ").//
			append(s.getStartGeneration()).//
			append(" Champion: ").//
			append(speciesService.getChampion(s).getGene().getId()).//
			append("\n");

			s.getPlayers().stream().forEach(p -> {
				sb.append("   - " + p.getFitness() + " " + geneService.stringify(p.getGene(), true) + "\n");
			});
		});
		return sb.toString();
	}

	public void setSpeciesService(SpeciesServiceImpl speciesService) {
		this.speciesService = speciesService;
	}

	public void setGeneService(GeneServiceImpl geneService) {
		this.geneService = geneService;
	}

}
