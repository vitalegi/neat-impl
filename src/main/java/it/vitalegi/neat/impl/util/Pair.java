package it.vitalegi.neat.impl.util;

public class Pair<E, T> {

	private E first;
	private T second;

	public static <E, T> Pair<E, T> newInstance(E first, T second) {
		return new Pair<>(first, second);
	}

	public Pair(E first, T second) {
		super();
		this.first = first;
		this.second = second;
	}

	public E getFirst() {
		return first;
	}

	public T getSecond() {
		return second;
	}

	public void setFirst(E first) {
		this.first = first;
	}

	public void setSecond(T second) {
		this.second = second;
	}

}
