package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;

import com.xrbpowered.jdiagram.data.Data;

public abstract class DataRenderer {

	protected PrintStream out;
	protected String style;
	
	public void start(PrintStream out, String style, int rowCount, Data data) {
		this.out = out;
		this.style = style;
	}
	public abstract void addPoint(double x, double y, Data.Row row);
	public abstract void finish();
	public abstract void printLegendSwatch(PrintStream out, double x, double y, int w, int h, String style);
	
	public static void printLineSwatch(PrintStream out, double x, double y, int w, int h, String style) {
		out.printf("<line x1=\"%.1f\" y1=\"%.1f\" x2=\"%.1f\" y2=\"%.1f\" style=\"%s\" />\n", x, y+h/2, x+w, y+h/2, style);
	}
	
	public static void printBoxSwatch(PrintStream out, double x, double y, int w, int h, String style) {
		out.printf("<rect x=\"%.1f\" y=\"%.1f\" width=\"%.1f\" height=\"%.1f\" style=\"%s\" />\n", x+w-h, y, h, h, style);
	}
}
