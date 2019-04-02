package it.vitalegi.neat.impl.feedforward;

public class GraphConnection {

	private double weight;
	private GraphNode toNode;

	public GraphConnection(GraphNode toNode, double weight) {
		this.toNode = toNode;
		this.weight = weight;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public GraphNode getToNode() {
		return toNode;
	}

	public void setToNode(GraphNode toNode) {
		this.toNode = toNode;
	}
}
