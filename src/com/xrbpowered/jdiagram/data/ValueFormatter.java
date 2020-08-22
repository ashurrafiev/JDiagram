package com.xrbpowered.jdiagram.data;

/**
 * Interface for converting values of a specific type to strings according to specific rules.
 *
 * @param <T> input value type
 * @see NumberFormatter
 * @see Formula#format(ValueFormatter, Formula)
 */
@FunctionalInterface
public interface ValueFormatter<T> {

	/**
	 * Formats value x according to formatter rules.
	 * 
	 * @param x value to format
	 * @return formatted string
	 */
	public String format(T x);

}
