package it.vitalegi.neat.impl;

public class Node {

	public static Node newInputInstance(UniqueId uniqueId, long id) {

		return new Node(uniqueId, id, true, false);
	}

	public static Node newInstance(UniqueId uniqueId, long id) {

		return new Node(uniqueId, id, false, false);
	}

	public static Node newOutputInstance(UniqueId uniqueId, long id) {
		return new Node(uniqueId, id, false, true);
	}

	private long id;
	private boolean input;
	private boolean output;

	public Node() {
		super();
	}

	public Node(UniqueId uniqueId, long id, boolean input, boolean output) {
		super();
		this.id = uniqueId.nextNodeId(id);
		this.input = input;
		this.output = output;
	}

	public long getId() {
		return id;
	}

	public boolean isInput() {
		return input;
	}

	public boolean isOutput() {
		return output;
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
