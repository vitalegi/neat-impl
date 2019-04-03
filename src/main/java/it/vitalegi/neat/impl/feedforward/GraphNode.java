package it.vitalegi.neat.impl.feedforward;

import java.util.ArrayList;
import java.util.List;

import it.vitalegi.neat.impl.function.SigmoidFunction;

public class GraphNode {

	private int actualInputs;
	private int expectedInputs;
	private boolean feededSuccessors;
	private long id;
	private double inputsSum;
	private double outputValue;
	private List<GraphConnection> to;

	public GraphNode(long id) {
		this.id = id;
		to = new ArrayList<>();
		inputsSum = 0;
		outputValue = 0;
		expectedInputs = 0;
		actualInputs = 0;
		feededSuccessors = false;
	}

	public void addSuccessor(GraphConnection connection) {
		to.add(connection);
	}

	public boolean allInputsReceived() {
		return actualInputs >= expectedInputs;
	}

	public double calculateOutputValue() {
		return SigmoidFunction.customSigmoid(inputsSum);
	}

	public void feed(double value) {
		inputsSum += value;
		outputValue = calculateOutputValue();
		actualInputs++;
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

	public long getId() {
		return id;
	}

	public double getInputsSum() {
		return inputsSum;
	}

	public double getOutputValue() {
		return outputValue;
	}

	protected boolean isPredecessor(long id) {
		return to.stream().map(GraphConnection::getToNode).map(GraphNode::getId)//
				.anyMatch(toId -> toId == id);
	}

	public void setInputsSum(double value) {
		inputsSum = value;
		outputValue = value;
	}
}
