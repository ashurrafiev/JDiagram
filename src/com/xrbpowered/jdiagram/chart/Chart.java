package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;

public abstract class Chart {

	public int chartWidth = 750;
	public int chartHeight = 300;
	public boolean clipChart = false;
	
	public int marginLeft = 50;
	public int marginRight = 20;
	public int marginTop = 40;
	public int marginBottom = 70;
	
	public String chartAreaStyle = "fill:#fff;stroke:#ddd;stroke-width:0.5";
	public String gridLineStyle = "fill: none; stroke-width: 0.5; stroke: #ddd";
	public String axisLineStyle = "none; stroke-width: 1; stroke: #555";
	public String frameStyle = null;

	public String title = null;
	public String titleStyle = "font-weight:bold;font-size:11pt";
	public Anchor titleAnchorX = Anchor.middle;
	public Anchor titleAnchorY = Anchor.top.offset(15);

	public Legend legend = (Legend) new Legend(0).setItemSize(80, 20).setGap(20, 0);

	public String id = "";
	
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
	
	protected double calcx(Anchor a) {
		return a.calc(0, chartWidth);
	}

	protected double calcy(Anchor a) {
		return -a.calc(0, chartHeight);
	}

	protected double calcx(Anchor a, double mid) {
		return a.calc(0, mid, chartWidth);
	}

	protected double calcy(Anchor a, double mid) {
		return -a.calc(0, -mid, chartHeight);
	}

	protected double calcLeft(Anchor a, double width, boolean inside) {
		return a.calc(0, chartWidth)+a.startOffs(width, inside);
	}

	protected double calcTop(Anchor a, double height, boolean inside) {
		return -a.calc(0, chartHeight)-a.startOffs(height, inside)-height;
	}
	
	public int getWidth() {
		return chartWidth+marginLeft+marginRight;
	}
	
	public int getHeight() {
		return chartHeight+marginTop+marginBottom;
	}
	
	protected void printGrid(PrintStream out) {
	}

	protected void printData(PrintStream out) {
	}

	protected void printAxes(PrintStream out) {
	}
	
	public void printChart(PrintStream out) {
		if(frameStyle!=null)
			out.printf("<rect x=\"0\" y=\"0\" width=\"%d\" height=\"%d\" style=\"%s\" />\n", getWidth(), getHeight(), frameStyle);
		out.printf("<g transform=\"translate(%d %d)\">\n", marginLeft, chartHeight+marginTop);
		
		if(chartAreaStyle!=null)
			out.printf("<rect x=\"0\" y=\"%d\" width=\"%d\" height=\"%d\" style=\"%s\" />\n", -chartHeight, chartWidth, chartHeight, chartAreaStyle);
		printGrid(out);

		// data
		if(clipChart)
			out.printf("<g style=\"clip-path:url(#clipChart_%s)\">\n", id);
		else
			out.println("<g>");
		printData(out);
		out.println("</g>");

		printAxes(out);
		if(title!=null)
			out.printf("<text x=\"%.1f\" y=\"%.1f\" text-anchor=\"%s\" style=\"%s\">%s</text>\n", calcx(titleAnchorX), calcy(titleAnchorY), titleAnchorX.innerAlign(), titleStyle, title);
		legend.printItems(out, calcLeft(legend.anchorX, legend.getWidth(), legend.inside), calcTop(legend.anchorY, legend.getHeight(), legend.inside));
		
		out.println("</g>");
	}
	
	protected void printStyles(PrintStream out) {
	}

	protected void printDefs(PrintStream out) {
		// FIXME conflict between charts
		if(clipChart) {
			out.printf("<clipPath id=\"clipChart_%s\">\n", id);
			out.printf("<rect x=\"0\" y=\"%d\" width=\"%d\" height=\"%d\" fill=\"#fff\" />", -chartHeight, chartWidth, chartHeight);
			out.println("</clipPath>");
		}
	}

	public void printPage(PrintStream out) {
		new Page(1).add(this).printPage(out);
	}
}
