package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;

public abstract class Axis {

	public Anchor anchor = Anchor.zero;
	public String label = null;
	public Anchor labelAnchor = Anchor.left.offset(-30);

	public Axis setAnchor(Anchor anchor) {
		this.anchor = anchor;
		return this;
	}
	
	public Axis setLabel(String label, Anchor labelAnchor) {
		this.label = label;
		this.labelAnchor = labelAnchor;
		return this;
	}

	public Axis setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public abstract double calc(double v);
	
	public double zero() {
		return 0;
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
