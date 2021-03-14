package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

import com.xrbpowered.jdiagram.data.Data;

public class HSetChart extends Chart {

	public static class Population extends ChartPopulation {
		public final String hdr;
		
		public Population(String hdr, String style) {
			super(new LineRenderer(), style);
			this.hdr = hdr;
		}
		
		public ChartPopulation addLegend(Legend legend) {
			return addLegend(legend, hdr);
		}
	}
	
	public SetAxis axisx;
	public ValueAxis axisy = new ValueAxis();

	public ArrayList<Population> populations = new ArrayList<>();
	
	public HSetChart(Data data, String labelHdr) {
		axisx = new SetAxis(data, labelHdr);
	}

	public HSetChart(Data data) {
		this(data, null);
	}

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
	
	protected double calcx(int v) {
		return axisx.calcx(this, v);
	}

	protected double calcy(double v) {
		return axisy.calcy(this, v);
	}

	protected double totalItemsX() {
		double s = 0;
		for(Population pop : populations)
			s += pop.renderer.itemSizeX();
		return s;
	}
	
	@Override
	protected void printGrid(PrintStream out) {
		if(gridLineStyle==null)
			return;
		out.printf("<g style=\"%s\">\n", gridLineStyle);
		for(Iterator<Double> d=axisy.gridPoints(); d.hasNext();)
			axisy.gridyLine(out, this, d.next());
		out.println("</g>");
	}

	@Override
	protected void printData(PrintStream out) {
		double zeroy = axisy.zeroy(this);
		double sizex = axisx.calcTotalSizeX(chartWidth, totalItemsX());
		double dx = 0;
		for(Population pop : populations) {
			DataRenderer r = pop.renderer;
			r.setZeroes(0, zeroy);
			dx = r.startHSet(out, pop.style, axisx.data.count(), axisx.data, dx, sizex*r.itemSizeX());
			int i = 0;
			for(Data.Row row : axisx.data.rows()) {
				double x = calcx(i);
				double y = calcy(row.getNum(pop.hdr));
				r.addPoint(x, y, row);
				i++;
			}
			r.finish();
		}
	}
	
	@Override
	protected void printAxes(PrintStream out) {
		out.println("<g>");
		if(axisLineStyle!=null)
			axisx.printAxisX(out, this, axisy.zeroy(this));
		// for(Iterator<Double> d=axisx.gridPoints(); d.hasNext();)
		//	axisx.gridxNumber(out, this, d.next());
		out.println("</g>");

		out.println("<g>");
		if(axisLineStyle!=null)
			axisy.printAxisY(out, this, 0);
		for(Iterator<Double> d=axisy.gridPoints(); d.hasNext();)
			axisy.gridyNumber(out, this, d.next());
		out.println("</g>");
	}
}
