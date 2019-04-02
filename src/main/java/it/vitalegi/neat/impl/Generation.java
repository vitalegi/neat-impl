package it.vitalegi.neat.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.vitalegi.neat.impl.function.CompatibilityDistance;
import it.vitalegi.neat.impl.function.SharedFitnessValue;

public class Generation {

	// numero di generazioni entro cui considerare vecchia una specie
	public static final int YOUNG_GEN = 15;
	// nel caso di valutazione della crescita, generazione di riferimento
	public static final int COMPARE_AGAINST_GEN = 10;
	// nel caso di valutazione della crescita, tasso di crescita minimo rispetto
	// la generazione di riferimento
	public static final double MIN_GROWTH_RATIO = 0.05;
	// dimensione minima per considerare rilevante una specie
	public static final int MIN_SPECIES_SIZE_TO_BE_RELEVANT = 5;
	// percentuale di geni da eliminare ad ogni nuova generazione
	public static final double REMOVE_LOW_PERFORMANCES_RATIO = 0.1;
	// probabilita' di aggiungere un nodo
	public static final double MUTATE_NODE_PROBABILITY = 0.01;
	// probabilita' di aggiungere una connessione
	public static final double MUTATE_CONNECTION_PROBABILITY = 0.03;
	// probabilita' di abilitare/disabilitare una connessione
	public static final double MUTATE_ENABLE_PROBABILITY = 0.001;
	// numero di specie da mantenere a prescindere dal miglioramento di rate
	public static final int BEST_SPECIES_TO_PRESERVE = 2;

	private int genNumber;
	private PlayerFactory factory;
	private List<Player> players;
	private List<Species> species;
	private CompatibilityDistance compatibilityDistance;
	private UniqueId uniqueId;

	public Generation(PlayerFactory factory, int genNumber, CompatibilityDistance compatibilityDistance) {
		uniqueId = new UniqueId();
		this.genNumber = genNumber;
		this.factory = factory;
		this.compatibilityDistance = compatibilityDistance;
		players = new ArrayList<>();
		species = new ArrayList<>();
	}

	public static Generation createGen0(PlayerFactory factory, int inputs, int outputs, int size,
			CompatibilityDistance compatibilityDistance) {
		Generation gen = new Generation(factory, 0, compatibilityDistance);

		long[] inputIds = new long[inputs];
		long[] outputIds = new long[outputs];
		for (int i = 0; i < inputIds.length; i++) {
			inputIds[i] = gen.uniqueId.nextNodeId();
		}
		for (int i = 0; i < outputIds.length; i++) {
			outputIds[i] = gen.uniqueId.nextNodeId();
		}
		for (int i = 0; i < size; i++) {
			Player player = factory.newPlayer(//
					Gene.newInstance(gen.uniqueId, gen.uniqueId.nextNodeId(), inputIds, outputIds));
			gen.addPlayer(player);
		}
		return gen;
	}

	public void computeFitnesses() {
		species.forEach(s -> {
			s.addFitness(s.getChampion().getFitness());
		});
	}

	public Generation nextGeneration() {
		Generation nextGen = new Generation(factory, genNumber + 1, compatibilityDistance);

		List<Species> speciesToPreserve = getSpeciesToPreserve();

		for (Species s : speciesToPreserve) {
			nextGen.preserveSpecies(s, s.getChampion().getGene());
		}

		Map<Long, List<Player>> selectedPlayers = new HashMap<>();

		speciesToPreserve.forEach(s -> {
			selectedPlayers.put(s.getId(), new ArrayList<>());
			getBestPlayers(s)//
					.forEach(p -> selectedPlayers.get(s.getId()).add(p));
		});

		while (nextGen.getPlayers().size() != this.getPlayers().size()) {
			nextGen.addPlayer(nextGen.getNextMutatedPlayer(selectedPlayers));
		}

		return nextGen;
	}

	public Species addPlayer(Player player) {
		Species compatible = getCompatibleSpecies(player);
		if (compatible == null) {
			compatible = Species.newInstance(uniqueId.nextSpeciesId(), genNumber, compatibilityDistance);
			addSpecies(compatible);
		}
		addPlayer(player, compatible);
		return compatible;
	}

	protected List<Player> getBestPlayers(Species species) {
		if (isYoung(species)) {
			return species.getBestPlayers(species, species.getPlayers().size());
		}
		int newSize = (int) Math.round(species.getPlayers().size() * REMOVE_LOW_PERFORMANCES_RATIO);

		return species.getBestPlayers(species, Math.max(1, newSize));
	}

	protected Player getNextMutatedPlayer(Map<Long, List<Player>> selectedPlayers) {
		if (selectedPlayers.values().stream().mapToInt(List::size).sum() == 0) {
			log.error("Non ho altri giocatori disponibili. Generazione {}", genNumber);
		}
		Player player1 = getRandomPlayer(selectedPlayers);
		Gene newGene1 = player1.getGene().clone();
		newGene1.mutate(MUTATE_NODE_PROBABILITY, MUTATE_CONNECTION_PROBABILITY, MUTATE_ENABLE_PROBABILITY);
		Player player2 = getRandomPlayer(selectedPlayers);
		Gene newGene2 = player2.getGene().clone();
		newGene2.mutate(MUTATE_NODE_PROBABILITY, MUTATE_CONNECTION_PROBABILITY, MUTATE_ENABLE_PROBABILITY);
		Gene offspring = newGene1.offspring(newGene2);
		return factory.newPlayer(offspring);
	}

	protected Species getSpeciesFromPlayer(Player player) {
		for (Species s : species) {
			if (s.getPlayerByGeneId(player.getGeneId()) != null) {
				return s;
			}
		}
		return null;
	}

	protected Player getRandomPlayer(Map<Long, List<Player>> players) {
		int size = (int) players.values().stream().mapToInt(List::size).sum();
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

	protected Player getRandomPlayer(List<Player> players, double[] weights) {

		int selectedIndex = Random.nextRandom(weights);

		return players.get(selectedIndex);
	}

	protected void addSpecies(Species species) {
		this.species.add(species);
	}

	protected void addPlayer(Player player, Species species) {
		players.add(player);
		species.addPlayer(player);
	}

	protected Species getCompatibleSpecies(Player player) {
		for (Species species : this.species) {
			if (species.isCompatible(player)) {
				return species;
			}
		}
		return null;
	}

	protected boolean isYoung(Species species) {
		boolean young = genNumber - species.getStartGeneration() <= YOUNG_GEN;
		if (log.isDebugEnabled()) {
			log.debug("Specie {} giovane? {}", species.getId(), young);
		}
		return young;
	}

	protected List<Species> getSpeciesToPreserve() {
		List<Species> speciesToPreserve = new ArrayList<>();
		for (Species s : species) {
			if (isYoung(s) || hasMinimumGrowth(s) || isTopScoreSpecies(s)) {
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

	protected Species preserveSpecies(Species speciesToPreserve, Gene champion) {
		Species nextSpeciesGen = Species.newInstance(//
				speciesToPreserve.getId(), speciesToPreserve.getStartGeneration(),
				speciesToPreserve.getCompatibilityDistance());
		nextSpeciesGen.getHistoryBestFitnesses().addAll(speciesToPreserve.getHistoryBestFitnesses());
		addSpecies(nextSpeciesGen);
		if (speciesToPreserve.isRelevantSpecies()) {
			if (log.isDebugEnabled()) {
				log.debug("Specie {} ha rappresentanza minima, copio campione.", speciesToPreserve.getId());
			}
			addPlayer(factory.newPlayer(champion.clone()), nextSpeciesGen);
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Specie {} NON ha rappresentanza minima, muto campione.", speciesToPreserve.getId());
			}
			addPlayer(factory.newPlayer(
					champion.mutate(MUTATE_NODE_PROBABILITY, MUTATE_CONNECTION_PROBABILITY, MUTATE_ENABLE_PROBABILITY)),
					nextSpeciesGen);
		}
		return nextSpeciesGen;
	}

	public boolean hasMinimumGrowth(Species species) {

		double currFitness = species.getLastFitness();
		double cmpFitness = species.getFitness(genNumber - COMPARE_AGAINST_GEN);
		double ratio = 0;
		if (cmpFitness > 0) {
			ratio = currFitness / cmpFitness;
		} else {
			ratio = currFitness;
		}

		if (ratio < MIN_GROWTH_RATIO) {
			if (log.isDebugEnabled()) {
				log.debug("Remove species {}. Curr fitness: {}. {} gen ago's fitness: {}. Ratio: {}", species.getId(),
						currFitness, cmpFitness, COMPARE_AGAINST_GEN, ratio);
			}
			return false;
		}
		if (log.isDebugEnabled()) {
			log.debug("Specie {} ha crescita minima, la mantengo.", species.getId());
		}
		return true;
	}

	protected boolean isTopScoreSpecies(Species species) {
		boolean isTopScore = this.species.stream()//
				.sorted(Comparator.comparing(Species::getLastFitness).reversed())//
				.limit(BEST_SPECIES_TO_PRESERVE)//
				.anyMatch(b -> b.getId() == species.getId());

		if (log.isDebugEnabled()) {
			log.debug("Specie {} e' tra le migliori {}, la mantengo.", species.getId(), BEST_SPECIES_TO_PRESERVE);
		}
		return isTopScore;
	}

	public String stringify() {
		StringBuilder sb = new StringBuilder();
		sb.append("GEN: " + genNumber + "\n");
		sb.append("Species: " + species.size() + "\n");
		species.forEach(s -> {
			sb.append(" - ").//
			append(s.getId()).//
			append(" Started on: ").//
			append(s.getStartGeneration()).//
			append(" Champion: ").//
			append(s.getChampion().getGene().getId()).//
			append("\n");

			s.getPlayers().stream().forEach(p -> {
				sb.append("   - " + p.getFitness() + " " + p.getGene().stringify(true) + "\n");
			});
		});
		return sb.toString();
	}

	public int getGenNumber() {
		return genNumber;
	}

	public void setGenNumber(int genNumber) {
		this.genNumber = genNumber;
	}

	public CompatibilityDistance getCompatibilityDistance() {
		return compatibilityDistance;
	}

	public void setCompatibilityDistance(CompatibilityDistance compatibilityDistance) {
		this.compatibilityDistance = compatibilityDistance;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public List<Species> getSpecies() {
		return species;
	}

	public UniqueId getUniqueId() {
		return uniqueId;
	}

	Logger log = LoggerFactory.getLogger(Generation.class);
}