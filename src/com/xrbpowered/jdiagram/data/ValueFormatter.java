package com.xrbpowered.jdiagram.data;

@FunctionalInterface
public interface ValueFormatter<T> {

	public String format(T x);

}
