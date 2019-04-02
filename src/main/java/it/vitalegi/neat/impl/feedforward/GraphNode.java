package it.vitalegi.neat.impl.feedforward;

import java.util.ArrayList;
import java.util.List;

import it.vitalegi.neat.impl.function.SigmoidFunction;

public class GraphNode {

	private long id;
	private List<GraphConnection> to;
	private double inputsSum;
	private double outputValue;
	private int expectedInputs;
	private int actualInputs;
	private boolean feededSuccessors;

	public GraphNode(long id) {
		this.id = id;
		to = new ArrayList<>();
		inputsSum = 0;
		outputValue = 0;
		expectedInputs = 0;
		actualInputs = 0;
		feededSuccessors = false;
	}

	public long getId() {
		return id;
	}

	public void addSuccessor(GraphConnection connection) {
		to.add(connection);
	}

	protected boolean isPredecessor(long id) {
		return to.stream().map(GraphConnection::getToNode).map(GraphNode::getId)//
				.anyMatch(toId -> toId == id);
	}

	public boolean allInputsReceived() {
		return actualInputs >= expectedInputs;
	}

	public void feedSuccessors() {
		if (feededSuccessors) {
			return;
		}
		if (!allInputsReceived()) {
			return;
		}
		feededSuccessors = true;
		to.forEach(c -> c.getToNode().feed(c.getWeight() * outputValue));
	}

	public void setInputsSum(double value) {
		inputsSum = value;
		outputValue = value;
	}

	public void feed(double value) {
		inputsSum += value;
		outputValue = calculateOutputValue();
		actualInputs++;
	}

	public double getInputsSum() {
		return inputsSum;
	}

	public double getOutputValue() {
		return outputValue;
	}

	public double calculateOutputValue() {
		return SigmoidFunction.customSigmoid(inputsSum);
	}
}
