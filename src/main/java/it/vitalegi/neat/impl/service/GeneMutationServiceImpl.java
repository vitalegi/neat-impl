package it.vitalegi.neat.impl.service;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.vitalegi.neat.impl.Connection;
import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.Node;
import it.vitalegi.neat.impl.Random;
import it.vitalegi.neat.impl.configuration.NeatConfig;

@Service
public class GeneMutationServiceImpl implements GeneMutationService {

	private static Logger log = LoggerFactory.getLogger(GeneServiceImpl.class);

	@Autowired
	GeneService geneService;

	protected boolean checkConnected(Gene gene, Node from, Node to) {
		Deque<Connection> stack = new LinkedList<>();

		geneService.getConnectionsFromNode(gene, from).forEach(stack::push);

		while (!stack.isEmpty()) {
			Connection c = stack.pop();
			if (c.getToNode().getId() == to.getId()) {
				return true;
			}
			geneService.getConnectionsFromNode(gene, c.getToNode()).forEach(stack::push);
		}
		return false;
	}

	protected Node getRandomNode(Gene gene, boolean includeInputNodes, boolean includeOutputNodes) {
		if (gene.getNodes().isEmpty()) {
			throw new IllegalArgumentException("Il gene " + gene.getId() + " non ha nodi.");
		}
		Stream<Node> stream = gene.getNodes().stream();
		if (!includeInputNodes) {
			stream = stream.filter(node -> !node.isInput());
		}
		if (!includeOutputNodes) {
			stream = stream.filter(node -> !node.isOutput());
		}
		List<Node> acceptableNodes = stream.collect(Collectors.toList());
		stream.close();
		if (acceptableNodes.isEmpty()) {
			return null;
		}
		return acceptableNodes.get(Random.nextInt(acceptableNodes.size()));
	}

	protected boolean isValidConnection(Gene gene, Node fromNode, Node toNode) {
		if (fromNode.getId() == toNode.getId()) {
			log.debug("I nodi sono uguali");
			return false;
		}
		if (geneService.getConnection(gene, fromNode.getId(), toNode.getId()) != null) {
			if (log.isDebugEnabled()) {
				log.debug("La connessione esiste gia'.");
			}
			return false;
		}
		if (checkConnected(gene, toNode, fromNode)) {
			if (log.isDebugEnabled()) {
				log.debug("Il nodo di destinazione e' connesso al nodo di partenza, impossibile procedere.");
			}
			return false;
		}

		return true;
	}

	protected boolean isMutateWeights(NeatConfig neatConfig) {
		return Random.nextBoolean(neatConfig.getMutateProbability());
	}

	protected boolean isMutateAddRandomNode(NeatConfig neatConfig) {
		return Random.nextBoolean(neatConfig.getMutateAddNodeProbability());
	}

	protected boolean isMutateAddRandomConnection(NeatConfig neatConfig) {
		return Random.nextBoolean(neatConfig.getMutateConnectionProbability());
	}

	protected boolean isMutateChangeRandomEnableConnection(NeatConfig neatConfig) {
		return Random.nextBoolean(neatConfig.getMutateEnableProbability());
	}

	@Override
	public Gene mutate(NeatConfig neatConfig, Gene gene) {

		// shuffle pesi
		if (isMutateWeights(neatConfig)) {
			mutateWeights(neatConfig, gene);
		}
		// aggiunta nodi
		if (isMutateAddRandomNode(neatConfig)) {
			mutateAddRandomNode(gene);
		}
		// aggiunta connessioni
		if (isMutateAddRandomConnection(neatConfig)) {
			mutateAddRandomConnection(neatConfig, gene);
		}
		// abilito / disabilito
		if (isMutateChangeRandomEnableConnection(neatConfig)) {
			mutateChangeRandomEnableConnection(gene);
		}
		return gene;
	}

	@Override
	public Gene mutateAddRandomConnection(NeatConfig neatConfig, Gene gene) {
		final int attempts = 20;

		if (log.isDebugEnabled()) {
			log.debug("Topologia attuale: {}", geneService.stringify(gene, true));
		}
		for (int i = 0; i < attempts; i++) {
			Node fromNode = getRandomNode(gene, true, false);
			Node toNode = getRandomNode(gene, false, true);
			log.debug("Selezionato nodi: {} {}", fromNode.getId(), toNode.getId());

			if (!isValidConnection(gene, fromNode, toNode)) {
				log.debug("I nodi sono connessi");
				continue;
			}
			geneService.addConnection(gene, fromNode.getId(), toNode.getId(),
					Random.nextDouble(neatConfig.getMinWeight(), neatConfig.getMaxWeight()), true);
			return gene;
		}
		if (log.isDebugEnabled()) {
			log.debug("Impossibile trovare una connessione valida in {} tentativi", attempts);
		}
		return gene;
	}

	protected Gene mutateAddRandomNode(Gene gene) {
		if (gene.getConnections().isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("Skip adding random node in gene {}: no existing connections", gene.getId());
			}
			return gene;
		}
		Connection randomConnection = gene.getConnections().get(Random.nextInt(gene.getConnections().size()));
		Node node = geneService.addNode(gene, gene.getUniqueId().nextNodeId(), randomConnection);
		if (log.isDebugEnabled()) {
			log.debug("Adding node {} in connection {} between {}-{}", node.getId(), randomConnection.getId(),
					randomConnection.getFromNode().getId(), randomConnection.getToNode().getId());
		}
		return gene;
	}

	protected Gene mutateChangeRandomEnableConnection(Gene gene) {
		if (gene.getConnections().isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("Skip change enable node in gene {}: no existing connections", gene.getId());
			}
			return gene;
		}
		Connection randomConnection = gene.getConnections().get(Random.nextInt(gene.getConnections().size()));
		if (log.isDebugEnabled()) {
			log.debug("Change enable of node {}. Old value: " + randomConnection.isEnabled());
		}
		randomConnection.setEnabled(!randomConnection.isEnabled());
		return gene;
	}

	private Gene mutateRemoveRandomNode(Gene gene) {
		Node hiddenNode = getRandomNode(gene, false, false);
		if (hiddenNode == null) {
			if (log.isDebugEnabled()) {
				log.debug("No hidden nodes in gene {}. Skip delete", gene.getId());
			}
			return gene;
		}
		List<Connection> fromNode = geneService.getConnectionsFromNode(gene, hiddenNode);
		List<Connection> toNode = geneService.getConnectionsToNode(gene, hiddenNode);
		if (log.isDebugEnabled()) {
			String ins = toNode.stream().map(Connection::getId).map(String::valueOf).collect(Collectors.joining());
			String outs = fromNode.stream().map(Connection::getId).map(String::valueOf).collect(Collectors.joining());
			log.debug("Delete node {} and correlated connections in gene {}. In conns: {}. Out conns: {}",
					hiddenNode.getId(), gene.getId(), ins, outs);
		}
		geneService.deleteNodeById(gene, hiddenNode.getId());
		toNode.stream().map(Connection::getId).forEach(c -> geneService.deleteConnectionById(gene, c));
		fromNode.stream().map(Connection::getId).forEach(c -> geneService.deleteConnectionById(gene, c));
		return gene;
	}

	protected boolean isUniformPerturbation(NeatConfig neatConfig) {
		return Random.nextBoolean(neatConfig.getUniformPerturbationProbability());
	}

	protected Gene mutateWeights(NeatConfig neatConfig, Gene gene) {

		if (isUniformPerturbation(neatConfig)) {
			return mutateWeightsUniform(neatConfig, gene);
		} else {
			return mutateWeightsRandom(neatConfig, gene);
		}
	}

	protected Gene mutateWeightsUniform(NeatConfig neatConfig, Gene gene) {

		for (Connection c : gene.getConnections()) {
			double variation = Random.nextDouble(-neatConfig.getUniformPerturbation(),
					neatConfig.getUniformPerturbation());
			double newWeight = c.getWeight() * (1 + variation);
			c.setWeight(newWeight);
		}
		return gene;
	}

	protected Gene mutateWeightsRandom(NeatConfig neatConfig, Gene gene) {

		gene.getConnections().forEach(c -> {
			c.setWeight(Random.nextDouble(neatConfig.getMinWeight(), neatConfig.getMaxWeight()));
		});
		return gene;
	}

	@Override
	public Gene offspring(Gene gene1, Gene gene2) {
		long[] inputIds = geneService.getSortedInputNodes(gene1).stream().mapToLong(n -> n).toArray();
		long[] outputIds = geneService.getSortedOutputNodes(gene1).stream().mapToLong(n -> n).toArray();
		Gene offspring = geneService.newInstance(gene1.getUniqueId(), gene1.getUniqueId().nextGeneId(), inputIds,
				outputIds, new long[0]);

		// add nodes
		for (Node node : gene1.getNodes()) {
			if (offspring.getNodes().stream().noneMatch(n -> n.getId() == node.getId())) {
				geneService.addNode(offspring, node);
			}
		}
		for (Node node : gene2.getNodes()) {
			if (offspring.getNodes().stream().noneMatch(n -> n.getId() == node.getId())) {
				geneService.addNode(offspring, node);
			}
		}

		int matchingCount = geneService.getMatchingGenesCount(gene1, gene2);

		// matching genes
		for (int i = 0; i < matchingCount; i++) {

			if (Random.nextBoolean()) {
				geneService.addConnection(offspring, geneService.getConnectionByIndex(gene1, i));
			} else {
				geneService.addConnection(offspring, geneService.getConnectionByIndex(gene2, i));
			}
		}
		int i1 = matchingCount;
		int i2 = matchingCount;

		if (Random.nextBoolean()) {
			while (i1 < gene1.getSize()) {
				geneService.addConnection(offspring, geneService.getConnectionByIndex(gene1, i1));
				i1++;
			}
		} else {
			while (i2 < gene2.getSize()) {
				geneService.addConnection(offspring, geneService.getConnectionByIndex(gene2, i2));
				i2++;
			}
		}
		// disjoint & excess
		// while (i1 < this.getSize() && i2 < gene.getSize()) {
		// Connection conn1 = this.getConnectionByIndex(i1);
		// Connection conn2 = gene.getConnectionByIndex(i2);
		// if (conn1.getId() < conn2.getId()) {
		// offspring.addConnection(conn1);
		// i1++;
		// } else {
		// offspring.addConnection(conn2);
		// i2++;
		// }
		// }
		//
		// while (i1 < this.getSize()) {
		// offspring.addConnection(this.getConnectionByIndex(i1));
		// i1++;
		// }
		// while (i2 < gene.getSize()) {
		// offspring.addConnection(gene.getConnectionByIndex(i2));
		// i2++;
		// }
		return offspring;
	}

	public void setGeneService(GeneService geneService) {
		this.geneService = geneService;
	}

}
