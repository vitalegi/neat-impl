package it.vitalegi.neat.impl;

public class Node {

	public static Node newBiasInstance(UniqueId uniqueId, long id) {
		return new Node(uniqueId, id, false, false, true);
	}

	public static Node newInputInstance(UniqueId uniqueId, long id) {

		return new Node(uniqueId, id, true, false, false);
	}

	public static Node newInstance(UniqueId uniqueId, long id) {

		return new Node(uniqueId, id, false, false, false);
	}

	public static Node newOutputInstance(UniqueId uniqueId, long id) {
		return new Node(uniqueId, id, false, true, false);
	}

	private boolean bias;
	private long id;
	private boolean input;
	private boolean output;

	public Node() {
		super();
	}

	public Node(UniqueId uniqueId, long id, boolean input, boolean output, boolean bias) {
		super();
		this.id = uniqueId.nextNodeId(id);
		this.input = input;
		this.output = output;
		this.bias = bias;
	}

	public long getId() {
		return id;
	}

	public boolean isBias() {
		return bias;
	}

	public boolean isInput() {
		return input;
	}

	public boolean isOutput() {
		return output;
	}

	public void setBias(boolean bias) {
		this.bias = bias;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setInput(boolean input) {
		this.input = input;
	}

	public void setOutput(boolean output) {
		this.output = output;
	}

	@Override
	public String toString() {
		return "Node [id=" + id + "]";
	}
}
