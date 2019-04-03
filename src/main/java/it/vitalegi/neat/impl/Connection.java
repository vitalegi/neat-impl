package it.vitalegi.neat.impl;

public class Connection {

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
	public static Connection newInstance(UniqueId uniqueId, Node fromNode, Node toNode, double weight,
			boolean enabled) {

		return newInstance(uniqueId, uniqueId.nextConnectionId(fromNode.getId(), toNode.getId()), fromNode, toNode,
				weight, enabled);
	}
	private boolean enabled;
	private Node fromNode;
	private long id;

	private Node toNode;

	private double weight;

	public Node getFromNode() {
		return fromNode;
	}

	public long getId() {
		return id;
	}

	public Node getToNode() {
		return toNode;
	}

	public double getWeight() {
		return weight;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setFromNode(Node fromNode) {
		this.fromNode = fromNode;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setToNode(Node toNode) {
		this.toNode = toNode;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return "Connection [id=" + id + ", fromNode=" + fromNode + ", toNode=" + toNode + ", enabled=" + enabled
				+ ", weight=" + weight + "]";
	}

}