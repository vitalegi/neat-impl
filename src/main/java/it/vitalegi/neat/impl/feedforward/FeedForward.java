package it.vitalegi.neat.impl.feedforward;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.vitalegi.neat.impl.Connection;
import it.vitalegi.neat.impl.Gene;
import it.vitalegi.neat.impl.Node;

public class FeedForward {

	Gene gene;
	Map<Long, GraphNode> graph;

	public FeedForward(Gene gene) {
		this.gene = gene;
	}

	public double[] feedForward(double[] inputs) {

		if (gene.getInputs() != inputs.length) {
			throw new IllegalArgumentException(
					"Input size is invalid. Expected " + gene.getInputs() + " found " + inputs.length);
		}
		initGraph();

		// initialize input connections
		List<Long> inputNodes = gene.getSortedInputNodes();
		List<Long> outputNodes = gene.getSortedOutputNodes();
		inputNodes.forEach(this::initIfNotExists);
		outputNodes.forEach(this::initIfNotExists);

		if (log.isDebugEnabled()) {
			log.debug("Graph with nodes: {}", graphToString());
		}
		initInputConnections(inputNodes, inputs);
		if (log.isDebugEnabled()) {
			log.debug("Graph initialized: {}", graphToString());
		}
		Iterator<Long> toProcess = getNodesInFeedingOrder().iterator();
		while (toProcess.hasNext()) {
			GraphNode node = graph.get(toProcess.next());
			if (log.isDebugEnabled()) {
				log.debug("Feeding {}: {}", node.getId(), graphToString());
			}
			node.feedSuccessors();
		}
		if (log.isDebugEnabled()) {
			log.debug("Graph after hidden layer: {}", graphToString());
		}
		double[] outputs = new double[outputNodes.size()];
		for (int i = 0; i < outputNodes.size(); i++) {
			GraphNode node = graph.get(outputNodes.get(i));
			outputs[i] = node.calculateOutputValue();
		}
		if (log.isDebugEnabled()) {
			log.debug("Graph end: {}", graphToString());
		}
		return outputs;
	}

	protected Map<Long, GraphNode> initGraph() {
		graph = new HashMap<>();

		List<Connection> connections = gene.getConnections();

		// init nodes
		connections.stream().forEach(c -> {
			initIfNotExists(c.getFromNode());
			initIfNotExists(c.getToNode());
		});

		// create connections
		connections.stream().forEach(c -> {
			GraphNode fromNode = graph.get(c.getFromNode().getId());
			fromNode.addSuccessor(new GraphConnection(graph.get(c.getToNode().getId()), c.getWeight()));
		});
		return graph;
	}

	protected void initIfNotExists(Node node) {
		initIfNotExists(node.getId());
	}

	protected void initIfNotExists(long id) {
		if (!graph.containsKey(id)) {
			graph.put(id, new GraphNode(id));
		}
	}

	protected void initInputConnections(List<Long> inputNodes, double[] inputs) {

		for (int i = 0; i < inputNodes.size(); i++) {
			Long nodeId = inputNodes.get(i);
			double nodeWeight = inputs[i];
			GraphNode node = graph.get(nodeId);
			node.setInputsSum(nodeWeight);
		}
	}

	protected String graphToString() {
		StringBuilder sb = new StringBuilder();
		List<Long> nodeIds = getNodesInFeedingOrder();
		nodeIds.forEach(nodeId -> {
			GraphNode node = graph.get(nodeId);

			sb.append(node.getId()).append(" ")//
					.append("in:").append(format(node.getInputsSum())).append(" ")//
					.append("out:").append(format(node.getOutputValue())).append(" ")//
					.append("sig:").append(format(node.calculateOutputValue()))//
					.append(node.allInputsReceived() ? "*" : "").append(", ");
		});
		return sb.toString();
	}

	private String format(double value) {
		return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).toPlainString();
	}

	protected List<Long> getNodesInFeedingOrder() {
		List<Long> nodes = new ArrayList<>();

		List<Long> pendingNodes = graph.keySet().stream().collect(Collectors.toList());

		while (!pendingNodes.isEmpty()) {
			for (int i = 0; i < pendingNodes.size(); i++) {
				GraphNode curr = graph.get(pendingNodes.get(i));

				boolean isSuccessor = false;
				for (int j = 0; j < pendingNodes.size(); j++) {
					GraphNode possiblePredecessor = graph.get(pendingNodes.get(j));
					if (possiblePredecessor.isPredecessor(curr.getId())) {
						isSuccessor = true;
						break;
					}
				}
				if (!isSuccessor) {
					nodes.add(curr.getId());
					pendingNodes.remove(i);
					i--;
				}
			}
		}
		return nodes;
	}

	Logger log = LoggerFactory.getLogger(FeedForward.class);

}
