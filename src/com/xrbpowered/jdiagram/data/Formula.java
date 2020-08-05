package com.xrbpowered.jdiagram.data;

import java.util.function.Function;

import com.xrbpowered.jdiagram.data.Data.Row;

public abstract class Formula<T> {

	public abstract T calc(Row row);

	public String calcString(Row row) {
		T v = calc(row);
		return v==null ? null : v.toString();
	}
	
	public static class Var<T> extends Formula<T> {
		public T value;
		
		public Var(T init) {
			value = init;
		}
		
		@Override
		public T calc(Row row) {
			return value;
		}
	}
	
	public static <T> Formula<T> func(final Function<Row, T> f) {
		return new Formula<T>() {
			@Override
			public T calc(Row row) {
				return f.apply(row);
			}
		};
	}
	
	public static <T> Formula<T> val(final T v) { 
		return new Formula<T>() {
			@Override
			public T calc(Row row) {
				return v;
			}
		};
	}
	
	public static Formula<String> get(final String hdr) { 
		return new Formula<String>() {
			@Override
			public String calc(Row row) {
				return row.get(hdr);
			}
		};
	}

	public static Formula<Integer> getInt(final String hdr) { 
		return new Formula<Integer>() {
			@Override
			public Integer calc(Row row) {
				return row.getInt(hdr);
			}
		};
	}

	public static Formula<Double> getNum(final String hdr) { 
		return new Formula<Double>() {
			@Override
			public Double calc(Row row) {
				return row.getNum(hdr);
			}
		};
	}
	
	public static <T> Formula<T> tryOrNull(final boolean silent, final Formula<T> x) {
		return new Formula<T>() {
			@Override
			public T calc(Row row) {
				try {
					return x.calc(row);
				}
				catch(Exception e) {
					if(!silent)
						System.err.println(e.getMessage());
					return null;
				}
			}
		};
	}
	
	public static Formula<String> format(final String fmt, final Formula<?> x) {
		return new Formula<String>() {
			@Override
			public String calc(Row row) {
				Object v = x.calc(row);
				return v==null ? null : String.format(fmt, v);
			}
		};
	}
	
	public static Formula<String> percent(final Formula<Double> x) {
		return new Formula<String>() {
			@Override
			public String calc(Row row) {
				Double v = x.calc(row);
				return v==null ? null : String.format("%.2f%%", v*100.0);
			}
		};
	}

	public static <T> Formula<Boolean> eq(final Formula<T> x, final Formula<T> y) {
		return new Formula<Boolean>() {
			@Override
			public Boolean calc(Row row) {
				return x.calc(row).equals(y.calc(row));
			}
		};
	}

	public static <T> Formula<Boolean> not(final Formula<Boolean> x) {
		return new Formula<Boolean>() {
			@Override
			public Boolean calc(Row row) {
				return !x.calc(row);
			}
		};
	}

	public static Formula<String> lowercase(final Formula<String> s) {
		return new Formula<String>() {
			@Override
			public String calc(Row row) {
				String v = s.calc(row);
				return v==null ? null : v.toLowerCase();
			}
		};
	}

	public static Formula<String> uppercase(final Formula<String> s) {
		return new Formula<String>() {
			@Override
			public String calc(Row row) {
				String v = s.calc(row);
				return v==null ? null : v.toUpperCase();
			}
		};
	}

	public static Formula<Integer> inc(final Formula<Integer> x) {
		return new Formula<Integer>() {
			@Override
			public Integer calc(Row row) {
				return x.calc(row) + 1;
			}
		};
	}

	public static Formula<Integer> sumInt(final Formula<Integer> x, final Formula<Integer> y) {
		return new Formula<Integer>() {
			@Override
			public Integer calc(Row row) {
				return x.calc(row) + y.calc(row);
			}
		};
	}

	public static Formula<Integer> round(final Formula<Double> x) {
		return new Formula<Integer>() {
			@Override
			public Integer calc(Row row) {
				return (int)Math.round(x.calc(row));
			}
		};
	}
	
	public static Formula<Double> diff(final Formula<Double> x, final Formula<Double> y) {
		return new Formula<Double>() {
			@Override
			public Double calc(Row row) {
				return x.calc(row) - y.calc(row);
			}
		};
	}
	
	public static Formula<Double> sum(final Formula<Double> x, final Formula<Double> y) {
		return new Formula<Double>() {
			@Override
			public Double calc(Row row) {
				return x.calc(row) + y.calc(row);
			}
		};
	}
	
	public static Formula<Double> ratio(final Formula<Double> x, final Formula<Double> y) {
		return new Formula<Double>() {
			@Override
			public Double calc(Row row) {
				return x.calc(row) / y.calc(row);
			}
		};
	}
	
	public static Formula<Double> prod(final Formula<Double> x, final Formula<Double> y) {
		return new Formula<Double>() {
			@Override
			public Double calc(Row row) {
				return x.calc(row) * y.calc(row);
			}
		};
	}
	
	public static Formula<Boolean> inRange(final Formula<Double> x, final double min, final double max) {
		return new Formula<Boolean>() {
			@Override
			public Boolean calc(Row row) {
				double v = x.calc(row);
				return v>=min && v<=max;
			}
		};
	}

}
