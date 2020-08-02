package com.xrbpowered.jdiagram.chart;

import java.io.PrintStream;

public abstract class GridLayout {

	public int numCols;
	public int itemWidth, itemHeight;
	public int gapx = 0;
	public int gapy = 0;
	
	public String frameStyle = null;
	public int margin = 0;

	public GridLayout(int numCols) {
		this.numCols = numCols;
	}

	public GridLayout setCols(int numCols) {
		this.numCols = numCols;
		return this;
	}
	
	public GridLayout setItemSize(int w, int h) {
		this.itemWidth = w;
		this.itemHeight = h;
		return this;
	}
	
	public GridLayout autoItemSize() {
		throw new UnsupportedOperationException();
	}
	
	public GridLayout setGap(int gap) {
		return this.setGap(gap, gap);
	}
	
	public GridLayout setGap(int gapx, int gapy) {
		this.gapx = gapx;
		this.gapy = gapy;
		return this;
	}
	
	public GridLayout setFrame(int margin, String style) {
		this.margin = margin;
		this.frameStyle = style;
		return this;
	}
	
	private int countCols(int count) {
		return numCols==0 ? count : numCols;
	}
	
	private int countRows(int count) {
		return numCols==0 ? 1 : (count+numCols-1)/numCols;
	}
	
	public int countCols() {
		return countCols(countItems());
	}

	public int countRows() {
		return countRows(countItems());
	}

	public int getWidth() {
		return countCols()*(itemWidth+gapx)-gapx + margin*2;
	}
	
	public int getHeight() {
		return countRows()*(itemHeight+gapy)-gapy + margin*2;
	}
	
	public abstract int countItems();
	public abstract void printItem(PrintStream out, int index, double x, double y);
	
	public void printItems(PrintStream out, double left, double top) {
		out.println("<g>");
		if(frameStyle!=null)
			out.printf("<rect x=\"%.1f\" y=\"%.1f\" width=\"%d\" height=\"%d\" style=\"%s\" />\n", left, top, getWidth(), getHeight(), frameStyle);
		
		int count = countItems();
		int cols = countCols(count);
		int rows = countRows(count);
		int index = 0;
		for(int r=0; r<rows; r++)
			for(int c=0; c<cols; c++) {
				if(index<count)
					printItem(out, index, left+c*(itemWidth+gapx)+margin, top+r*(itemHeight+gapy)+margin);
				index++;
			}
		out.println("</g>");
	}
	
}
