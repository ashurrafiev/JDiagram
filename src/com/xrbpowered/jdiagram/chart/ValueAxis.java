package com.xrbpowered.jdiagram.chart;

import java.util.Iterator;

public class ValueAxis {

	public boolean log = false;
	
	public double min = 0;
	public double max = 0;
	public double gridStep = 0;

	public Anchor anchor = Anchor.zero;
	public String label = null;
	public Anchor labelAnchor = Anchor.left.offset(-30);
	public String numberFmt = "%.1e";

	public ValueAxis setLogRange(double min, double max) {
		return setRange(true, min, max, 10);
	}

	public ValueAxis setLogRange(double min, double max, double step) {
		return setRange(true, min, max, step);
	}

	public ValueAxis setRange(double min, double max, double step) {
		return setRange(log, min, max, step);
	}

	public ValueAxis setRange(boolean log, double min, double max, double step) {
		this.log = log;
		this.min = min;
		this.max = max;
		this.gridStep = step;
		return this;
	}
	
	public ValueAxis setAnchor(Anchor anchor) {
		this.anchor = anchor;
		return this;
	}
	
	public ValueAxis setLabel(String label, Anchor labelAnchor) {
		this.label = label;
		this.labelAnchor = labelAnchor;
		return this;
	}

	public ValueAxis setLabel(String label) {
		this.label = label;
		return this;
	}

	public ValueAxis setNumberFmt(String fmt) {
		this.numberFmt = fmt;
		return this;
	}

	public double calc(double v) {
		if(log)
			return v<=0 ? 0 : Math.log(v/min) / Math.log(max/min);
		else
			return (v-min) / (max-min);
	}

	public double zero() {
		return log ? 1 : 0;
	}
	
	public Iterator<Double> gridPoints() {
		return new Iterator<Double>() {
			private Double x = null; 
			@Override
			public boolean hasNext() {
				return (x==null || x<max) && gridStep>zero() && Double.isFinite(x);
			}
			@Override
			public Double next() {
				x = (x==null) ? min : log ? x*gridStep : x+gridStep;
				return x;
			}
		};
	}

	
}
