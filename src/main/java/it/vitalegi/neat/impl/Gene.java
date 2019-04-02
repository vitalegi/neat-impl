package it.vitalegi.neat.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.vitalegi.neat.impl.util.StringUtil;

public class Gene {

	protected UniqueId uniqueId;
	protected long id;
	protected List<Node> nodes;
	protected List<Connection> connections;
	protected int inputs;
	protected int outputs;

	public static Gene newInstance(UniqueId uniqueId) {
		return newInstance(uniqueId, uniqueId.nextGeneId(), 0, 0);
	}

	public static Gene newInstance(UniqueId uniqueId, int inputs, int outputs) {
		return newInstance(uniqueId, uniqueId.nextGeneId(), inputs, outputs);
	}

	public static Gene newInstance(UniqueId uniqueId, long id, int inputs, int outputs) {
		long[] inputIds = new long[inputs];
		long[] outputIds = new long[outputs];
		for (int i = 0; i < inputIds.length; i++) {
			inputIds[i] = uniqueId.nextNodeId();
		}
		for (int i = 0; i < outputIds.length; i++) {
			outputIds[i] = uniqueId.nextNodeId();
		}
		return new Gene(uniqueId, id, inputIds, outputIds);
	}

	public static Gene newInstance(UniqueId uniqueId, long id, long[] inputIds, long[] outputIds) {
		return new Gene(uniqueId, id, inputIds, outputIds);
	}

	private Gene(UniqueId uniqueId, long id, long[] inputIds, long[] outputIds) {
		this(uniqueId, id);

		this.inputs = inputIds.length;
		this.outputs = outputIds.length;

		for (int i = 0; i < inputIds.length; i++) {
			addNode(Node.newInputInstance(uniqueId, inputIds[i]));
		}
		for (int i = 0; i < outputIds.length; i++) {
			addNode(Node.newOutputInstance(uniqueId, outputIds[i]));
		}
	}

	private Gene(UniqueId uniqueId, long id) {
		this.id = uniqueId.nextGeneId(id);
		this.uniqueId = uniqueId;
		connections = new ArrayList<>();
		nodes = new ArrayList<>();
	}

	public Connection addConnection(long connectionId, long nodeId1, long nodeId2, double weight, boolean enabled) {
		Node node1 = getOrCreateNodeById(nodeId1);
		Node node2 = getOrCreateNodeById(nodeId2);
		Connection c = Connection.newInstance(uniqueId, connectionId, node1, node2, weight, enabled);
		connections.add(c);
		return c;
	}

	protected Connection addConnection(Connection connection) {
		return addConnection(connection.getId(), connection.getFromNode().getId(), connection.getToNode().getId(),
				connection.getWeight(), connection.isEnabled());
	}

	public Connection addConnection(long node1, long node2, double weight, boolean enabled) {

		return addConnection(uniqueId.nextConnectionId(node1, node2), node1, node2, weight, enabled);
	}

	protected Node addNode(long node, Connection inConnection) {
		Node n = getOrCreateNodeById(node);

		Connection targetConnection = getConnectionById(inConnection.getId());
		if (targetConnection == null) {
			throw new NoSuchElementException("La connessione " + inConnection + " non esiste");
		}
		inConnection.setEnabled(false);
		addConnection(inConnection.getFromNode().getId(), n.getId(), 1.0, true);
		addConnection(n.getId(), inConnection.getToNode().getId(), inConnection.getWeight(), true);
		return n;
	}

	public List<Long> getSortedInputNodes() {
		return getSortedNodes(Node::isInput);
	}

	public List<Long> getSortedOutputNodes() {
		return getSortedNodes(Node::isOutput);
	}

	public Long getInputNode(int index) {
		return getSortedInputNodes().get(index);
	}

	public Long getOutputNode(int index) {
		return getSortedOutputNodes().get(index);
	}

	private List<Long> getSortedNodes(Predicate<Node> condition) {
		List<Long> list = new ArrayList<>();
		for (int i = 0; i < nodes.size(); i++) {
			if (condition.test(nodes.get(i))) {
				list.add(nodes.get(i).getId());
			}
		}
		return list;
	}

	public int getSize() {
		return connections.size();
	}

	public Connection getConnectionById(long id) {
		return connections.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
	}

	public Connection getConnection(long fromId, long toId) {
		return connections.stream().//
				filter(c -> c.getFromNode().getId() == fromId). //
				filter(c -> c.getToNode().getId() == toId). //
				findFirst().orElse(null);
	}

	public Connection getConnectionByIndex(int index) {
		return connections.get(index);
	}

	public Gene mutate(double randomNode, double randomConnection, double randomEnable) {
		// shuffle pesi
		mutateWeights();
		// aggiunta nodi
		while (Random.nextBoolean(randomNode)) {
			mutateAddRandomNode();
		}
		// aggiunta connessioni
		while (Random.nextBoolean(randomConnection)) {
			mutateAddRandomConnection();
		}
		// abilito / disabilito
		while (Random.nextBoolean(randomEnable)) {
			mutateChangeRandomEnableConnection();
		}
		return this;
	}

	protected Gene mutateAddRandomNode() {
		if (connections.isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("Skip adding random node in gene {}: no existing connections", getId());
			}
			return this;
		}
		Connection randomConnection = connections.get(Random.nextInt(connections.size()));
		Node node = addNode(uniqueId.nextNodeId(), randomConnection);
		if (log.isDebugEnabled()) {
			log.debug("Adding node {} in connection {} between {}-{}", node.getId(), randomConnection.getId(),
					randomConnection.getFromNode().getId(), randomConnection.getToNode().getId());
		}
		return this;
	}

	protected Gene mutateAddRandomConnection() {
		final int attempts = 20;

		if (log.isDebugEnabled()) {
			log.debug("Topologia attuale: {}", stringify(true));
		}

		for (int i = 0; i < attempts; i++) {
			Node fromNode = getRandomNode(true, false);
			Node toNode = getRandomNode(false, true);
			log.debug("Selezionato nodi: {} {}", fromNode.getId(), toNode.getId());
			if (fromNode.getId() == toNode.getId()) {
				log.debug("I nodi sono uguali");
				continue;
			}
			if (!isValidConnection(fromNode, toNode)) {
				log.debug("I nodi sono connessi");
				continue;
			}
			if (getConnection(fromNode.getId(), toNode.getId()) != null) {
				log.debug("La connessione tra questi nodi esiste.");
				continue;
			}
			addConnection(fromNode.getId(), toNode.getId(), Random.nextDouble(-1, 1), true);
			return this;
		}
		if (log.isErrorEnabled()) {
			log.error("Impossibile trovare una connessione valida in {} tentativi", attempts);
		}
		return this;
	}

	protected Gene mutateChangeRandomEnableConnection() {
		if (connections.isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("Skip change enable node in gene {}: no existing connections", getId());
			}
			return this;
		}
		Connection randomConnection = connections.get(Random.nextInt(connections.size()));
		if (log.isDebugEnabled()) {
			log.debug("Change enable of node {}. Old value: " + randomConnection.isEnabled());
		}
		randomConnection.setEnabled(!randomConnection.isEnabled());
		return this;
	}

	protected Gene mutateWeights() {
		double mutateProbability = 0.8;
		double uniformPerturbationProbability = 0.9;
		double uniformPerturbation = 0.1;
		if (Random.nextBoolean(mutateProbability)) {
			boolean uniform = Random.nextBoolean(uniformPerturbationProbability);
			getConnections().forEach(c -> {
				if (uniform) {
					c.setWeight(c.getWeight() * (1 + Random.nextDouble(-uniformPerturbation, uniformPerturbation)));
				} else {
					c.setWeight(Random.nextDouble(-1, 1));
				}
			});
		}
		return this;
	}

	protected boolean isValidConnection(Node fromNode, Node toNode) {
		if (getConnection(fromNode.getId(), toNode.getId()) != null) {
			if (log.isDebugEnabled()) {
				log.debug("La connessione esiste gia'.");
			}
			return false;
		}
		if (checkConnected(toNode, fromNode)) {
			if (log.isDebugEnabled()) {
				log.debug("Il nodo di destinazione e' connesso al nodo di partenza, impossibile procedere.");
			}
			return false;
		}
		return true;
	}

	protected boolean checkConnected(Node from, Node to) {
		Deque<Connection> stack = new LinkedList<>();

		getConnectionsFromNode(from).forEach(stack::push);

		while (!stack.isEmpty()) {
			Connection c = stack.pop();
			if (c.getToNode().getId() == to.getId()) {
				return true;
			}
			getConnectionsFromNode(c.getToNode()).forEach(stack::push);
		}
		return false;
	}

	protected List<Connection> getConnectionsFromNode(Node node) {
		return connections.stream()//
				.filter(c -> c.getFromNode().getId() == node.getId())//
				.collect(Collectors.toList());
	}

	protected Node getRandomNode(boolean includeInputNodes, boolean includeOutputNodes) {
		if (nodes.isEmpty()) {
			throw new IllegalArgumentException("Il gene " + getId() + " non ha nodi.");
		}
		while (true) {
			int index = Random.nextInt(nodes.size());
			Node node = nodes.get(index);
			if (node.isInput() && includeInputNodes) {
				return node;
			}
			if (node.isOutput() && includeOutputNodes) {
				return node;
			}
			if (!node.isInput() && !node.isOutput()) {
				return node;
			}
		}
	}

	public Gene offspring(Gene gene) {
		long[] inputIds = getSortedInputNodes().stream().mapToLong(n -> n).toArray();
		long[] outputIds = getSortedOutputNodes().stream().mapToLong(n -> n).toArray();
		Gene offspring = Gene.newInstance(uniqueId, uniqueId.nextGeneId(), inputIds, outputIds);

		int matchingCount = this.getMatchingGenesCount(gene);

		// matching genes
		for (int i = 0; i < matchingCount; i++) {

			if (Random.nextBoolean()) {
				offspring.addConnection(this.getConnectionByIndex(i));
			} else {
				offspring.addConnection(gene.getConnectionByIndex(i));
			}
		}
		int i1 = matchingCount;
		int i2 = matchingCount;

		// disjoint & excess
		while (i1 < this.getSize() && i2 < gene.getSize()) {
			Connection conn1 = this.getConnectionByIndex(i1);
			Connection conn2 = gene.getConnectionByIndex(i2);
			if (conn1.getId() < conn2.getId()) {
				offspring.addConnection(conn1);
				i1++;
			} else {
				offspring.addConnection(conn2);
				i2++;
			}
		}

		while (i1 < this.getSize()) {
			offspring.addConnection(this.getConnectionByIndex(i1));
			i1++;
		}
		while (i2 < gene.getSize()) {
			offspring.addConnection(gene.getConnectionByIndex(i2));
			i2++;
		}
		return offspring;
	}

	public int getMatchingGenesCount(Gene gene) {
		int minLen = Math.min(this.connections.size(), gene.connections.size());
		for (int i = 0; i < minLen; i++) {
			if (this.connections.get(i).getId() != gene.connections.get(i).getId()) {
				return i;
			}
		}
		return 0;
	}

	public int getDisjointGenesCount(Gene gene) {
		int matchingCount = getMatchingGenesCount(gene);
		return this.getSize() + gene.getSize() - 2 * matchingCount;
	}

	public double getAvgWeightDifference(Gene gene, int size) {
		double sum = 0;
		int count = 0;
		for (int i = 0; i < size; i++) {
			double w1 = this.connections.get(i).getWeight();
			double w2 = gene.connections.get(i).getWeight();
			sum += Math.abs(w1 - w2);
			count++;
		}
		if (count == 0) {
			return 0;
		}
		return sum / count;
	}

	public String stringify(boolean inline) {
		StringBuilder sb = new StringBuilder();
		sb.append("Id: ").append(id);
		sb.append(" Nodes: ").append(nodes.size());
		sb.append(" Conns: ").append(connections.size());
		sb.append(" ");
		connections.stream().sorted(Comparator.comparing(Connection::getId))//
				.forEach(c -> {
					// id
					sb.append(c.getId());
					sb.append(" ");

					sb.append("(");
					if (getNodeById(c.getFromNode().getId()).isInput()) {
						sb.append("in:");
					}
					sb.append(c.getFromNode().getId());
					sb.append("->");

					if (getNodeById(c.getToNode().getId()).isOutput()) {
						sb.append("out:");
					}
					sb.append(c.getToNode().getId());

					if (!c.isEnabled()) {
						sb.append(" DIS");
					} else {
						sb.append(" " + StringUtil.format(c.getWeight()));
					}
					sb.append(")");

					if (inline) {
						sb.append(", ");
					} else {
						sb.append("\n");
					}
				});

		return sb.toString();
	}

	public Gene clone() {
		return copy(new Gene(uniqueId, id));
	}

	public Gene copy(Gene copy) {
		copy.setInputs(inputs);
		copy.setOutputs(outputs);
		this.nodes.forEach(copy::addNode);
		this.connections.forEach(c -> copy.addConnection(c.getId(), c.getFromNode().getId(), c.getToNode().getId(),
				c.getWeight(), c.isEnabled()));
		return copy;
	}

	protected Node getOrCreateNodeById(long id) {
		Node n = getNodeById(id);
		if (n != null) {
			return n;
		}
		return addNode(Node.newInstance(uniqueId, id));
	}

	protected Node addNode(Node n) {
		nodes.add(n);
		return n;
	}

	public long getId() {
		return id;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public List<Connection> getConnections() {
		return connections;
	}

	protected Node getNodeById(long id) {
		return nodes.stream().filter(n -> n.getId() == id).findFirst().orElse(null);
	}

	public int getInputs() {
		return inputs;
	}

	public void setInputs(int inputs) {
		this.inputs = inputs;
	}

	public int getOutputs() {
		return outputs;
	}

	public void setOutputs(int outputs) {
		this.outputs = outputs;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Gene)) {
			return false;
		}
		Gene other = (Gene) obj;
		return this.getId() == other.getId();
	}

	private static Logger log = LoggerFactory.getLogger(Gene.class);
}
