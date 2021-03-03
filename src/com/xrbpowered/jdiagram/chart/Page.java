package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;
import java.util.ArrayList;

public class Page extends GridLayout {

	public static String globalTextStyle = "font-family:Arial, Helvetica, sans-serif;font-size:9pt;fill:#00";

	public ArrayList<Chart> charts = new ArrayList<>();
	
	public String customDefs = null;
	
	public Page(int numCols) {
		super(numCols);
	}
	
	public Page add(Chart chart) {
		charts.add(chart);
		return this;
	}

	@Override
	public GridLayout autoItemSize() {
		itemWidth = 0;
		itemHeight = 0;
		for(Chart chart : charts) {
			int w = chart.getWidth();
			if(w>itemWidth)
				itemWidth = w;
			int h = chart.getHeight();
			if(h>itemHeight)
				itemHeight = h;
		}
		return this;
	}
	
	@Override
	public int countItems() {
		return charts.size();
	}

	@Override
	public void printItem(PrintStream out, int index, double x, double y) {
		Chart chart = charts.get(index);
		chart.id = chartId(index);
		int cx = (int)x + itemWidth/2 - chart.getWidth()/2; 
		int cy = (int)y + itemHeight/2 - chart.getHeight()/2; 
		out.printf("<g id=\"#%s\" transform=\"translate(%d %d)\">\n", chart.id, cx, cy);
		
		chart.printChart(out);
		
		out.println("</g>");
	}

	protected String chartId(int index) {
		return String.format("chart%d", index);
	}
	
	public void printPage(PrintStream out) {
		if(itemWidth==0 || itemHeight==0)
			autoItemSize();
		out.printf("<svg width=\"%d\" height=\"%d\" xmlns=\"http://www.w3.org/2000/svg\">\n", getWidth(), getHeight());

		int index;

		out.println("<style>");
		out.printf("text { %s }\n", globalTextStyle);
		index = 0;
		for(Chart chart : charts) {
			chart.id = chartId(index);
			chart.printStyles(out);
			index++;
		}
		out.println("</style>");

		out.println("<defs>");
		if(customDefs!=null)
			out.println(customDefs);
		index = 0;
		for(Chart chart : charts) {
			chart.id = chartId(index);
			chart.printDefs(out);
			index++;
		}
		out.println("</defs>");
		
		printItems(out, 0, 0);

		out.println("</svg>");
	}
	
}
