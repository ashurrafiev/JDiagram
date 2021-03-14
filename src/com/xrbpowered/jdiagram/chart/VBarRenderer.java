package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;

import com.xrbpowered.jdiagram.data.Data;
import com.xrbpowered.jdiagram.data.Data.Row;

public class VBarRenderer extends DataRenderer {

	protected double dx;
	protected double sizex;
	
	@Override
	public double itemSizeX() {
		return 1;
	}
	
	@Override
	public void start(PrintStream out, String style, int rowCount, Data data) {
		super.start(out, style, rowCount, data);
		out.printf("<g style=\"%s\">\n", style);
	}
	
	@Override
	public double startHSet(PrintStream out, String style, int rowCount, Data data, double dx, double sizex) {
		this.dx = dx;
		this.sizex = sizex;
		super.startHSet(out, style, rowCount, data, dx, sizex);
		return dx + sizex;
	}
	
	@Override
	public void addPoint(double x, double y, Row row) {
		out.printf("<rect x=\"%.1f\" y=\"%.1f\" width=\"%.1f\" height=\"%.1f\" />\n",
				x+dx, y<zeroy ? y : zeroy, sizex, y<zeroy ? zeroy-y : y-zeroy);
	}
	
	@Override
	public void finish() {
		out.println("</g>");
	}

	@Override
	public void printLegendSwatch(PrintStream out, double x, double y, int w, int h, String style) {
		printBoxSwatch(out, x, y+4, w, h-8, style);
	}

}
