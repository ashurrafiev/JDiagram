package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;

import com.xrbpowered.jdiagram.data.Data;
import com.xrbpowered.jdiagram.data.Data.Row;

public class NoteRenderer extends DataRenderer {

	public String noteHdr;
	public double dx, dy;
	public String anchor = "middle";
	
	public NoteRenderer(String noteHdr) {
		this.noteHdr = noteHdr;
	}
	
	public NoteRenderer setAnchor(String anchor, double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
		this.anchor = anchor;
		return this;
	}
	
	@Override
	public void start(PrintStream out, String style, int rowCount, Data data) {
		super.start(out, style, rowCount, data);
		out.printf("<g style=\"%s\">\n", style);
	}
	
	@Override
	public void addPoint(double x, double y, Row row) {
		out.printf("<text x=\"%.1f\" y=\"%.1f\" text-anchor=\"%s\">%s</text>\n", x+dx, y+dy, anchor, row.get(noteHdr));
	}
	
	@Override
	public void finish() {
		out.println("</g>");
	}

	@Override
	public void printLegendSwatch(PrintStream out, double x, double y, int w, int h, String style) {
		throw new UnsupportedOperationException();
	}
}
