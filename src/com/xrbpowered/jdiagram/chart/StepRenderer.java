package com.xrbpowered.jdiagram.chart;

import com.xrbpowered.jdiagram.data.Data.Row;

public class StepRenderer extends LineRenderer {

	@Override
	public void addPoint(double x, double y, Row row) {
		path.append(String.format(first ? "M 0,%.1f" : " V %.1f", y));
		path.append(String.format(" H %.1f", x));
		first = false;
	}
	
}
