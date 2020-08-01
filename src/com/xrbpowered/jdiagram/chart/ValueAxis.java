package com.xrbpowered.jdiagram.chart;

import java.util.Iterator;

public class ValueAxis {

	public boolean log = false;
	
	public double min = 0;
	public double max = 0;
	public double gridStep = 0;

	public String label = null;
	public String numberFmt = "%.1e";

	public ValueAxis setRange(double min, double max) {
		this.min = min;
		this.max = max;
		return this;
	}

	public ValueAxis setRange(double min, double max, double step) {
		this.min = min;
		this.max = max;
		this.gridStep = step;
		return this;
	}
	
	public ValueAxis setNumberFmt(String fmt) {
		this.numberFmt = fmt;
		return this;
	}

	public double calc(double v) {
		return calc(v, min, max, log);
	}

	public double zero() {
		return log ? 1 : 0;
	}
	
	public Iterator<Double> gridPoints() {
		return new Iterator<Double>() {
			private Double x = null; 
			@Override
			public boolean hasNext() {
				return x==null || x<max && gridStep>zero();
			}
			@Override
			public Double next() {
				x = (x==null) ? min : log ? x*gridStep : x+gridStep;
				return x;
			}
		};
	}
	
	public static double calc(double v, double min, double max, boolean log) {
		if(log)
			return Math.log(v/min) / Math.log(max/min);
		else
			return (v-min) / (max-min);
	}
	
}
