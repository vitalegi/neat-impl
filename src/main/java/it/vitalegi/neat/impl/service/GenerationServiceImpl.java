package it.vitalegi.neat.impl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.vitalegi.neat.impl.Generation;
import it.vitalegi.neat.impl.Species;
import it.vitalegi.neat.impl.player.Player;

@Service
public class GenerationServiceImpl implements GenerationService {

	@Autowired
	GeneService geneService;

	Logger log = LoggerFactory.getLogger(GenerationServiceImpl.class);
	@Autowired
	SpeciesService speciesService;

	@Override
	public Species addPlayer(Generation gen, Player player) {
		Species compatible = getCompatibleSpecies(gen, player);
		if (compatible == null) {
			compatible = speciesService.newInstance(gen.getUniqueId().nextSpeciesId(), gen.getGenNumber());
			addSpecies(gen, compatible);
		}
		addPlayer(gen, player, compatible);
		return compatible;
	}

	@Override
	public void addPlayer(Generation gen, Player player, Species species) {
		gen.getPlayers().add(player);
		species.addPlayer(player);
	}

	@Override
	public void addSpecies(Generation gen, Species species) {
		gen.getSpecies().add(species);
	}

	@Override
	public void computeFitnesses(Generation gen) {
		gen.getSpecies().forEach(s -> {
			s.addFitness(speciesService.getChampion(s).getFitness());
		});
	}

	@Override
	public Species getCompatibleSpecies(Generation gen, Player player) {
		for (Species species : gen.getSpecies()) {
			if (speciesService.isCompatible(species, player)) {
				return species;
			}
		}
		return null;
	}

	protected Species getSpeciesFromPlayer(Generation gen, Player player) {
		for (Species s : gen.getSpecies()) {
			if (speciesService.getPlayerByGeneId(s, player.getGeneId()) != null) {
				return s;
			}
		}
		return null;
	}

	public void setGeneService(GeneService geneService) {
		this.geneService = geneService;
	}

	public void setSpeciesService(SpeciesService speciesService) {
		this.speciesService = speciesService;
	}

	@Override
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

}
