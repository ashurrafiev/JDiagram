package com.xrbpowered.jdiagram.data;

public class ExpFormatter implements NumberFormatter {

	protected final String formatString;
	protected final boolean showMantissa;
	
	/**
	 * Exponent base.
	 */
	public final int base;
	
	protected ExpFormatter(int base, String mantissaFmt, String mulSign, String baseExpFmt) {
		this.base = base;
		if(mantissaFmt!=null) {
			this.formatString = mantissaFmt+mulSign+baseExpFmt;
			this.showMantissa = true;
		}
		else {
			this.formatString = baseExpFmt;
			this.showMantissa = false;
		}
	}
	
	@Override
	public String format(Double x) {
		if(x==null)
			return null;
		int exp = (int)Math.round(Math.log(x)/Math.log(base));
		if(showMantissa) {
			double mantissa = x / Math.pow(base, exp);
			return String.format(formatString, mantissa, base, exp);
		}
		else
			return String.format(formatString, base, exp);
	}

	/**
	 * Plain text format as an integer power of a specific base written as {@code "B^X"},
	 * where B is the base and X is the power (exponent).
	 * <p>
	 * Note that the number is rounded to the nearest integer power of {@code base}.
	 * If you need precise expression, use {@linkplain #plainExp(int, String) mantissa version} instead.
	 * 
	 * @param base the base of exponent; for example, in {@code 2^X}, 2 is the base
	 * @return number formatter object encapsulating the rule
	 * @see #plainExp(int, String)
	 */
	public static ExpFormatter plainExp(int base) {
		return plainExp(base, null);
	}

	/**
	 * Plain text format as mantissa and exponent in a specific base written as {@code "M*B^X"},
	 * where M is mantissa, B is the base, and X is the power (exponent).
	 * 
	 * @param base the base of exponent; for example, in {@code 2^X}, 2 is the base
	 * @param mantissaFmt mantissa format string compatible with {@link NumberFormatter#simple(String) simple number format}.
	 *   <p>
	 *   Format string should expect mantissa to be a floating point number. Valid examples are:
	 *   {@code "%f"}, {@code "%.1f"}, {@code "%+.3f"}, and so on. Using {@code "%e"} is not recommended
	 *   as it would not make sense in the final result.
	 * @return number formatter object encapsulating the rule
	 * @see #plainExp(int)
	 */
	public static ExpFormatter plainExp(int base, String mantissaFmt) {
		return new ExpFormatter(base, mantissaFmt, "*", "%d^%d");
	}

	/**
	 * SVG-compatible format as an integer power of a specific base written in superscript form using {@code <tspan>} tag.
	 * The exact format is:
	 * <pre>{@code B<tspan dy="dy" style="font-size:smaller">X</tspan>}</pre>
	 * where B is the base and X is the power (exponent); {@code dy} is the superscript offset.
	 * The formatted string can be used within SVG tag {@code <text>} and should appear as:
	 * <blockquote>B<sup>X</sup></blockquote>
	 * <p>
	 * Note that the number is rounded to the nearest integer power of {@code base}.
	 * If you need precise expression, use {@linkplain #svgExp(int, String, double) mantissa version} of instead.
	 * 
	 * @param base the base of exponent; for example, in 2<sup>X</sup>, 2 is the base
	 * @param dy superscript offset in pixels
	 * @return number formatter object encapsulating the rule
	 * @see #svgExp(int, String, double)
	 * @see #plainExp(int)
	 */
	public static ExpFormatter svgExp(int base, double dy) {
		return svgExp(base, null, dy);
	}

	/**
	 * SVG-compatible format as mantissa and exponent in a specific base written in superscript form using {@code <tspan>} tag.
	 * The exact format is:
	 * <pre>{@code M&#x00b7;B<tspan dy="dy" style="font-size:smaller">X</tspan>}</pre>
	 * where M is mantissa, B is the base, and X is the power (exponent); {@code dy} is the superscript offset.
	 * The formatted string can be used within SVG tag {@code <text>} and should appear as:
	 * <blockquote>M&#x00b7;B<sup>X</sup></blockquote>
	 * 
	 * @param base the base of exponent; for example, in 2<sup>X</sup>, 2 is the base
	 * @param mantissaFmt mantissaFmt mantissa format string compatible with {@link NumberFormatter#simple(String) simple number format}.
	 *   <p>
	 *   Format string should expect mantissa to be a floating point number. Valid examples are:
	 *   {@code "%f"}, {@code "%.1f"}, {@code "%+.3f"}, and so on. Using {@code "%e"} is not recommended
	 *   as it would not make sense in the final result.
	 * @param dy superscript offset in pixels
	 * @return number formatter object encapsulating the rule
	 * @see #svgExp(int, double)
	 * @see #plainExp(int, String)
	 */
	public static ExpFormatter svgExp(int base, String mantissaFmt, double dy) {
		return new ExpFormatter(base, mantissaFmt, "&#x00b7;",
				String.format("%%d<tspan dy=\"%.1f\" style=\"font-size:smaller\">%%d</tspan>", -dy));
	}
}
