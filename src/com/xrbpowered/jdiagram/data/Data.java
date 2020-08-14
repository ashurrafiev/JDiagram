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
import java.util.NoSuchElementException;
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
		
		private Row() {
			cells = new String[headers.length];
		}
		
		/**
		 * Get number of columns in the enclosing {@link Data}. (TODO explain row referencing)
		 * @return number of columns
		 */
		public int colCount() {
			return cells.length;
		}

		/**
		 * Get value of a cell by column index. Use for faster access. 
		 * @param index column index
		 * @return cell value
		 * @throws ArrayIndexOutOfBoundsException if index is outside column range
		 * @see #colCount()
		 * @see #get(String)
		 */
		public String get(int index) {
			return cells[index];
		}
		
		/**
		 * Update value of a cell by column index. Use for faster access.
		 * The value of {@code v} is converted to {@code String} using {@link Object#toString()} unless {@code v} is {@code null}.
		 * @param index column index
		 * @param v new value
		 * @throws ArrayIndexOutOfBoundsException if index is outside column range
		 * @see #colCount()
		 * @see #set(String, Object)
		 */
		public void set(int index, Object v) {
			cells[index] = v==null ? null : v.toString();
		}

		/**
		 * Copy cell values to another array.
		 * @param dst destination array, cannot be {@code null}
		 * @param begin start index in this row
		 * @param end end index in this row (inclusive)
		 * @param dstBegin start index in destination array
		 * @throws NullPointerException if {@code dst} is {@code null}
		 * @throws ArrayIndexOutOfBoundsException if goes outside column range or {@code dst} range
		 * @see #colCount()
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
		 * @throws NullPointerException if {@code dst} is {@code null}
		 * @throws ArrayIndexOutOfBoundsException if goes outside column range or {@code dst} range
		 * @see #colCount()
		 */
		public void copy(Row dst, int begin, int end, int dstBegin) {
			copy(dst.cells, begin, end, dstBegin);
		}

		/**
		 * Get cell value.
		 * @param hdr column name
		 * @return cell value, which can be {@code null}
		 * @throws NoSuchElementException column not found
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
		 * @throws NoSuchElementException column not found
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
		 * @throws NoSuchElementException column not found
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
		 * @throws NoSuchElementException column not found
		 */
		public Boolean getBool(String hdr) {
			String v = get(hdr);
			return v==null ? null : Boolean.parseBoolean(v);
		}
		
		/**
		 * Update cell value. The value of {@code v} is converted to {@code String} using {@link Object#toString()} unless {@code v} is {@code null}. 
		 * @param hdr column header
		 * @param v new value
		 * @throws NoSuchElementException column not found
		 */
		public void set(String hdr, Object v) {
			set(findCol(hdr), v);
		}
		
		/**
		 * Set multiple cell values at once. Column order is inferred from the enclosing {@link Data}.
		 * Copying is range-safe in case the number of values in {@code vs} does not match the number of columns. 
		 * @param vs new values
		 */
		public void update(String... vs) {
			int n = Math.min(cells.length, vs.length);
			for(int i=0; i<n; i++)
				cells[i] = vs[i];
		}
	}
	
	private String[] headers;
	private HashMap<String, Integer> headerMap = new HashMap<>();
	private ArrayList<Row> rows = new ArrayList<>();
	
	/**
	 * Class constructor.
	 * @param headers column headers
	 * @throws InvalidParameterException duplicate header names
	 */
	public Data(String... headers) {
		setHeaders(headers);
	}
	
	private void setHeaders(String[] headers) {
		this.headers = headers;
		headerMap.clear();
		for(int i=0; i<headers.length; i++) {
			if(headerMap.put(headers[i], i)!=null)
				throw new InvalidParameterException("Duplicate header "+headers[i]);
		}
	}
	
	/**
	 * Rename all headers.
	 * @param headers new header names in order, must match the current number of columns
	 * @throws InvalidParameterException number of items in {@code headers} does not match the number of columns
	 * @throws InvalidParameterException duplicate header names
	 */
	public void renameHeaders(String[] headers) {
		if(this.headers.length!=headers.length)
			throw new InvalidParameterException("Header count mismatch");
		setHeaders(headers);
	}
	
	/**
	 * Rename column.
	 * @param oldName old column name
	 * @param newName new column name
	 * @throws NoSuchElementException no column {@code oldName}
	 * @throws InvalidParameterException column {@code newName} already exists
	 */
	public void renameHeader(String oldName, String newName) {
		Integer i = headerMap.remove(oldName);
		if(i==null)
			throw new NoSuchElementException("No column "+oldName);
		else {
			if(headerMap.put(newName, i)!=null)
				throw new RuntimeException("Duplicate header "+newName);
		}
	}
	
	/**
	 * Get number of columns.
	 * @return number of columns
	 */
	public int colCount() {
		return headers.length;
	}
	
	/**
	 * Get column name by column index.
	 * @param index column index
	 * @return column name
	 * @throws ArrayIndexOutOfBoundsException if {@code index<0} or {@code index>=colCount()}
	 * @see #colCount()
	 */
	public String getHeader(int index) {
		return headers[index];
	}
	
	/**
	 * Create new empty data row.
	 * @return new row
	 */
	public Row addRow() {
		Row row = new Row();
		rows.add(row);
		return row;
	}
	
	/**
	 * Create new data row and initialise with values.
	 * Copying is range-safe in case the number of values in {@code vs} does not match the number of columns. 
	 * @param vs initial values in column order
	 * @return new row
	 */
	public Row addRow(String... vs) {
		Row row = new Row();
		row.update(vs);
		rows.add(row);
		return row;
	}
	
	/**
	 * Get data rows as unmodifiable iterable.
	 * @return data rows
	 */
	public Iterable<Row> rows() {
		return Collections.unmodifiableList(rows);
	}
	
	/**
	 * Find column index by column name. Lookup is relatively fast as it uses a hash map.
	 * @param hdr column header
	 * @return column index
	 * @throws NoSuchElementException column not found
	 */
	public int findCol(String hdr) {
		Integer col = headerMap.get(hdr);
		if(col==null)
			throw new NoSuchElementException("No column "+hdr);
		return col;
	}
	
	/**
	 * Find column indices by column names. Throws an exception if any of the names is not found.
	 * @param hdrs column headers, cannot be {@code null}
	 * @return array of column indices in the order of {@code hdrs}
	 * @throws NoSuchElementException column not found
	 * @throws NullPointerException if {@code hdrs} is {@code null}
	 */
	public int[] indexMap(String[] hdrs) {
		int[] indexMap = new int[hdrs.length];
		for(int i=0; i<hdrs.length; i++)
			indexMap[i] = findCol(hdrs[i]);
		return indexMap;
	}
	
	/**
	 * Find column indices by column names without throwing an exception. If a column name does not exist, {@code -1} index is returned.
	 * @param hdrs column headers, cannot be {@code null}
	 * @return array of column indices in the order of {@code hdrs}
	 * @throws NullPointerException if {@code hdrs} is {@code null}
	 */
	public int[] optionalIndexMap(String[] hdrs) {
		int[] indexMap = new int[hdrs.length];
		for(int i=0; i<hdrs.length; i++) {
			Integer col = headerMap.get(hdrs[i]);
			indexMap[i] = col==null ? -1 : col;
		}
		return indexMap;
	}
	
	/**
	 * Get count of data rows.
	 * @return row count
	 */
	public int count() {
		return rows.size();
	}
	
	/**
	 * Modify column headers to add new column.
	 * Values of the new column can be calculated using {@code calc} or initalised to {@code null} if formula is not provided.
	 * @param hdr new column name
	 * @param calc formula to calculate initial values, can be {@code null}
	 * @see #recalc(String, Formula)
	 * @see #addCol(String, String)
	 */
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

	/**
	 * Modify column headers to add new column. Values of the new column are initialised to the given constant.
	 * @param hdr new column name
	 * @param value initial value, can be {@code null}
	 * @see #addCol(String, Formula)
	 */
	public void addCol(String hdr, String value) {
		addCol(hdr, value==null ? null : Formula.val(value));
	}

	/**
	 * Modify column headers to add new column. Values of the new column are initialised to {@code null}.
	 * @param hdr new column name
	 * @see #addCol(String, Formula)
	 * @see #addCol(String, String)
	 */
	public void addCol(String hdr) {
		addCol(hdr, (Formula<?>)null);
	}

	/**
	 * Recalculate contents of a column using formula. If formula is not provided, column values are set to {@code null}.
	 * @param hdr column name
	 * @param calc formula to calculate values, can be {@code null}
	 * @see #addCol(String, Formula)
	 */
	public void recalc(String hdr, Formula<?> calc) {
		int index = findCol(hdr);
		for(Row row: rows) {
			row.set(index, calc==null ? null : calc.calcString(row));
		}
	}
	
	/**
	 * Find and replace values in a column. Uses case-sensitive string comparison for non-null values.
	 * @param hdr column to search values
	 * @param oldValue old value, can be {@code null}
	 * @param newValue replacement value, can be {@code null}
	 */
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
	
	/**
	 * Append data from another table respecting column names.
	 * @param other source table
	 * @throws NullPointerException source table is {@code null}
	 */
	public void append(Data other) {
		int[] indexMap = other.optionalIndexMap(headers);
		for(Row srcRow: other.rows) {
			Row dstRow = addRow();
			for(int i=0; i<indexMap.length; i++) {
				if(indexMap[i]>=0)
					dstRow.set(i, srcRow.get(indexMap[i]));
			}
		}
	}

	/**
	 * Append and mark data from another table respecting column names. Appended rows are marked with a specified value.
	 * <p>Typical use for this method is to keep track of data sources when combining data from multiple tables. For example:
	 * <pre>
	 * Data data = Data.read(new File("data1.csv"));
	 * data.addCol("source", "data1");
	 * data.appendMarked(Data.read(new File("data2.csv")), "source", "data2");
	 * data.appendMarked(Data.read(new File("data3.csv")), "source", "data3");</pre> 
	 * @param other source table
	 * @param markHdr which column in the destination table to mark
	 * @param mark marking value, can be {@code null}
	 * @throws NullPointerException source table is {@code null}
	 * @throws NoSuchElementException column {@code markHdr} does not exist 
	 * @see #append(Data)
	 * @see #addCol(String, String)
	 */
	public void appendMarked(Data other, String markHdr, String mark) {
		int[] indexMap = other.optionalIndexMap(headers);
		int markIndex = findCol(markHdr);
		for(Row srcRow: other.rows) {
			Row dstRow = addRow();
			for(int i=0; i<indexMap.length; i++) {
				if(indexMap[i]>=0)
					dstRow.set(i, srcRow.get(indexMap[i]));
			}
			dstRow.set(markIndex, mark);
		}
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
	
	public Data copyCols(String[] headers) {
		Data data = new Data(headers);
		int[] indexMap = indexMap(headers);
		for(Row srcRow : rows) {
			Row dstRow = data.addRow();
			for(int i=0; i<headers.length; i++)
				dstRow.set(i, srcRow.get(indexMap[i]));
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
