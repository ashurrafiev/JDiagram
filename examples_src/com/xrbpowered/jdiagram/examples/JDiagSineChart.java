package com.xrbpowered.jdiagram.examples;

import com.xrbpowered.jdiagram.chart.ScatterChart;
import com.xrbpowered.jdiagram.chart.ScatterChart.Population;
import com.xrbpowered.jdiagram.data.Data;
import com.xrbpowered.jdiagram.data.Data.Row;
import com.xrbpowered.jdiagram.data.Formula;

public class JDiagSineChart {

	public static void main(String[] args) {
		Data data = Data.range("x", -180, 180);
		data.addCol("sin", new Formula<Double>() {
			@Override
			public Double calc(Row row) {
				return Math.sin(Math.toRadians(row.getNum("x")));
			}
		});
		data.addCol("cos", new Formula<Double>() {
			@Override
			public Double calc(Row row) {
				return Math.cos(Math.toRadians(row.getNum("x")));
			}
		});
		
		ScatterChart chart = new ScatterChart();
		chart.axisx.setRange(-180, 180, 45).setNumberFmt("%.0f&#xb0;");
		chart.axisy.setRange(-1, 1, 0.5).setNumberFmt("%.1f");
		chart.addPopLegend(new Population(data, "x", "cos", "fill:none;stroke:#777;stroke-width:1;stroke-dasharray:2 2"));
		chart.addPopLegend(new Population(data, "x", "sin", "fill:none;stroke:#d00;stroke-width:2"));
		
		chart.printPage(System.out);
		
	}

}
