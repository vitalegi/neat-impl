package it.vitalegi.neat.impl.service;

import java.util.Comparator;
import java.util.List;

import it.vitalegi.neat.impl.Connection;
import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.Node;
import it.vitalegi.neat.impl.UniqueId;

public interface GeneService {

	Connection addConnection(Gene gene, Connection connection);

	Connection addConnection(Gene gene, long node1, long node2, double weight, boolean enabled);

	Connection addConnection(Gene gene, long connectionId, long nodeId1, long nodeId2, double weight, boolean enabled);

	Node addNode(Gene gene, long node, Connection inConnection);

	Node addNode(Gene gene, Node n);

	Gene clone(Gene gene);

	Gene copy(Gene source, Gene target);

	void deleteConnectionById(Gene gene, long id);

	void deleteNodeById(Gene gene, long id);

	double getAvgWeightDifference(Gene gene1, Gene gene2, int size);

	Connection getConnection(Gene gene, long fromId, long toId);

	Connection getConnectionById(Gene gene, long id);

	Connection getConnectionByIndex(Gene gene, int index);

	List<Connection> getConnectionsFromNode(Gene gene, Node node);

	List<Connection> getConnectionsToNode(Gene gene, Node node);

	int getDisjointGenesCount(Gene gene1, Gene gene2);

	Long getInputNode(Gene gene, int index);

	int getMatchingGenesCount(Gene gene1, Gene gene2);

	Node getNodeById(Gene gene, long id);

	Long getOutputNode(Gene gene, int index);

	List<Long> getSortedInputNodes(Gene gene);

	List<Long> getSortedOutputNodes(Gene gene);

	Gene newInstance(UniqueId uniqueId);

	Gene newInstance(UniqueId uniqueId, int inputs, int outputs);

	Gene newInstance(UniqueId uniqueId, long id, int inputs, int outputs, int biases);

	Gene newInstance(UniqueId uniqueId, long id, long[] inputIds, long[] outputIds, long[] biasIds);

	String stringify(Gene gene, boolean inline);

	String stringify(Gene gene, boolean inline, boolean includeConnectionName, boolean includeDisabled,
			Comparator<Connection> comparator);
}