package com.xrbpowered.jdiagram.chart;

public abstract class PatternDefs {

	public static String cross(String id, int size, String fill, String stroke, float strokeWidth) {
		return String.format("<pattern patternUnits=\"userSpaceOnUse\" width=\"%d\" height=\"%d\" id=\"%s\">\n" +
				"<rect style=\"fill:%s;stroke:none\" width=\"%d\"  height=\"%d\" x=\"0\" y=\"0\" />\n" +
				"<line style=\"stroke:%s;stroke-width:%.1f\" x1=\"0\" y1=\"0\" x2=\"%d\" y2=\"%d\" />\n" +
				"<line style=\"stroke:%s;stroke-width:%.1f\" x1=\"0\" y1=\"%d\" x2=\"%d\" y2=\"0\" />\n" +
				"</pattern>", size, size, id, fill, size, size,
				stroke, strokeWidth, size, size,
				stroke, strokeWidth, size, size);
	}

	public static String diag(String id, int size, String fill, String stroke, float strokeWidth) {
		return String.format("<pattern patternUnits=\"userSpaceOnUse\" width=\"%d\" height=\"%d\" id=\"%s\">\n" +
				"<rect style=\"fill:%s;stroke:none\" width=\"%d\"  height=\"%d\" x=\"0\" y=\"0\" />\n" +
				"<line style=\"stroke:%s;stroke-width:%.1f\" x1=\"0\" y1=\"%.1f\" x2=\"%.1f\" y2=\"0\" />\n" +
				"<line style=\"stroke:%s;stroke-width:%.1f\" x1=\"%.1f\" y1=\"%d\" x2=\"%d\" y2=\"%.1f\" />\n" +
				"</pattern>", size, size, id, fill, size, size,
				stroke, strokeWidth, size/2f, size/2f,
				stroke, strokeWidth, size/2f, size, size, size/2f);
	}

	public static String rdiag(String id, int size, String fill, String stroke, float strokeWidth) {
		return String.format("<pattern patternUnits=\"userSpaceOnUse\" width=\"%d\" height=\"%d\" id=\"%s\">\n" +
				"<rect style=\"fill:%s;stroke:none\" width=\"%d\"  height=\"%d\" x=\"0\" y=\"0\" />\n" +
				"<line style=\"stroke:%s;stroke-width:%.1f\" x1=\"%.1f\" y1=\"0\" x2=\"%d\" y2=\"%.1f\" />\n" +
				"<line style=\"stroke:%s;stroke-width:%.1f\" x1=\"0\" y1=\"%.1f\" x2=\"%.1f\" y2=\"%d\" />\n" +
				"</pattern>", size, size, id, fill, size, size,
				stroke, strokeWidth, size/2f, size, size/2f,
				stroke, strokeWidth, size/2f, size/2f, size);
	}

	public static String dots(String id, int size, String fill, String stroke, float strokeWidth) {
		return String.format("<pattern patternUnits=\"userSpaceOnUse\" width=\"%d\" height=\"%d\" id=\"%s\">\n" +
				"<rect style=\"fill:%s;stroke:none\" width=\"%d\"  height=\"%d\" x=\"0\" y=\"0\" />\n" +
				"<circle style=\"fill:%s;stroke:none\" r=\"%.1f\" cx=\"%.1f\" cy=\"%.1f\" />\n" +
				"<circle style=\"fill:%s;stroke:none\" r=\"%.1f\" cx=\"%.1f\" cy=\"%.1f\" />\n" +
				"</pattern>", size, size, id, fill, size, size,
				stroke, strokeWidth/2f, size*0.25f, size*0.25f,
				stroke, strokeWidth/2f, size*0.75f, size*0.75f);
	}
}
