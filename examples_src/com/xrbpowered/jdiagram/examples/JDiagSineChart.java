package com.xrbpowered.jdiagram.examples;

import com.xrbpowered.jdiagram.chart.ScatterChart;
import com.xrbpowered.jdiagram.chart.ScatterChart.Population;
import com.xrbpowered.jdiagram.data.Data;
import com.xrbpowered.jdiagram.data.Formula;
import com.xrbpowered.jdiagram.data.Data.Row;

public class JDiagSineChart {

	public static void main(String[] args) {
		Data data = Data.range("x", 0, 360);
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
		
		ScatterChart chart = new ScatterChart().setXRange(0, 360).setYRange(-1, 1);
		chart.addPopulation(new Population("cos", data, "x", "cos", "fill:none;stroke:#777;stroke-width:1;stroke-dasharray:2 2"));
		chart.addPopulation(new Population("sin", data, "x", "sin", "fill:none;stroke:#d00;stroke-width:2"));
		
		chart.print(System.out);
		
	}

}
