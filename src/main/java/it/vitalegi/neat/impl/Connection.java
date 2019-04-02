package it.vitalegi.neat.impl;

public class Connection {

	private long id;
	private Node fromNode;
	private Node toNode;
	private boolean enabled;
	private double weight;

	public static Connection newInstance(UniqueId uniqueId, Node fromNode, Node toNode, double weight,
			boolean enabled) {

		return newInstance(uniqueId, uniqueId.nextConnectionId(fromNode.getId(), toNode.getId()), fromNode, toNode,
				weight, enabled);
	}

	public static Connection newInstance(UniqueId uniqueId, long id, Node fromNode, Node toNode, double weight,
			boolean enabled) {

		Connection con = new Connection();
		con.setId(uniqueId.nextConnectionId(id, fromNode.getId(), toNode.getId()));
		con.setFromNode(fromNode);
		con.setToNode(toNode);
		con.setWeight(weight);
		con.setEnabled(enabled);
		return con;
	}

	@Override
	public String toString() {
		return "Connection [id=" + id + ", fromNode=" + fromNode + ", toNode=" + toNode + ", enabled=" + enabled
				+ ", weight=" + weight + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Node getFromNode() {
		return fromNode;
	}

	public void setFromNode(Node fromNode) {
		this.fromNode = fromNode;
	}

	public Node getToNode() {
		return toNode;
	}

	public void setToNode(Node toNode) {
		this.toNode = toNode;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

}