package it.vitalegi.neat.impl;

public class Node {

	private long id;
	private boolean input;
	private boolean output;

	public static Node newInstance(UniqueId uniqueId, long id) {

		return new Node(uniqueId, id, false, false);
	}

	public static Node newInputInstance(UniqueId uniqueId, long id) {

		return new Node(uniqueId, id, true, false);
	}

	public static Node newOutputInstance(UniqueId uniqueId, long id) {
		return new Node(uniqueId, id, false, true);
	}

	public Node(UniqueId uniqueId, long id, boolean input, boolean output) {
		super();
		this.id = uniqueId.nextNodeId(id);
		this.input = input;
		this.output = output;
	}

	@Override
	public String toString() {
		return "Node [id=" + id + "]";
	}

	public Node() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isInput() {
		return input;
	}

	public void setInput(boolean input) {
		this.input = input;
	}

	public boolean isOutput() {
		return output;
	}

	public void setOutput(boolean output) {
		this.output = output;
	}
}
