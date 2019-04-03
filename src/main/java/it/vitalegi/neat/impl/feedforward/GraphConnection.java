package it.vitalegi.neat.impl.feedforward;

public class GraphConnection {

	private GraphNode toNode;
	private double weight;

	public GraphConnection(GraphNode toNode, double weight) {
		this.toNode = toNode;
		this.weight = weight;
	}

	public GraphNode getToNode() {
		return toNode;
	}

	public double getWeight() {
		return weight;
	}

	public void setToNode(GraphNode toNode) {
		this.toNode = toNode;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
}
