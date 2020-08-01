package com.xrbpowered.jdiagram.data;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

import com.xrbpowered.jdiagram.data.Formula.Var;

public class Data {

	public class Row {
		private String[] cells;
		
		public Row() {
			cells = new String[headers.length];
		}
		
		public Data data() {
			return Data.this;
		}
		
		public String get(String hdr) {
			return cells[findCol(hdr)];
		}
		
		public Integer getInt(String hdr) {
			String v = get(hdr);
			return v==null ? null : Integer.parseInt(v);
		}
		
		public Double getNum(String hdr) {
			String v = get(hdr);
			return v==null ? null : Double.parseDouble(v);
		}
		
		public Boolean getBool(String hdr) {
			String v = get(hdr);
			return v==null ? null : Boolean.parseBoolean(v);
		}
		
		public void set(String hdr, Object v) {
			cells[findCol(hdr)] = v==null ? null : v.toString();
		}
		
		public void update(String... vs) {
			int n = Math.min(cells.length, vs.length);
			for(int i=0; i<n; i++)
				cells[i] = vs[i];
		}
	}
	
	protected String[] headers;
	private HashMap<String, Integer> headerMap = new HashMap<>();
	
	public ArrayList<Row> rows = new ArrayList<>();
	
	public Data(String... headers) {
		setHeaders(headers);
	}

	public String[] headers() {
		return this.headers;
	}
	
	protected void setHeaders(String[] headers) {
		this.headers = headers;
		headerMap.clear();
		for(int i=0; i<headers.length; i++) {
			headerMap.put(headers[i], i);
		}
	}
	
	public void renameHeaders(String[] headers) {
		if(this.headers.length!=headers.length)
			throw new RuntimeException("Header count mismatch");
		setHeaders(headers);
	}
	
	public Row addRow() {
		Row row = new Row();
		rows.add(row);
		return row;
	}
	
	public Row addRow(String... vs) {
		Row row = new Row();
		row.update(vs);
		rows.add(row);
		return row;
	}
	
	protected int findCol(String hdr) {
		Integer col = headerMap.get(hdr);
		if(col==null)
			throw new RuntimeException("No column "+hdr);
		return col;
	}
	
	public int count() {
		return rows.size();
	}
	
	public void addCol(String hdr, Formula<?> calc) {
		int col = this.headers.length;
		String[] headers = new String[col+1];
		for(int i=0; i<this.headers.length; i++)
			headers[i] = this.headers[i];
		headers[col] = hdr;
		headerMap.put(hdr, col);
		this.headers = headers;
		
		for(Row row: rows) {
			String[] cells = new String[col+1];
			for(int i=0; i<col; i++)
				cells[i] = row.cells[i];
			cells[col] = calc==null ? null : calc.calcString(row);
			row.cells = cells;
		}
	}
	
	public <T> void fold(Var<T> acc, Formula<T> calc) {
		for(Row row: rows)
			acc.value = calc.calc(row);
	}
	
	public void sort(final boolean asc, final Formula<?>... calc) {
		rows.sort(new Comparator<Row>() {
			@SuppressWarnings("unchecked")
			@Override
			public int compare(Row row1, Row row2) {
				int res = 0;
				for(int i=0; res==0 && i<calc.length; i++) {
					Object v1 = calc[i].calc(row1);
					Object v2 = calc[i].calc(row2);
					if(v1==null || v2==null)
						return Boolean.compare(v1==null, v2==null);
					if(v1 instanceof Comparable<?>)
						res = ((Comparable<Object>) v1).compareTo(v2);
					else
						throw new RuntimeException("Not comparable");
					if(!asc)
						res = -res;
				}
				return res;
			}
		});
	}
	
	public void sort(final Formula<?>... calc) {
		sort(true, calc);
	}
	
	public void printHeaders(PrintStream out) {
		for(String hdr : headers) {
			out.print(hdr);
			out.print("\t");
		}
		out.println();
	}
	
	public void print(PrintStream out, boolean withHeaders) {
		if(withHeaders)
			printHeaders(out);
		for(Row row : rows) {
			for(int i=0; i<headers.length; i++) {
				out.print(row.cells[i]);
				out.print("\t");
			}
			out.println();
		}
	}
	
	public void print(PrintStream out) {
		print(out, true);
	}
	
	public void print() {
		print(System.out);
	}
	
	public Data filter(Filter filter) {
		Data data = new Data(headers);
		for(Row row : rows) {
			if(filter.accept(row))
				data.rows.add(row);
		}
		return data;
	}

	public static Data read(File file, String[] headers, String sepRegex) {
		try {
			Scanner in = new Scanner(file);
			Data data = new Data(headers==null ? in.nextLine().split(sepRegex) : headers);
			while(in.hasNext()) {
				String[] vs = in.nextLine().split(sepRegex);
				if(vs.length>0)
					data.addRow(vs);
			}
			in.close();
			return data;
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Data read(File file, String[] headers) {
		return read(file, headers, "(\\,\\s+)|\\s");
	}

	public static Data read(File file) {
		return read(file, null);
	}
	
	public static void write(Data data, File file) {
		try {
			PrintStream out = new PrintStream(file);
			data.print(out, true);
			out.close();
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Data range(String hdr, int min, int max, int step) {
		Data data = new Data(hdr);
		for(int x=min; x<=max; x+=step)
			data.addRow(Integer.toString(x));
		return data;
	}
	
	public static Data range(String hdr, int min, int max) {
		return range(hdr, min, max, 1);
	}

	public static Data range(String hdr, String fmt, double min, double max, double step) {
		Data data = new Data(hdr);
		for(double x=min; x<=max; x+=step)
			data.addRow(String.format(fmt, x));
		return data;
	}

}
