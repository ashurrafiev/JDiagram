package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

import com.xrbpowered.jdiagram.chart.Legend.LegendItem;
import com.xrbpowered.jdiagram.data.Data;

public class ScatterChart extends Chart {

	public static class Population implements LegendItem {
		public final Data data;
		public final String hdrx;
		public final String hdry;
		public String style;
		public String legend = null;
		
		public Population(Data data, String hdrx, String hdry, String style) {
			this.data = data;
			this.hdrx = hdrx;
			this.hdry = hdry;
			this.style = style;
		}

		public Population addLegend(Legend legend) {
			legend.items.add(this);
			this.legend = hdry;
			return this;
		}

		public Population addLegend(Legend legend, String name) {
			legend.items.add(this);
			this.legend = name;
			return this;
		}
		
		@Override
		public String getLegendText() {
			return legend;
		}
		
		@Override
		public String getLegendStyle() {
			return style;
		}
	}

	public ValueAxis axisx = new ValueAxis();
	public ValueAxis axisy = new ValueAxis();

	public ArrayList<Population> populations = new ArrayList<>();
	
	public void addPop(Population pop) {
		populations.add(pop);
	}

	public void addPopLegend(Population pop) {
		pop.addLegend(legend);
		populations.add(pop);
	}

	public void addPopLegend(String name, Population pop) {
		pop.addLegend(legend, name);
		populations.add(pop);
	}

	protected double calcx(double v) {
		return chartWidth * axisx.calc(v);
	}

	protected double calcy(double v) {
		return -chartHeight * axisy.calc(v);
	}

	@Override
	protected void printGrid(PrintStream out) {
		out.println("<g>");
		for(Iterator<Double> d=axisx.gridPoints(); d.hasNext();)
			gridxLine(out, d.next());
		for(Iterator<Double> d=axisy.gridPoints(); d.hasNext();)
			gridyLine(out, d.next(), 5);
		out.println("</g>");
	}

	@Override
	protected void printData(PrintStream out) {
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
	}
	
	@Override
	protected void printAxes(PrintStream out) {
		if(axisLineStyle!=null) {
			printAxisX(out);
			printAxisY(out);
		}
	}
	
	protected void gridxLine(PrintStream out, double v) {
		double x = calcx(v);
		out.printf("<text x=\"%.1f\" y=\"15\" text-anchor=\"middle\">"+axisx.numberFmt+"</text>\n", x, v);
		out.printf("<line class=\"grid\" x1=\"%.1f\" y1=\"0\" x2=\"%.1f\" y2=\"%d\" />\n", x, x, -chartHeight);
	}
	
	protected void gridyLine(PrintStream out, double v, int numberOffs) {
		double y = calcy(v);
		out.printf("<text x=\"-5\" y=\"%.1f\" text-anchor=\"end\" >"+axisy.numberFmt+"</text>\n", y+numberOffs, v);
		out.printf("<line class=\"grid\" x1=\"0\" y1=\"%.1f\" x2=\"%d\" y2=\"%.1f\" />\n", y, chartWidth, y);
	}
	
	protected void printAxisX(PrintStream out) {
		double y = calcy(axisx.anchor, calcy(axisy.zero()));
		out.printf("<line class=\"axis\" x1=\"0\" y1=\"%.1f\" x2=\"%d\" y2=\"%.1f\" />\n", y, chartWidth, y);
		if(axisx.label!=null) {
			double labely = calcy(axisx.labelAnchor, y);
			out.printf("<text x=\"%d\" y=\"%.1f\" text-anchor=\"middle\">%s</text>\n", chartWidth/2, labely, axisx.label);
		}
	}

	protected void printAxisY(PrintStream out) {
		double x =calcx(axisy.anchor, calcx(axisx.zero()));
		out.printf("<line class=\"axis\" x1=\"%.1f\" y1=\"0\" x2=\"%.1f\" y2=\"%d\" />\n", x, x, -chartHeight);
		if(axisy.label!=null) {
			double labelx = calcx(axisy.labelAnchor, x);
			out.printf("<text x=\"%.1f\" y=\"%d\" text-anchor=\"middle\" transform=\"rotate(-90 %.1f,%d)\">%s</text>\n", labelx, -chartHeight/2, labelx, -chartHeight/2, axisy.label);
		}
	}
	

}
