package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;

import com.xrbpowered.jdiagram.chart.Legend.LegendItem;

public abstract class ChartPopulation implements LegendItem {

	public String legend = null;
	
	public DataRenderer renderer;
	public String style;
	
	public ChartPopulation(DataRenderer renderer, String style) {
		this.renderer = renderer;
		this.style = style;
	}

	public ChartPopulation setRenderer(DataRenderer renderer) {
		this.renderer = renderer;
		return this;
	}

	public abstract ChartPopulation addLegend(Legend legend);

	public ChartPopulation addLegend(Legend legend, String name) {
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
