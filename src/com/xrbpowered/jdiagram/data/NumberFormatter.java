package com.xrbpowered.jdiagram.data;

@FunctionalInterface
public interface NumberFormatter extends ValueFormatter<Double> {
	
	public static NumberFormatter simple(final String fmt) {
		return new NumberFormatter() {
			@Override
			public String format(Double x) {
				return x==null ? null : String.format(fmt, x);
			}
		};
	}
	
	public static NumberFormatter exp(final int base, final String mantissaFmt) {
		return new NumberFormatter() {
			@Override
			public String format(Double x) {
				if(x==null)
					return null;
				int exp = (int)Math.round(Math.log(x)/Math.log(base));
				if(mantissaFmt==null)
					return String.format("%d<tspan dy=\"-5\" style=\"font-size:smaller\">%d</tspan>", base, exp); // TODO proper dy
				else {
					double mantissa = x / Math.pow(base, exp);
					return String.format(mantissaFmt+"&#x00b7;%d<tspan dy=\"-5\" style=\"font-size:smaller\">%d</tspan>", mantissa, base, exp); // TODO proper dy
				}
			}
		};
	}
	
	public static NumberFormatter exp(final int base) {
		return exp(base, null);
	}
	
}
