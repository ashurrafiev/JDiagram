package com.xrbpowered.jdiagram.data;

import com.xrbpowered.jdiagram.data.Data.Row;

public abstract class Filter {
	
	public abstract boolean accept(Row row);

	public static boolean eq(String v1, String v2) {
		return (v1==null || v2==null) ? v1==null && v2==null : v1.equals(v2);
	}
	
	public static Filter equals(final String hdr1, final String hdr2) {
		return new Filter() {
			@Override
			public boolean accept(Row row) {
				return eq(row.get(hdr1), row.get(hdr2));
			}
		};
	}

	public static Filter filter(final String hdr, final String v) {
		return new Filter() {
			@Override
			public boolean accept(Row row) {
				return eq(row.get(hdr), v);
			}
		};
	}

	public static Filter filter(final String[] hdrs, final String[] vs) {
		if(hdrs.length!=vs.length)
			throw new RuntimeException("Filter size mismatch");
		return new Filter() {
			@Override
			public boolean accept(Row row) {
				for(int i=0; i<hdrs.length; i++) {
					if(!eq(row.get(hdrs[i]), vs[i]))
						return false;
				}
				return true;
			}
		};
	}

	public static Filter regex(final String hdr, final String regex) {
		return new Filter() {
			@Override
			public boolean accept(Row row) {
				String v = row.get(hdr);
				return v!=null && v.matches(regex);
			}
		};
	}


	public static Filter notNull(final String... hdrs) {
		return new Filter() {
			@Override
			public boolean accept(Row row) {
				for(int i=0; i<hdrs.length; i++) {
					String v = row.get(hdrs[i]);
					if(v==null || v.isEmpty())
						return false;
				}
				return true;
			}
		};
	}

	public static Filter notNull() {
		return new Filter() {
			@Override
			public boolean accept(Row row) {
				for(int i=0; i<row.colCount(); i++) {
					String v = row.get(i);
					if(v==null || v.isEmpty())
						return false;
				}
				return true;
			}
		};
	}

	public static Filter expr(final Formula<Boolean> calc) {
		return new Filter() {
			@Override
			public boolean accept(Row row) {
				Boolean v = calc.calc(row);
				return v!=null && v;
			}
		};
	}

}
