package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;
import java.util.ArrayList;

public class Legend extends GridLayout {

	public static interface LegendItem {
		public String getLegendText();
		public String getLegendStyle();
	}

	public Anchor anchorX, anchorY;
	public boolean inside = false;
	
	public ArrayList<LegendItem> items = new ArrayList<>();
	
	public Legend(int numCols) {
		super(numCols);
		posBottom(-35);
	}

	public Legend posInside(Anchor anchorX, Anchor anchorY) {
		this.anchorX = anchorX;
		this.anchorY = anchorY;
		this.inside = true;
		return this;
	}

	public Legend posLeft(double offs) {
		anchorX = Anchor.left.offset(offs);
		anchorY = Anchor.middle;
		inside = false;
		return this;
	}

	public Legend posRight(double offs) {
		anchorX = Anchor.right.offset(offs);
		anchorY = Anchor.middle;
		inside = false;
		return this;
	}

	public Legend posTop(double offs) {
		anchorX = Anchor.middle;
		anchorY = Anchor.top.offset(offs);
		inside = false;
		return this;
	}

	public Legend posBottom(double offs) {
		anchorX = Anchor.middle;
		anchorY = Anchor.bottom.offset(offs);
		inside = false;
		return this;
	}
	
	@Override
	public int countItems() {
		return items.size();
	}
	
	@Override
	public void printItem(PrintStream out, int index, double x, double y) {
		LegendItem item = items.get(index);
		out.printf("<line x1=\"%.1f\" y1=\"%.1f\" x2=\"%.1f\" y2=\"%.1f\" style=\"%s\" />\n", x, y+itemHeight/2, x+30, y+itemHeight/2, item.getLegendStyle());
		out.printf("<text x=\"%.1f\" y=\"%.1f\">%s</text>\n", x+35, y+itemHeight/2+3, item.getLegendText());
	}
	
}
