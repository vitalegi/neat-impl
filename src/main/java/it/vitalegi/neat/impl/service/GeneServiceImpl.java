package it.vitalegi.neat.impl.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import it.vitalegi.neat.impl.Connection;
import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.Node;
import it.vitalegi.neat.impl.UniqueId;
import it.vitalegi.neat.impl.util.StringUtil;

@Service
public class GeneServiceImpl implements GeneService {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(GeneServiceImpl.class);

	@Override
	public Connection addConnection(Gene gene, Connection connection) {
		return addConnection(gene, connection.getId(), connection.getFromNode().getId(), connection.getToNode().getId(),
				connection.getWeight(), connection.isEnabled());
	}

	@Override
	public Connection addConnection(Gene gene, long node1, long node2, double weight, boolean enabled) {

		return addConnection(gene, gene.getUniqueId().nextConnectionId(node1, node2), node1, node2, weight, enabled);
	}

	@Override
	public Connection addConnection(Gene gene, long connectionId, long nodeId1, long nodeId2, double weight,
			boolean enabled) {
		Node node1 = getOrCreateNodeById(gene, nodeId1);
		Node node2 = getOrCreateNodeById(gene, nodeId2);
		Connection c = Connection.newInstance(gene.getUniqueId(), connectionId, node1, node2, weight, enabled);
		gene.getConnections().add(c);
		return c;
	}

	@Override
	public Node addNode(Gene gene, long node, Connection inConnection) {
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

	@Override
	public Node addNode(Gene gene, Node n) {
		gene.getNodes().add(n);
		return n;
	}

	@Override
	public Gene clone(Gene gene) {
		return copy(gene, new Gene(gene.getUniqueId(), gene.getId()));
	}

	@Override
	public Gene copy(Gene source, Gene target) {
		target.setInputs(source.getInputs());
		target.setOutputs(source.getOutputs());
		source.getNodes().forEach(n -> addNode(target, n));
		source.getConnections().forEach(c -> addConnection(target, c.getId(), c.getFromNode().getId(),
				c.getToNode().getId(), c.getWeight(), c.isEnabled()));
		return target;
	}

	@Override
	public void deleteConnectionById(Gene gene, long id) {
		for (int i = 0; i < gene.getConnections().size(); i++) {
			if (gene.getConnections().get(i).getId() == id) {
				gene.getConnections().remove(i);
				return;
			}
		}
	}

	@Override
	public void deleteNodeById(Gene gene, long id) {
		for (int i = 0; i < gene.getNodes().size(); i++) {
			if (gene.getNodes().get(i).getId() == id) {
				gene.getNodes().remove(i);
				return;
			}
		}
	}

	@Override
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

	@Override
	public Connection getConnection(Gene gene, long fromId, long toId) {
		return gene.getConnections().stream().//
				filter(c -> c.getFromNode().getId() == fromId). //
				filter(c -> c.getToNode().getId() == toId). //
				findFirst().orElse(null);
	}

	@Override
	public Connection getConnectionById(Gene gene, long id) {
		return gene.getConnections().stream().filter(c -> c.getId() == id).findFirst().orElse(null);
	}

	@Override
	public Connection getConnectionByIndex(Gene gene, int index) {
		return gene.getConnections().get(index);
	}

	@Override
	public List<Connection> getConnectionsFromNode(Gene gene, Node node) {
		return gene.getConnections().stream()//
				.filter(c -> c.getFromNode().getId() == node.getId())//
				.collect(Collectors.toList());
	}

	@Override
	public List<Connection> getConnectionsToNode(Gene gene, Node node) {
		return gene.getConnections().stream()//
				.filter(c -> c.getToNode().getId() == node.getId())//
				.collect(Collectors.toList());
	}

	@Override
	public int getDisjointGenesCount(Gene gene1, Gene gene2) {
		int matchingCount = getMatchingGenesCount(gene1, gene2);
		return gene1.getSize() + gene2.getSize() - 2 * matchingCount;
	}

	@Override
	public Long getInputNode(Gene gene, int index) {
		return getSortedInputNodes(gene).get(index);
	}

	@Override
	public int getMatchingGenesCount(Gene gene1, Gene gene2) {
		int minLen = Math.min(gene1.getConnections().size(), gene2.getConnections().size());
		for (int i = 0; i < minLen; i++) {
			if (gene1.getConnections().get(i).getId() != gene2.getConnections().get(i).getId()) {
				return i;
			}
		}
		return 0;
	}

	@Override
	public Node getNodeById(Gene gene, long id) {
		return gene.getNodes().stream().filter(n -> n.getId() == id).findFirst().orElse(null);
	}

	protected Node getOrCreateNodeById(Gene gene, long id) {
		Node n = getNodeById(gene, id);
		if (n != null) {
			return n;
		}
		return addNode(gene, Node.newInstance(gene.getUniqueId(), id));
	}

	@Override
	public Long getOutputNode(Gene gene, int index) {
		return getSortedOutputNodes(gene).get(index);
	}

	@Override
	public List<Long> getSortedInputNodes(Gene gene) {
		return getSortedNodes(gene, Node::isInput);
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

	@Override
	public List<Long> getSortedOutputNodes(Gene gene) {
		return getSortedNodes(gene, Node::isOutput);
	}

	@Override
	public Gene newInstance(UniqueId uniqueId) {
		return newInstance(uniqueId, uniqueId.nextGeneId(), 0, 0, 0);
	}

	@Override
	public Gene newInstance(UniqueId uniqueId, int inputs, int outputs) {
		return newInstance(uniqueId, uniqueId.nextGeneId(), inputs, outputs, 0);
	}

	@Override
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

	@Override
	public Gene newInstance(UniqueId uniqueId, long id, long[] inputIds, long[] outputIds, long[] biasIds) {
		Gene gene = new Gene(uniqueId, id);

		gene.setInputs(inputIds.length);
		gene.setOutputs(outputIds.length);

		for (int i = 0; i < inputIds.length; i++) {
			addNode(gene, Node.newInputInstance(uniqueId, inputIds[i]));
		}
		for (int i = 0; i < biasIds.length; i++) {
			addNode(gene, Node.newInputInstance(uniqueId, biasIds[i]));
		}
		for (int i = 0; i < outputIds.length; i++) {
			addNode(gene, Node.newOutputInstance(uniqueId, outputIds[i]));
		}
		return gene;
	}

	@Override
	public String stringify(Gene gene, boolean inline) {
		return stringify(gene, inline, true, true, Comparator.comparing(Connection::getId));
	}

	@Override
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
					sb.append(c.getFromNodeId());
					sb.append("->");

					if (getNodeById(gene, c.getToNodeId()).isOutput()) {
						sb.append("out:");
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
