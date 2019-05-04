package it.vitalegi.neat.impl.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.Generation;
import it.vitalegi.neat.impl.UniqueId;
import it.vitalegi.neat.impl.configuration.NeatConfig;
import it.vitalegi.neat.impl.player.Player;
import it.vitalegi.neat.impl.player.PlayerFactory;

@Service
public class FirstGenerationServiceImpl implements FirstGenerationService {

	@Autowired
	GenerationService generationService;
	@Autowired
	GeneService geneService;

	@Override
	public Generation create(NeatConfig config, PlayerFactory factory, int inputs, int outputs, int biases, int size) {
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

			List<Long> inputNodes = geneService.getSortedInputNodes(gene);
			List<Long> outputNodes = geneService.getSortedOutputNodes(gene);
			for (long inputNode : inputNodes) {
				for (long outputNode : outputNodes) {
					geneService.addConnection(gene, inputNode, outputNode, config.getMaxWeight(), true);
				}
			}

			Player player = factory.newPlayer(gene);
			generationService.addPlayer(gen, player);
		}
		return gen;
	}

	public void setGenerationService(GenerationService generationService) {
		this.generationService = generationService;
	}

	public void setGeneService(GeneService geneService) {
		this.geneService = geneService;
	}

}
