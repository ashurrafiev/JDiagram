package com.xrbpowered.jdiagram.data;

import com.xrbpowered.jdiagram.data.Data.Row;

public abstract class Filter {
	
	public abstract boolean accept(Row row);
	
	public static Filter equals(final String hdr1, final String hdr2) {
		return new Filter() {
			@Override
			public boolean accept(Row row) {
				String v1 = row.get(hdr1);
				String v2 = row.get(hdr2);
				return (v1==null || v2==null) ? v1==null && v2==null : v1.equals(v2);
			}
		};
	}

	public static Filter filter(final String hdr, final String v) {
		return new Filter() {
			@Override
			public boolean accept(Row row) {
				String v1 = row.get(hdr);
				return (v1==null || v==null) ? v1==null && v==null : v1.equals(v);
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
					String v1 = row.get(hdrs[i]);
					boolean res = (v1==null || vs[i]==null) ? v1==null && vs[i]==null : v1.equals(vs[i]);
					if(!res)
						return false;
				}
				return true;
			}
		};
	}
	
	public static Filter notNull(final String... hdrs) {
		return new Filter() {
			@Override
			public boolean accept(Row row) {
				String[] h = hdrs==null ? row.data().headers : hdrs;
				for(int i=0; i<h.length; i++) {
					String v = row.get(h[i]);
					if(v==null || v.isEmpty())
						return false;
				}
				return true;
			}
		};
	}

	public static Filter notNull() {
		return notNull((String[])null);
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
