package com.xrbpowered.jdiagram.chart;

import com.xrbpowered.jdiagram.data.Data;

public class SetAxis extends Axis {

	public Data data;
	public String labelHdr = null;
	
	public double itemSizeRatio = 0.8;

	public SetAxis(Data data, String labelHdr) {
		this.data = data;
		this.labelHdr = labelHdr;
	}
	
	public SetAxis(Data data) {
		this(data, null);
	}
	
	@Override
	public SetAxis setAnchor(Anchor anchor) {
		return (SetAxis) super.setAnchor(anchor);
	}
	
	@Override
	public SetAxis setLabel(String label, Anchor labelAnchor) {
		return (SetAxis) super.setLabel(label, labelAnchor);
	}

	@Override
	public SetAxis setLabel(String label) {
		return (SetAxis) super.setLabel(label);
	}
	
	@Override
	public double calc(double v) {
		return v / data.count();
	}
	
	public double calcTotalSizeX(double chartWidth, double totalItemsX) {
		return chartWidth / data.count() / (totalItemsX>0 ?  totalItemsX/itemSizeRatio : 1);
	}

}
