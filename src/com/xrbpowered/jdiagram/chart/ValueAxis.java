package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;
import java.util.Iterator;

import com.xrbpowered.jdiagram.data.NumberFormatter;

public class ValueAxis {

	public boolean log = false;
	
	public double min = 0;
	public double max = 0;
	public double gridStep = 0;

	public Anchor anchor = Anchor.zero;
	public String label = null;
	public Anchor labelAnchor = Anchor.left.offset(-30);
	public NumberFormatter numberFmt = NumberFormatter.simple("%.1e");

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

	public ValueAxis setNumberFormatter(NumberFormatter numberFmt) {
		this.numberFmt = numberFmt;
		return this;
	}

	public ValueAxis setNumberFmt(String fmt) {
		this.numberFmt = NumberFormatter.simple(fmt);
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
				return (x==null || x<max && Double.isFinite(x)) && gridStep>zero();
			}
			@Override
			public Double next() {
				x = (x==null) ? min : log ? x*gridStep : x+gridStep;
				return x;
			}
		};
	}

	public double calcx(Chart chart, double v) {
		return chart.chartWidth * calc(v);
	}

	public double calcy(Chart chart, double v) {
		return -chart.chartHeight * calc(v);
	}
	
	public double zerox(Chart chart) {
		return calcx(chart, zero());
	}

	public double zeroy(Chart chart) {
		return calcy(chart, zero());
	}

	public void gridxLine(PrintStream out, Chart chart, double v) {
		double x = calcx(chart, v);
		out.printf("<line x1=\"%.1f\" y1=\"0\" x2=\"%.1f\" y2=\"%d\" />\n", x, x, -chart.chartHeight);
	}
	
	public void gridyLine(PrintStream out, Chart chart, double v) {
		double y = calcy(chart, v);
		out.printf("<line x1=\"0\" y1=\"%.1f\" x2=\"%d\" y2=\"%.1f\" />\n", y, chart.chartWidth, y);
	}

	public void gridxNumber(PrintStream out, Chart chart, double v) {
		// TODO grid number positioning
		String s = numberFmt.format(v);
		if(s!=null)
			out.printf("<text x=\"%.1f\" y=\"15\" text-anchor=\"middle\">%s</text>\n", calcx(chart, v), s);
	}
	
	public void gridyNumber(PrintStream out, Chart chart, double v) {
		// TODO grid number positioning
		String s = numberFmt.format(v);
		if(s!=null)
			out.printf("<text x=\"-5\" y=\"%.1f\" text-anchor=\"end\" >%s</text>\n", calcy(chart, v)+5, s); // TODO proper numberOffs configuration
	}

	public void printAxisX(PrintStream out, Chart chart, double zeroy) {
		double y = chart.calcy(anchor, zeroy);
		if(chart.axisLineStyle!=null)
			out.printf("<line x1=\"0\" y1=\"%.1f\" x2=\"%d\" y2=\"%.1f\" style=\"%s\" />\n", y, chart.chartWidth, y, chart.axisLineStyle);
		if(label!=null) {
			double labely = chart.calcy(labelAnchor, y);
			out.printf("<text x=\"%d\" y=\"%.1f\" text-anchor=\"middle\">%s</text>\n", chart.chartWidth/2, labely, label);
		}
	}

	public void printAxisY(PrintStream out, Chart chart, double zerox) {
		double x =chart.calcx(anchor, zerox);
		if(chart.axisLineStyle!=null)
			out.printf("<line x1=\"%.1f\" y1=\"0\" x2=\"%.1f\" y2=\"%d\" style=\"%s\" />\n", x, x, -chart.chartHeight, chart.axisLineStyle);
		if(label!=null) {
			double labelx = chart.calcx(labelAnchor, x);
			out.printf("<text x=\"%.1f\" y=\"%d\" text-anchor=\"middle\" transform=\"rotate(-90 %.1f,%d)\">%s</text>\n", labelx, -chart.chartHeight/2, labelx, -chart.chartHeight/2, label);
		}
	}
}
