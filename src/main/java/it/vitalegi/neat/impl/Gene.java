package it.vitalegi.neat.impl;

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

import it.vitalegi.neat.impl.feedforward.FeedForward;
import it.vitalegi.neat.impl.feedforward.FeedForwardImpl;
import it.vitalegi.neat.impl.util.StringUtil;

public class Gene {

	public static final double MIN_WEIGHT = -10;
	public static final double MAX_WEIGHT = 10;

	protected List<Connection> connections;

	protected long id;

	protected int inputs;

	protected List<Node> nodes;

	protected int outputs;

	protected UniqueId uniqueId;

	public Gene(UniqueId uniqueId, long id) {
		this.id = uniqueId.nextGeneId(id);
		this.uniqueId = uniqueId;
		connections = new ArrayList<>();
		nodes = new ArrayList<>();
	}

	@Override
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

	public List<Connection> getConnections() {
		return connections;
	}

	public long getId() {
		return id;
	}

	public int getInputs() {
		return inputs;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public int getOutputs() {
		return outputs;
	}

	public int getSize() {
		return connections.size();
	}

	public void setInputs(int inputs) {
		this.inputs = inputs;
	}

	public void setOutputs(int outputs) {
		this.outputs = outputs;
	}

	public UniqueId getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(UniqueId uniqueId) {
		this.uniqueId = uniqueId;
	}

	public void setConnections(List<Connection> connections) {
		this.connections = connections;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

}
