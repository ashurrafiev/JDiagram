package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;

import com.xrbpowered.jdiagram.data.Data;

public abstract class DataRenderer {

	protected PrintStream out;
	protected String style;
	
	public double zerox = 0;
	public double zeroy = 0;
	
	public void setZeroes(double x, double y) {
		this.zerox = x;
		this.zeroy = y;
	}
	
	public void start(PrintStream out, String style, int rowCount, Data data) {
		this.out = out;
		this.style = style;
	}

	public double startHSet(PrintStream out, String style, int rowCount, Data data, double dx, double sizex) {
		start(out, style, rowCount, data);
		return dx;
	}

	public double startVSet(PrintStream out, String style, int rowCount, Data data, double dy, double sizey) {
		start(out, style, rowCount, data);
		return dy;
	}

	public abstract void addPoint(double x, double y, Data.Row row);
	public abstract void finish();
	public abstract void printLegendSwatch(PrintStream out, double x, double y, int w, int h, String style);
	
	public double itemSizeX() {
		return 0;
	}
	
	public double itemSizeY() {
		return 0;
	}
	
	public static void printLineSwatch(PrintStream out, double x, double y, int w, int h, String style) {
		out.printf("<line x1=\"%.1f\" y1=\"%.1f\" x2=\"%.1f\" y2=\"%.1f\" style=\"%s\" />\n", x, y+h/2, x+w, y+h/2, style);
	}
	
	public static void printBoxSwatch(PrintStream out, double x, double y, int w, int h, String style) {
		out.printf("<rect x=\"%.1f\" y=\"%.1f\" width=\"%d\" height=\"%d\" style=\"%s\" />\n", x+w-h, y, h, h, style);
	}
}
