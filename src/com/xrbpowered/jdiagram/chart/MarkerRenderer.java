package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;

import com.xrbpowered.jdiagram.data.Data;
import com.xrbpowered.jdiagram.data.Data.Row;

public class MarkerRenderer extends DataRenderer {

	public String href;
	
	public MarkerRenderer(String href) {
		this.href = href;
	}
	
	@Override
	public void start(PrintStream out, String style, int rowCount, Data data) {
		super.start(out, style, rowCount, data);
		out.printf("<g style=\"%s\">\n", style);
	}
	
	@Override
	public void addPoint(double x, double y, Row row) {
		out.printf("<use xlink:href=\"#%s\" x=\"%.1f\" y=\"%.1f\" />\n", href, x, y);
	}
	
	@Override
	public void finish() {
		out.println("</g>");
	}

	@Override
	public void printLegendSwatch(PrintStream out, double x, double y, int w, int h, String style) {
		out.printf("<use xlink:href=\"#%s\" x=\"%.1f\" y=\"%.1f\" style=\"%s\" />\n", href, x+w-h/2, y+h/2, style);
	}

}
