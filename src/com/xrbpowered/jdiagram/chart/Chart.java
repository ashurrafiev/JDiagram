package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;

public abstract class Chart {

	public int chartWidth = 750;
	public int chartHeight = 300;
	public boolean clipChart = false;
	
	public int marginLeft = 50;
	public int marginRight = 20;
	public int marginTop = 40;
	public int marginBottom = 60;
	
	public String chartAreaStyle = "fill:#fff;stroke:#ddd;stroke-width:0.5";
	public String gridLineStyle = "fill: none; stroke-width: 0.5; stroke: #ddd";
	public String axisLineStyle = "none; stroke-width: 1; stroke: #555";

	public String textStyle = "font-family:Arial, Helvetica, sans-serif;font-size:9pt;fill:#00";
	
	public String title = null;
	public String titleStyle = "font-weight:bold;font-size:11pt";
	public Anchor titleAnchorX = Anchor.middle;
	public Anchor titleAnchorY = Anchor.top.offset(15);

	public Chart setTitle(String title) {
		this.title = title;
		return this;
	}

	public Chart setTitle(String title, Anchor anchorX, Anchor anchorY) {
		this.title = title;
		this.titleAnchorX = anchorX;
		this.titleAnchorY = anchorY;
		return this;
	}

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
