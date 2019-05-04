package it.vitalegi.neat.impl.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import it.vitalegi.neat.impl.Connection;
import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.Node;
import it.vitalegi.neat.impl.Random;
import it.vitalegi.neat.impl.UniqueId;
import it.vitalegi.neat.impl.util.StringUtil;

@Service
public class GeneServiceImpl {
	private static Logger log = LoggerFactory.getLogger(GeneServiceImpl.class);

	public Gene newInstance(UniqueId uniqueId) {
		return newInstance(uniqueId, uniqueId.nextGeneId(), 0, 0, 0);
	}

	public Gene newInstance(UniqueId uniqueId, int inputs, int outputs) {
		return newInstance(uniqueId, uniqueId.nextGeneId(), inputs, outputs, 0);
	}

	public Gene newInstance(UniqueId uniqueId, long id, int inputs, int outputs, int biases) {
		long[] inputIds = new long[inputs];
		long[] outputIds = new long[outputs];
		long[] biasIds = new long[biases];
		for (int i = 0; i < inputIds.length; i++) {
			inputIds[i] = uniqueId.nextNodeId();
		}
		for (int i = 0; i < outputIds.length; i++) {
			outputIds[i] = uniqueId.nextNodeId();
		}
		for (int i = 0; i < biasIds.length; i++) {
			biasIds[i] = uniqueId.nextNodeId();
		}
		return newInstance(uniqueId, id, inputIds, outputIds, biasIds);
	}

	public Gene newInstance(UniqueId uniqueId, long id, long[] inputIds, long[] outputIds, long[] biasIds) {
		Gene gene = new Gene(uniqueId, id);

		gene.setInputs(inputIds.length);
		gene.setOutputs(outputIds.length);

		for (int i = 0; i < inputIds.length; i++) {
			addNode(gene, Node.newInputInstance(uniqueId, inputIds[i]));
		}
		for (int i = 0; i < outputIds.length; i++) {
			addNode(gene, Node.newOutputInstance(uniqueId, outputIds[i]));
		}
		for (int i = 0; i < biasIds.length; i++) {
			addNode(gene, Node.newInstance(uniqueId, biasIds[i]));
		}
		return gene;
	}

	public Gene clone(Gene gene) {
		return copy(gene, new Gene(gene.getUniqueId(), gene.getId()));
	}

	public Gene copy(Gene source, Gene target) {
		target.setInputs(source.getInputs());
		target.setOutputs(source.getOutputs());
		source.getNodes().forEach(n -> addNode(target, n));
		source.getConnections().forEach(c -> addConnection(target, c.getId(), c.getFromNode().getId(),
				c.getToNode().getId(), c.getWeight(), c.isEnabled()));
		return target;
	}

	protected Connection addConnection(Gene gene, Connection connection) {
		return addConnection(gene, connection.getId(), connection.getFromNode().getId(), connection.getToNode().getId(),
				connection.getWeight(), connection.isEnabled());
	}

	public Connection addConnection(Gene gene, long node1, long node2, double weight, boolean enabled) {

		return addConnection(gene, gene.getUniqueId().nextConnectionId(node1, node2), node1, node2, weight, enabled);
	}

	public Connection addConnection(Gene gene, long connectionId, long nodeId1, long nodeId2, double weight,
			boolean enabled) {
		Node node1 = getOrCreateNodeById(gene, nodeId1);
		Node node2 = getOrCreateNodeById(gene, nodeId2);
		Connection c = Connection.newInstance(gene.getUniqueId(), connectionId, node1, node2, weight, enabled);
		gene.getConnections().add(c);
		return c;
	}

	protected Node addNode(Gene gene, long node, Connection inConnection) {
		Node n = getOrCreateNodeById(gene, node);

		Connection targetConnection = getConnectionById(gene, inConnection.getId());
		if (targetConnection == null) {
			throw new NoSuchElementException("La connessione " + inConnection + " non esiste");
		}
		inConnection.setEnabled(false);
		addConnection(gene, inConnection.getFromNode().getId(), n.getId(), 1.0, true);
		addConnection(gene, n.getId(), inConnection.getToNode().getId(), inConnection.getWeight(), true);
		return n;
	}

	public Node addNode(Gene gene, Node n) {
		gene.getNodes().add(n);
		return n;
	}

	protected boolean checkConnected(Gene gene, Node from, Node to) {
		Deque<Connection> stack = new LinkedList<>();

		getConnectionsFromNode(gene, from).forEach(stack::push);

		while (!stack.isEmpty()) {
			Connection c = stack.pop();
			if (c.getToNode().getId() == to.getId()) {
				return true;
			}
			getConnectionsFromNode(gene, c.getToNode()).forEach(stack::push);
		}
		return false;
	}

	public double getAvgWeightDifference(Gene gene1, Gene gene2, int size) {
		double sum = 0;
		int count = 0;
		for (int i = 0; i < size; i++) {
			double w1 = gene1.getConnections().get(i).getWeight();
			double w2 = gene2.getConnections().get(i).getWeight();
			if (gene1.getConnections().get(i).getId() != gene2.getConnections().get(i).getId()) {
				throw new IllegalArgumentException(
						gene1.getConnections().get(i).getId() + " " + gene2.getConnections().get(i).getId());
			}
			sum += Math.abs(w1 - w2);
			count++;
		}
		if (count == 0) {
			return 0;
		}
		return sum / count;
	}

	public Connection getConnection(Gene gene, long fromId, long toId) {
		return gene.getConnections().stream().//
				filter(c -> c.getFromNode().getId() == fromId). //
				filter(c -> c.getToNode().getId() == toId). //
				findFirst().orElse(null);
	}

	public Connection getConnectionById(Gene gene, long id) {
		return gene.getConnections().stream().filter(c -> c.getId() == id).findFirst().orElse(null);
	}

	public Connection getConnectionByIndex(Gene gene, int index) {
		return gene.getConnections().get(index);
	}

	protected List<Connection> getConnectionsFromNode(Gene gene, Node node) {
		return gene.getConnections().stream()//
				.filter(c -> c.getFromNode().getId() == node.getId())//
				.collect(Collectors.toList());
	}

	protected List<Connection> getConnectionsToNode(Gene gene, Node node) {
		return gene.getConnections().stream()//
				.filter(c -> c.getToNode().getId() == node.getId())//
				.collect(Collectors.toList());
	}

	public int getDisjointGenesCount(Gene gene1, Gene gene2) {
		int matchingCount = getMatchingGenesCount(gene1, gene2);
		return gene1.getSize() + gene2.getSize() - 2 * matchingCount;
	}

	public Long getInputNode(Gene gene, int index) {
		return getSortedInputNodes(gene).get(index);
	}

	public int getMatchingGenesCount(Gene gene1, Gene gene2) {
		int minLen = Math.min(gene1.getConnections().size(), gene2.getConnections().size());
		for (int i = 0; i < minLen; i++) {
			if (gene1.getConnections().get(i).getId() != gene2.getConnections().get(i).getId()) {
				return i;
			}
		}
		return 0;
	}

	protected Node getNodeById(Gene gene, long id) {
		return gene.getNodes().stream().filter(n -> n.getId() == id).findFirst().orElse(null);
	}

	protected Node getOrCreateNodeById(Gene gene, long id) {
		Node n = getNodeById(gene, id);
		if (n != null) {
			return n;
		}
		return addNode(gene, Node.newInstance(gene.getUniqueId(), id));
	}

	public Long getOutputNode(Gene gene, int index) {
		return getSortedOutputNodes(gene).get(index);
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

	public List<Long> getSortedInputNodes(Gene gene) {
		return getSortedNodes(gene, Node::isInput);
	}

	public List<Long> getSortedBiasNodes(Gene gene) {
		return getSortedNodes(gene, Node::isBias);
	}

	private List<Long> getSortedNodes(Gene gene, Predicate<Node> condition) {
		List<Long> list = new ArrayList<>();
		for (int i = 0; i < gene.getNodes().size(); i++) {
			if (condition.test(gene.getNodes().get(i))) {
				list.add(gene.getNodes().get(i).getId());
			}
		}
		return list;
	}

	public List<Long> getSortedOutputNodes(Gene gene) {
		return getSortedNodes(gene, Node::isOutput);
	}

	protected boolean isValidConnection(Gene gene, Node fromNode, Node toNode) {
		if (fromNode.getId() == toNode.getId()) {
			log.debug("I nodi sono uguali");
			return false;
		}
		if (getConnection(gene, fromNode.getId(), toNode.getId()) != null) {
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

	public Gene mutate(Gene gene, double addRandomNode, double removeRandomNode, double randomConnection,
			double randomEnable) {
		// shuffle pesi
		mutateWeights(gene);
		// aggiunta nodi
		if (Random.nextBoolean(addRandomNode)) {
			mutateAddRandomNode(gene);
		}
		// rimozione nodi
		if (Random.nextBoolean(removeRandomNode)) {
			// mutateRemoveRandomNode();
		}
		// aggiunta connessioni
		if (Random.nextBoolean(randomConnection)) {
			mutateAddRandomConnection(gene);
		}
		// abilito / disabilito
		if (Random.nextBoolean(randomEnable)) {
			mutateChangeRandomEnableConnection(gene);
		}
		return gene;
	}

	public Gene mutateAddRandomConnection(Gene gene) {
		final int attempts = 20;

		if (log.isDebugEnabled()) {
			log.debug("Topologia attuale: {}", stringify(gene, true));
		}
		for (int i = 0; i < attempts; i++) {
			Node fromNode = getRandomNode(gene, true, false);
			Node toNode = getRandomNode(gene, false, true);
			log.debug("Selezionato nodi: {} {}", fromNode.getId(), toNode.getId());

			if (!isValidConnection(gene, fromNode, toNode)) {
				log.debug("I nodi sono connessi");
				continue;
			}
			addConnection(gene, fromNode.getId(), toNode.getId(), Random.nextDouble(Gene.MIN_WEIGHT, Gene.MAX_WEIGHT),
					true);
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
		Node node = addNode(gene, gene.getUniqueId().nextNodeId(), randomConnection);
		if (log.isDebugEnabled()) {
			log.debug("Adding node {} in connection {} between {}-{}", node.getId(), randomConnection.getId(),
					randomConnection.getFromNode().getId(), randomConnection.getToNode().getId());
		}
		return gene;
	}

	protected Gene mutateRemoveRandomNode(Gene gene) {
		Node hiddenNode = getRandomNode(gene, false, false);
		if (hiddenNode == null) {
			if (log.isDebugEnabled()) {
				log.debug("No hidden nodes in gene {}. Skip delete", gene.getId());
			}
			return gene;
		}
		List<Connection> fromNode = getConnectionsFromNode(gene, hiddenNode);
		List<Connection> toNode = getConnectionsToNode(gene, hiddenNode);
		if (log.isDebugEnabled()) {
			String ins = toNode.stream().map(Connection::getId).map(String::valueOf).collect(Collectors.joining());
			String outs = fromNode.stream().map(Connection::getId).map(String::valueOf).collect(Collectors.joining());
			log.debug("Delete node {} and correlated connections in gene {}. In conns: {}. Out conns: {}",
					hiddenNode.getId(), gene.getId(), ins, outs);
		}
		deleteNodeById(gene, hiddenNode.getId());
		toNode.stream().map(Connection::getId).forEach(c -> deleteConnectionById(gene, c));
		fromNode.stream().map(Connection::getId).forEach(c -> deleteConnectionById(gene, c));
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

	protected Gene mutateWeights(Gene gene) {
		double mutateProbability = 0.8;
		double uniformPerturbationProbability = 0.9;
		double uniformPerturbation = 0.1;
		if (Random.nextBoolean(mutateProbability)) {
			boolean uniform = Random.nextBoolean(uniformPerturbationProbability);
			gene.getConnections().forEach(c -> {
				if (uniform) {
					c.setWeight(c.getWeight() * (1 + Random.nextDouble(-uniformPerturbation, uniformPerturbation)));
				} else {
					c.setWeight(Random.nextDouble(Gene.MIN_WEIGHT, Gene.MAX_WEIGHT));
				}
			});
		}
		return gene;
	}

	protected void deleteConnectionById(Gene gene, long id) {
		for (int i = 0; i < gene.getConnections().size(); i++) {
			if (gene.getConnections().get(i).getId() == id) {
				gene.getConnections().remove(i);
				return;
			}
		}
	}

	protected void deleteNodeById(Gene gene, long id) {
		for (int i = 0; i < gene.getNodes().size(); i++) {
			if (gene.getNodes().get(i).getId() == id) {
				gene.getNodes().remove(i);
				return;
			}
		}
	}

	public Gene offspring(Gene gene1, Gene gene2) {
		long[] inputIds = getSortedInputNodes(gene1).stream().mapToLong(n -> n).toArray();
		long[] outputIds = getSortedOutputNodes(gene1).stream().mapToLong(n -> n).toArray();
		Gene offspring = newInstance(gene1.getUniqueId(), gene1.getUniqueId().nextGeneId(), inputIds, outputIds,
				new long[0]);

		// add nodes
		for (Node node : gene1.getNodes()) {
			if (offspring.getNodes().stream().noneMatch(n -> n.getId() == node.getId())) {
				addNode(offspring, node);
			}
		}
		for (Node node : gene2.getNodes()) {
			if (offspring.getNodes().stream().noneMatch(n -> n.getId() == node.getId())) {
				addNode(offspring, node);
			}
		}

		int matchingCount = getMatchingGenesCount(gene1, gene2);

		// matching genes
		for (int i = 0; i < matchingCount; i++) {

			if (Random.nextBoolean()) {
				addConnection(offspring, getConnectionByIndex(gene1, i));
			} else {
				addConnection(offspring, getConnectionByIndex(gene2, i));
			}
		}
		int i1 = matchingCount;
		int i2 = matchingCount;

		if (Random.nextBoolean()) {
			while (i1 < gene1.getSize()) {
				addConnection(offspring, getConnectionByIndex(gene1, i1));
				i1++;
			}
		} else {
			while (i2 < gene2.getSize()) {
				addConnection(offspring, getConnectionByIndex(gene2, i2));
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

	public String stringify(Gene gene, boolean inline) {
		return stringify(gene, inline, true, true, Comparator.comparing(Connection::getId));
	}

	public String stringify(Gene gene, boolean inline, boolean includeConnectionName, boolean includeDisabled,
			Comparator<Connection> comparator) {
		StringBuilder sbb = new StringBuilder();
		sbb.append("Id: ").append(gene.getId());
		sbb.append(" Nodes: ").append(gene.getNodes().size());
		sbb.append(" Conns: ").append(gene.getConnections().size());
		sbb.append(" ");
		sbb.append(gene.getConnections().stream()//
				.filter(c -> c.isEnabled() || includeDisabled) //
				.sorted(comparator)//
				.map(c -> {
					StringBuilder sb = new StringBuilder();
					// id
					if (includeConnectionName) {
						sb.append(c.getId()).append(" ");
					}

					sb.append("(");
					if (getNodeById(gene, c.getFromNodeId()).isInput()) {
						sb.append("in:");
					}
					if (getNodeById(gene, c.getFromNodeId()).isBias()) {
						sb.append("bias:");
					}
					sb.append(c.getFromNodeId());
					sb.append("->");

					if (getNodeById(gene, c.getToNodeId()).isOutput()) {
						sb.append("out:");
					}
					if (getNodeById(gene, c.getToNodeId()).isBias()) {
						sb.append("bias:");
					}
					sb.append(c.getToNodeId());

					if (!c.isEnabled()) {
						sb.append(" DIS");
					} else {
						sb.append(" " + StringUtil.format(c.getWeight()));
					}
					sb.append(")");
					return sb;
				})//
				.collect(Collectors.joining(inline ? ", " : "\n")));

		return sbb.toString();
	}

}
