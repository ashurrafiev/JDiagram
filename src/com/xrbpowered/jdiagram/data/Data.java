package com.xrbpowered.jdiagram.data;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;

import com.xrbpowered.jdiagram.data.Formula.Var;

/**
 * Data table.
 * <p>
 * Values are stored as nullable strings in rows and columns. Columns can be accessed by name (header).
 */
public class Data {

	/**
	 * Contains one row of values.
	 * <p>
	 * Values are stored as nullable strings. The number of cells matches the number of headers in the enclosing {@link Data}.
	 * (TODO explain row referencing)
	 */
	public class Row {
		private final String[] cells;
		
		/**
		 * Create row object without adding it to {@link Data#rows}. The number of cells will match enclosing {@link Data#headers}.
		 */
		protected Row() {
			cells = new String[headers.length];
		}
		
		/**
		 * Get the list of column headers. Returned array is the reference to the original enclosing {@link Data#headers}.
		 * (TODO explain row referencing)
		 * @return column headers
		 */
		public String[] headers() {
			return headers;
		}

		/**
		 * Get value of a cell by column index. Use for faster access. 
		 * @param index column index
		 * @return cell value
		 */
		protected String get(int index) {
			return cells[index];
		}
		
		/**
		 * Update value of a cell by column index. Use for faster access. 
		 * @param index column index
		 * @param v new value
		 */
		protected void set(int index, Object v) {
			cells[index] = v==null ? null : v.toString();
		}

		/**
		 * Copy cell values to another array.
		 * @param dst destination array, cannot be {@code null}
		 * @param begin start index in this row
		 * @param end end index in this row (inclusive)
		 * @param dstBegin start index in destination array
		 */
		public void copy(String[] dst, int begin, int end, int dstBegin) {
			for(int i=begin; i<=end; i++)
				dst[dstBegin-begin+i] = cells[i];
		}

		/**
		 * Copy all cell values to another array.
		 * @param dst destination array. If {@code null}, a new array is created and returned.
		 * @return destination array
		 */
		public String[] copy(String[] dst) {
			if(dst==null)
				dst = new String[cells.length]; 
			copy(dst, 0, cells.length, 0);
			return dst;
		}

		/**
		 * Copy cell values to another row.
		 * @param dst destination row, cannot be {@code null}
		 * @param begin start index in this row
		 * @param end end index in this row (inclusive)
		 * @param dstBegin start index in destination row
		 */
		protected void copy(Row dst, int begin, int end, int dstBegin) {
			copy(dst.cells, begin, end, dstBegin);
		}

		/**
		 * Get cell value.
		 * @param hdr column name
		 * @return cell value, which can be {@code null}
		 */
		public String get(String hdr) {
			return cells[findCol(hdr)];
		}
		
		/**
		 * Get cell value as integer.
		 * Conversion is done by {@link Integer#parseInt(String)} and can throw a {@code NumberFormatException};
		 * {@code null} is returned only if the cell value is null. 
		 * @param hdr column name
		 * @return cell value, which can be {@code null}
		 * @throws NumberFormatException if the value is not null and does not contain a parsable integer.
		 */
		public Integer getInt(String hdr) {
			String v = get(hdr);
			return v==null ? null : Integer.parseInt(v);
		}
		
		/**
		 * Get cell value as a double-precision real number.
		 * Conversion is done by {@link Double#parseDouble(String)} and can throw a {@code NumberFormatException};
		 * {@code null} is returned only if the cell value is null. 
		 * @param hdr column name
		 * @return cell value, which can be {@code null}
		 * @throws NumberFormatException if the value is not null and does not contain a parsable {@code double}.
		 */
		public Double getNum(String hdr) {
			String v = get(hdr);
			return v==null ? null : Double.parseDouble(v);
		}
		
		/**
		 * Get cell value as boolean. Conversion is done by {@link Boolean#parseBoolean(String)};
		 * {@code null} is returned only if the cell value is null. 
		 * @param hdr column name
		 * @return cell value, which can be {@code null}
		 */
		public Boolean getBool(String hdr) {
			String v = get(hdr);
			return v==null ? null : Boolean.parseBoolean(v);
		}
		
		/**
		 * Update cell value. The value of {@code v} is converted to {@link String} using {@link Object#toString()} unless {@code v} is null. 
		 * @param hdr column header
		 * @param v new value
		 */
		public void set(String hdr, Object v) {
			set(findCol(hdr), v);
		}
		
		/**
		 * Set multiple cell values at once. Column order is inferred from {@link #headers()}.
		 * Copying is range-safe in case the number of values in {@code vs} does not match the number of cells. 
		 * @param vs new values
		 */
		public void update(String... vs) {
			int n = Math.min(cells.length, vs.length);
			for(int i=0; i<n; i++)
				cells[i] = vs[i];
		}
	}
	
	/**
	 * List of column headers. All subclasses must ensure that, if any change to headers happens when rows are not empty,
	 * data values must be updated respectively. Typically, this is done by clearing and recreating all rows.
	 * See the implementation of {@link Data#addCol(String, Formula) addCol} for an example. 
	 */
	protected String[] headers;
	private HashMap<String, Integer> headerMap = new HashMap<>();
	
	protected ArrayList<Row> rows = new ArrayList<>();
	
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
	
	public Iterable<Row> rows() {
		return Collections.unmodifiableList(rows);
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
		
		ArrayList<Row> oldRows = rows;
		rows = new ArrayList<>();
		
		for(Row oldRow: oldRows) {
			Row row = addRow();
			oldRow.copy(row, 0, col-1, 0);
			row.set(col, calc==null ? null : calc.calcString(row));
		}
	}
	
	public void recalc(String hdr, Formula<?> calc) {
		int index = findCol(hdr);
		for(Row row: rows) {
			row.set(index, calc==null ? null : calc.calcString(row));
		}
	}
	
	public void replaceAll(String hdr, String oldValue, String newValue) {
		int index = findCol(hdr);
		for(Row row: rows) {
			String v = row.get(index);
			if(oldValue==null && v==null || oldValue!=null && oldValue.equals(v))
				row.set(index, newValue);
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

	public void sort(boolean asc, String... hdr) {
		Formula<?>[] calc = new Formula<?>[hdr.length];
		for(int i=0; i<hdr.length; i++)
			calc[i] = Formula.get(hdr[i]);
		sort(asc, calc);
	}

	public void sort(final Formula<?>... calc) {
		sort(true, calc);
	}

	public void sort(String... hdr) {
		sort(true, hdr);
	}

	public void printHeaders(PrintStream out) {
		for(String hdr : headers) {
			out.print(hdr);
			out.print("\t");
		}
		out.println();
	}

	public void printHeaders(PrintStream out, String[] cols) {
		for(String hdr : cols) {
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
				out.print(row.get(i));
				out.print("\t");
			}
			out.println();
		}
	}

	public void printCols(PrintStream out, boolean withHeaders, String... cols) {
		if(withHeaders)
			printHeaders(out, cols);
		for(Row row : rows) {
			for(int i=0; i<cols.length; i++) {
				out.print(row.get(cols[i]));
				out.print("\t");
			}
			out.println();
		}
	}
	
	public void print() {
		print(System.out, true);
	}

	public void printCols(String... cols) {
		printCols(System.out, true, cols);
	}

	public Data filter(Filter filter) {
		Data data = new Data(headers);
		for(Row row : rows) {
			if(filter.accept(row))
				data.rows.add(row);
		}
		return data;
	}
	
	public Data limit(int count) {
		Data data = new Data(headers);
		for(int i=0; i<count; i++) {
			data.rows.add(rows.get(i));
		}
		return data;
	}
	
	public Data copy() {
		Data data = new Data(Arrays.copyOf(headers, headers.length));
		for(Row row : rows) {
			data.addRow(Arrays.copyOf(row.cells, headers.length));
		}
		return data;
	}
	
	public Data groupBy(String hdr, String[] headers, Fold<?>... folds) {
		if(headers.length!=folds.length)
			throw new InvalidParameterException("Number of headers and number of folds don't match");
		String[] h = new String[headers.length+1];
		h[0] = hdr;
		for(int i=0; i<headers.length; i++)
			h[i+1] = headers[i];

		Data data = new Data(h);
		LinkedHashMap<String, Data> uniques = new LinkedHashMap<>();
		
		int index = findCol(hdr);
		for(Row row : rows) {
			String v = row.get(index);
			Data u = uniques.get(v);
			if(u==null) {
				u = new Data(this.headers);
				uniques.put(v, u);
			}
			u.rows.add(row);
		}
		
		for(String key : uniques.keySet()) {
			Data u = uniques.get(key);
			Row row = data.addRow();
			row.set(0, key);
			for(int i=0; i<headers.length; i++) {
				row.set(i+1, folds[i].fold(u));
			}
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
		return read(file, headers, "(\\,\\s*)|\\t");
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
