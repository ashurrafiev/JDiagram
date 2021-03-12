package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

import com.xrbpowered.jdiagram.data.Data;

/**
 * Line chart on XY plane where data points are independent (X, Y) pairs.
 *
 */
public class ScatterChart extends Chart {

	public static class Population extends ChartPopulation {
		public final Data data;
		public final String hdrx;
		public final String hdry;
		
		public Population(Data data, String hdrx, String hdry, String style) {
			super(new LineRenderer(), style);
			this.data = data;
			this.hdrx = hdrx;
			this.hdry = hdry;
		}

		@Override
		public ChartPopulation addLegend(Legend legend) {
			return addLegend(legend, hdry);
		}
	}

	public ValueAxis axisx = new ValueAxis();
	public ValueAxis axisy = new ValueAxis();

	public ArrayList<Population> populations = new ArrayList<>();
	
	public <T extends ChartPopulation> void addPop(T pop) {
		populations.add((Population) pop);
	}

	public <T extends ChartPopulation> void addPopLegend(T pop) {
		pop.addLegend(legend);
		populations.add((Population) pop);
	}

	public <T extends ChartPopulation> void addPopLegend(String name, T pop) {
		pop.addLegend(legend, name);
		populations.add((Population) pop);
	}

	protected double calcx(double v) {
		return axisx.calcx(this, v);
	}

	protected double calcy(double v) {
		return axisy.calcy(this, v);
	}

	@Override
	protected void printGrid(PrintStream out) {
		if(gridLineStyle==null)
			return;
		out.printf("<g style=\"%s\">\n", gridLineStyle);
		for(Iterator<Double> d=axisx.gridPoints(); d.hasNext();)
			axisx.gridxLine(out, this, d.next());
		for(Iterator<Double> d=axisy.gridPoints(); d.hasNext();)
			axisy.gridyLine(out, this, d.next());
		out.println("</g>");
	}

	@Override
	protected void printData(PrintStream out) {
		double zerox = axisx.zerox(this);
		double zeroy = axisy.zeroy(this);
		for(Population pop : populations) {
			DataRenderer r = pop.renderer;
			r.setZeroes(zerox, zeroy);
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
			axisx.printAxisX(out, this, axisy.zeroy(this));
		for(Iterator<Double> d=axisx.gridPoints(); d.hasNext();)
			axisx.gridxNumber(out, this, d.next());
		out.println("</g>");

		out.println("<g>");
		if(axisLineStyle!=null)
			axisy.printAxisY(out, this, axisx.zerox(this));
		for(Iterator<Double> d=axisy.gridPoints(); d.hasNext();)
			axisy.gridyNumber(out, this, d.next());
		out.println("</g>");
	}
	
}
