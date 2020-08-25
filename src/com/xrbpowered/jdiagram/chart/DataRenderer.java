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
	
}
