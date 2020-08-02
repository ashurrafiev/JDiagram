package com.xrbpowered.jdiagram.chart;

public class Anchor {

	public final double offs;
	
	protected final int align;
	
	protected Anchor(int align, double offs) {
		this.align = align;
		this.offs = offs;
	}
	
	public Anchor offset(double offs) {
		return new Anchor(this.align, this.offs + offs);
	}
	
	public Anchor opposite() {
		return new Anchor(2-align, -offs);
	}
	
	public boolean aligned(Anchor a) {
		return this.align==a.align;
	}

	public double calc(double min, double max) {
		return min + (max-min)*(align/2.0) + offs;
	}

	public double calc(double min, double mid, double max) {
		return align==1 ? mid+offs : calc(min, max);
	}
	
	public double startOffs(double size, boolean inside) {
		if(inside)
			return -size*(align/2.0);
		else
			return -size*((2-align)/2.0);
	}

	public String innerAlign() {
		return innerAlign(align);
	}

	public String outerAlign() {
		return innerAlign(2-align);
	}

	protected static String innerAlign(int a) {
		switch(a) {
			case 0: return "start";
			case 2: return "end";
			default: return "middle";
		}
	}
	
	public static Anchor left = new Anchor(0, 0);
	public static Anchor bottom = left;
	public static Anchor middle = new Anchor(1, 0);
	public static Anchor zero = middle;
	public static Anchor right = new Anchor(2, 0);
	public static Anchor top = right;
	
	
}
