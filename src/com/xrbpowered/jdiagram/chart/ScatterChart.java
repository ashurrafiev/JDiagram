package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

import com.xrbpowered.jdiagram.data.Data;

public class ScatterChart extends Chart {

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

	public ValueAxis axisx = new ValueAxis();
	public ValueAxis axisy = new ValueAxis();

	public ArrayList<Population> populations = new ArrayList<>();
	
	public void addPopulation(Population pop) {
		populations.add(pop);
	}
	
	protected double calcx(double v) {
		return chartWidth * axisx.calc(v);
	}

	protected double calcy(double v) {
		return -chartHeight * axisy.calc(v);
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

	@Override
	public void print(PrintStream out) {
		out.printf("<svg width=\"%d\" height=\"%d\" xmlns=\"http://www.w3.org/2000/svg\">\n", getWidth(), getHeight());
		
		out.println("<style>");
		out.println("text { "+textStyle+" }");
		if(axisLineStyle!=null)
			out.println(".axis { "+axisLineStyle+" }");
		if(gridLineStyle!=null)
			out.println(".grid { "+gridLineStyle+" }");
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
		if(chartAreaStyle!=null)
			out.printf("<rect x=\"0\" y=\"%d\" width=\"%d\" height=\"%d\" style=\"%s\" />\n", -chartHeight, chartWidth, chartHeight, chartAreaStyle);
		out.println("<g>");
		for(Iterator<Double> d=axisx.gridPoints(); d.hasNext();)
			gridxLine(out, d.next());
		for(Iterator<Double> d=axisy.gridPoints(); d.hasNext();)
			gridyLine(out, d.next(), 5);
		out.println("</g>");

		// data
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
		if(axisLineStyle!=null) {
			printAxisX(out);
			printAxisY(out);
		}
		
		if(title!=null)
			out.printf("<text x=\"%.1f\" y=\"%.1f\" text-anchor=\"%s\" style=\"%s\">%s</text>\n", calcx(titleAnchorX), calcy(titleAnchorY), titleAnchorX.innerAlign(), titleStyle, title);
		
		// FIXME legend
		int lw = 60;
		int x = chartWidth/2 - (populations.size()*lw)/2;
		for(Population pop : populations) {
			out.printf("<line x1=\"%d\" y1=\"45\" x2=\"%d\" y2=\"45\" style=\"%s\" />\n", x, x+30, pop.style);
			out.printf("<text x=\"%d\" y=\"48\">%s</text>\n", x+35, pop.legend);
			x += lw;
		}
		
		out.println("</g>");
		out.println("</svg>");
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
		double y = calcy(axisx.anchor, calcy(axisy.zero())); // axisx.anchor.calc(0, calcy(axisy.zero()), -chartHeight);
		out.printf("<line class=\"axis\" x1=\"0\" y1=\"%.1f\" x2=\"%d\" y2=\"%.1f\" />\n", y, chartWidth, y);
		if(axisx.label!=null) {
			double labely = calcy(axisx.labelAnchor, y); // axisx.labelAnchor.calc(0, y, -chartHeight);
			out.printf("<text x=\"%d\" y=\"%.1f\" text-anchor=\"middle\">%s</text>\n", chartWidth/2, labely, axisx.label);
		}
	}

	protected void printAxisY(PrintStream out) {
		double x =calcx(axisy.anchor, calcx(axisx.zero())); // axisy.anchor.calc(0, calcx(axisx.zero()), chartWidth);
		out.printf("<line class=\"axis\" x1=\"%.1f\" y1=\"0\" x2=\"%.1f\" y2=\"%d\" />\n", x, x, -chartHeight);
		if(axisy.label!=null) {
			double labelx = calcx(axisy.labelAnchor, x); // axisy.labelAnchor.calc(0, x, chartWidth);
			out.printf("<text x=\"%.1f\" y=\"%d\" text-anchor=\"middle\" transform=\"rotate(-90 %.1f,%d)\">%s</text>\n", labelx, -chartHeight/2, labelx, -chartHeight/2, axisy.label);
		}
	}
	

}
