package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;

public abstract class Chart {

	public int chartWidth = 750;
	public int chartHeight = 300;
	public boolean clipChart = false;
	
	public int marginLeft = 40;
	public int marginRight = 10;
	public int marginTop = 30;
	public int marginBottom = 50;
	
	public String title = null;
	
	public String chartAreaStyle = "fill:#fff;stroke:#ddd;stroke-width:0.5";
	public String gridLineStyle = "fill: none; stroke-width: 0.5; stroke: #ddd";
	public String axisLineStyle = "none; stroke-width: 1; stroke: #000";

	public Chart setSize(int w, int h) {
		this.chartWidth = w;
		this.chartHeight = h;
		return this;
	}
	
	public Chart setMargins(int left, int right, int top, int bottom) {
		this.marginLeft = left;
		this.marginRight = right;
		this.marginTop = top;
		this.marginBottom = bottom;
		return this;
	}
	
	public int getWidth() {
		return chartWidth+marginLeft+marginRight;
	}
	
	public int getHeight() {
		return chartHeight+marginTop+marginBottom;
	}
	
	public abstract void print(PrintStream out);

}
