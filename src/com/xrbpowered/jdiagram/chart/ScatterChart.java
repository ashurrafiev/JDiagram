package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;
import java.util.ArrayList;

import com.xrbpowered.jdiagram.data.Data;

public class ScatterChart {

	public static class Population {
		public final String legend;
		public final Data data;
		public final String hdrx;
		public final String hdry;
		public String style;
		
		public Population(String legend, Data data, String hdrx, String hdry, String style) {
			this.legend = legend;
			this.data = data;
			this.hdrx = hdrx;
			this.hdry = hdry;
			this.style = style;
		}
	}
	
	public int chartWidth = 750;
	public int chartHeight = 300;
	public boolean clipChart = false;
	
	public int marginLeft = 50;
	public int marginRight = 5;
	public int marginTop = 20;
	public int marginBottom = 50;
	
	public boolean xLog = false;
	public boolean yLog = false;
	
	public double minXValue = 0;
	public double minYValue = 0;
	public double maxXValue = 0;
	public double maxYValue = 0;

	public String title = null;
	public String xLabel = null;
	public String yLabel = null;
	public String xLabelFmt = "%.1f";
	public String yLabelFmt = "%.1f";
	
	public ArrayList<Population> populations = new ArrayList<>();
	
	public ScatterChart setXRange(double min, double max) {
		this.minXValue = min;
		this.maxXValue = max;
		return this;
	}

	public ScatterChart setYRange(double min, double max) {
		this.minYValue = min;
		this.maxYValue = max;
		return this;
	}
	
	public void addPopulation(Population pop) {
		populations.add(pop);
	}
	
	public int getWidth() {
		return chartWidth+marginLeft+marginRight;
	}
	
	public int getHeight() {
		return chartHeight+marginTop+marginBottom;
	}
	
	protected double calcx(double v) {
		return calcx(v, minXValue, maxXValue, chartWidth, xLog);
	}

	protected double calcy(double v) {
		return calcy(v, minYValue, maxYValue, chartHeight, yLog);
	}

	protected double calcx(double v, double min, double max, int width, boolean log) {
		if(log)
			return width * Math.log(v/min) / Math.log(max/min);
		else
			return width * (v-min) / (max-min);
	}

	protected double calcy(double v, double min, double max, int height, boolean log) {
		return -calcx(v, min, max, height, log);
	}

	public void print(PrintStream out) {
		out.printf("<svg width=\"%d\" height=\"%d\" xmlns=\"http://www.w3.org/2000/svg\">\n",
				chartWidth+marginLeft+marginRight, chartHeight+marginTop+marginBottom);
		
		out.println("<style>");
		out.println("text { font-family: Arial, Helvetica, sans-serif; font-size: 10px; fill: #000 }");
		out.println(".axis { fill: none; stroke-width: 1; stroke: #000 }");
		out.println(".grid { fill: none; stroke-width: 0.5; stroke: #ddd }");
		out.println("</style>");

		out.println("<defs>");
		if(clipChart) {
			out.println("<clipPath id=\"clipChart\">");
			out.printf("<rect x=\"0\" y=\"%d\" width=\"%d\" height=\"%d\" fill=\"#fff\" />", -chartHeight, chartWidth, chartHeight);
			out.println("</clipPath>");
		}
		out.println("</defs>");

		out.printf("<g transform=\"translate(%d %d)\">\n", marginLeft, chartHeight+marginTop);
		
		// grid
		out.printf("<rect x=\"0\" y=\"%d\" width=\"%d\" height=\"%d\" style=\"fill:#fff;stroke:#ddd;stroke-width:0.5\" />\n", -chartHeight, chartWidth, chartHeight);
		out.println("<g>");
		gridx(out, minXValue, maxXValue, chartWidth, chartHeight, xLog);
		gridy(out, minYValue, maxYValue, chartWidth, chartHeight, yLog, 5);
		out.println("</g>");

		out.println(clipChart ? "<g style=\"clip-path:url(#clipChart)\">" : "<g>");
		for(Population pop : populations) {
			StringBuilder path = new StringBuilder();
			boolean first = true;
			for(Data.Row row : pop.data.rows) {
				double x = calcx(row.getNum(pop.hdrx));
				double y = calcy(row.getNum(pop.hdry));
				if(first) {
					path.append("M");
					first = false;
				}
				else
					path.append(" L");
				path.append(String.format("%.1f,%.1f", x, y));
			}
			out.printf("<path d=\"%s\" style=\"%s\" />\n", path.toString(), pop.style);
		}
		out.println("</g>");

		// axes
		out.printf("<line class=\"axis\" x1=\"%.1f\" y1=\"0\" x2=\"%.1f\" y2=\"%d\" />\n", calcx(0), calcx(0), -chartHeight);
		if(yLabel!=null)
			out.printf("<text x=\"-40\" y=\"%d\" text-anchor=\"middle\" transform=\"rotate(-90 -40,%d)\">%s</text>\n", -chartHeight/2, -chartHeight/2, yLabel);
		out.printf("<line class=\"axis\" x1=\"0\" y1=\"%.1f\" x2=\"%d\" y2=\"%.1f\" />\n", calcy(0), chartWidth, calcy(0));
		if(xLabel!=null)
			out.printf("<text x=\"%d\" y=\"25\" text-anchor=\"middle\">%s</text>\n", chartWidth/2, xLabel);
		if(title!=null)
			out.printf("<text x=\"%d\" y=\"%d\" text-anchor=\"middle\" font-weight=\"bold\">%s</text>\n", chartWidth/2, -chartHeight-10, title);
		
		// legend
		int lw = 60;
		int x = chartWidth/2 - (populations.size()*lw)/2;
		for(Population pop : populations) {
			out.printf("<line x1=\"%d\" y1=\"40\" x2=\"%d\" y2=\"40\" style=\"%s\" />\n", x, x+30, pop.style);
			out.printf("<text x=\"%d\" y=\"43\">%s</text>\n", x+35, pop.legend);
			x += lw;
		}
		
		out.println("</g>");
		out.println("</svg>");
	}

	protected void gridxLine(PrintStream out, double x, double d, int height) {
		out.printf("<text x=\"%.1f\" y=\"15\" text-anchor=\"middle\">"+xLabelFmt+"</text>\n", x, d); // TODO label format
		out.printf("<line class=\"grid\" x1=\"%.1f\" y1=\"0\" x2=\"%.1f\" y2=\"%d\" />\n", x, x, -height);
	}
	
	protected void gridx(PrintStream out, double min, double max, int width, int height, boolean log) {
		if(log) {
			for(double d=1; d<=max; d*=10) {
				double x = calcx(d, min, max, width, true);
				gridxLine(out, x, d, height);
			}
		}
		else {
			double dd;
			for(dd=1; dd*15<(max-min); dd*=10) {}
			for(double d=min; d<=max; d+=dd) {
				double x = calcx(d, min, max, width, false);
				gridxLine(out, x, d, height);
			}
		}
	}
	
	protected void gridyLine(PrintStream out, double y, double d, int width, int yoffs) {
		out.printf("<text x=\"-5\" y=\"%.1f\" text-anchor=\"end\" >"+yLabelFmt+"</text>\n", y+yoffs, d); // TODO label format
		out.printf("<line class=\"grid\" x1=\"0\" y1=\"%.1f\" x2=\"%d\" y2=\"%.1f\" />\n", y, width, y);
	}
	
	protected void gridy(PrintStream out, double min, double max, int width, int height, boolean log, int yoffs) {
		if(log) {
			for(double d=min; d<=max; d*=10) {
				double y = calcy(d, min, max, height, true);
				gridyLine(out, y, d, width, yoffs);
			}
		}
		else {
			double dd;
			for(dd=1; dd*15<(max-min); dd*=10) {}
			for(double d=min; d<=max; d+=dd) {
				double y = calcy(d, min, max, height, false);
				gridyLine(out, y, d, width, yoffs);
			}
		}
	}
}
