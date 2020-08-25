package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;

import com.xrbpowered.jdiagram.data.Data;
import com.xrbpowered.jdiagram.data.Data.Row;

public class LineRenderer extends DataRenderer {

	protected StringBuilder path;
	protected boolean first;
	
	@Override
	public void start(PrintStream out, String style, int rowCount, Data data) {
		super.start(out, style, rowCount, data);
		path = new StringBuilder();
		first = true;
	}
	
	@Override
	public void addPoint(double x, double y, Row row) {
		path.append(first ? "M" : " L");
		path.append(String.format("%.1f,%.1f", x, y));
		first = false;
	}

	@Override
	public void finish() {
		out.printf("<path d=\"%s\" style=\"%s\" />\n", path.toString(), style);
	}

}
