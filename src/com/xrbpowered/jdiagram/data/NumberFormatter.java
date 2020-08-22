package com.xrbpowered.jdiagram.data;

@FunctionalInterface
public interface NumberFormatter extends ValueFormatter<Double> {
	
	/**
	 * Simple number format.
	 * 
	 * @param fmt format string
	 * @return number formatter object
	 */
	public static NumberFormatter simple(final String fmt) {
		return new NumberFormatter() {
			@Override
			public String format(Double x) {
				return x==null ? null : String.format(fmt, x);
			}
		};
	}
	
	public static NumberFormatter percent = new NumberFormatter() {
		@Override
		public String format(Double v) {
			return v==null ? null : String.format("%.2f%%", v*100.0);
		}
	};
	
}
