package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

import com.xrbpowered.jdiagram.chart.Legend.LegendItem;
import com.xrbpowered.jdiagram.data.Data;

/**
 * Line chart on XY plane where data points are independent (X, Y) pairs.
 *
 */
public class ScatterChart extends Chart {

	public static class Population implements LegendItem {
		public final Data data;
		public final String hdrx;
		public final String hdry;
		
		public String legend = null;
		
		public DataRenderer renderer = new LineRenderer();
		public String style;
		
		public Population(Data data, String hdrx, String hdry, String style) {
			this.data = data;
			this.hdrx = hdrx;
			this.hdry = hdry;
			this.style = style;
		}
		
		public Population setRenderer(DataRenderer renderer) {
			this.renderer = renderer;
			return this;
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
		public void printLegendSwatch(PrintStream out, double x, double y, int w, int h) {
			renderer.printLegendSwatch(out, x, y, w, h, style);
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
		if(gridLineStyle==null)
			return;
		out.printf("<g style=\"%s\">\n", gridLineStyle);
		for(Iterator<Double> d=axisx.gridPoints(); d.hasNext();)
			gridxLine(out, d.next());
		for(Iterator<Double> d=axisy.gridPoints(); d.hasNext();)
			gridyLine(out, d.next());
		out.println("</g>");
	}

	@Override
	protected void printData(PrintStream out) {
		for(Population pop : populations) {
			DataRenderer r = pop.renderer;
			r.start(out, pop.style, pop.data.count(), pop.data);
			for(Data.Row row : pop.data.rows()) {
				double x = calcx(row.getNum(pop.hdrx));
				double y = calcy(row.getNum(pop.hdry));
				r.addPoint(x, y, row);
			}
			r.finish();
		}
	}
	
	@Override
	protected void printAxes(PrintStream out) {
		out.println("<g>");
		if(axisLineStyle!=null)
			printAxisX(out);
		for(Iterator<Double> d=axisx.gridPoints(); d.hasNext();)
			gridxNumber(out, d.next());
		out.println("</g>");

		out.println("<g>");
		if(axisLineStyle!=null)
			printAxisY(out);
		for(Iterator<Double> d=axisy.gridPoints(); d.hasNext();)
			gridyNumber(out, d.next());
		out.println("</g>");
	}
	
	protected void gridxLine(PrintStream out, double v) {
		double x = calcx(v);
		out.printf("<line x1=\"%.1f\" y1=\"0\" x2=\"%.1f\" y2=\"%d\" />\n", x, x, -chartHeight);
	}
	
	protected void gridyLine(PrintStream out, double v) {
		double y = calcy(v);
		out.printf("<line x1=\"0\" y1=\"%.1f\" x2=\"%d\" y2=\"%.1f\" />\n", y, chartWidth, y);
	}

	protected void gridxNumber(PrintStream out, double v) {
		// TODO grid number positioning
		String s = axisx.numberFmt.format(v);
		if(s!=null)
			out.printf("<text x=\"%.1f\" y=\"15\" text-anchor=\"middle\">%s</text>\n", calcx(v), s);
	}
	
	protected void gridyNumber(PrintStream out, double v) {
		// TODO grid number positioning
		String s = axisy.numberFmt.format(v);
		if(s!=null)
			out.printf("<text x=\"-5\" y=\"%.1f\" text-anchor=\"end\" >%s</text>\n", calcy(v)+5, s); // TODO proper numberOffs configuration
	}

	protected void printAxisX(PrintStream out) {
		double y = calcy(axisx.anchor, calcy(axisy.zero()));
		if(axisLineStyle!=null)
			out.printf("<line x1=\"0\" y1=\"%.1f\" x2=\"%d\" y2=\"%.1f\" style=\"%s\" />\n", y, chartWidth, y, axisLineStyle);
		if(axisx.label!=null) {
			double labely = calcy(axisx.labelAnchor, y);
			out.printf("<text x=\"%d\" y=\"%.1f\" text-anchor=\"middle\">%s</text>\n", chartWidth/2, labely, axisx.label);
		}
	}

	protected void printAxisY(PrintStream out) {
		double x =calcx(axisy.anchor, calcx(axisx.zero()));
		if(axisLineStyle!=null)
			out.printf("<line x1=\"%.1f\" y1=\"0\" x2=\"%.1f\" y2=\"%d\" style=\"%s\" />\n", x, x, -chartHeight, axisLineStyle);
		if(axisy.label!=null) {
			double labelx = calcx(axisy.labelAnchor, x);
			out.printf("<text x=\"%.1f\" y=\"%d\" text-anchor=\"middle\" transform=\"rotate(-90 %.1f,%d)\">%s</text>\n", labelx, -chartHeight/2, labelx, -chartHeight/2, axisy.label);
		}
	}
	

}
