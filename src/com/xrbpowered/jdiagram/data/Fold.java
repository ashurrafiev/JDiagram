package com.xrbpowered.jdiagram.data;

import static com.xrbpowered.jdiagram.data.Formula.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import com.xrbpowered.jdiagram.data.Data.Row;
import com.xrbpowered.jdiagram.data.Formula.Var;

public abstract class Fold<T> {

	public abstract T fold(Data data);

	public static Fold<Integer> count = new Fold<Integer>() {
		@Override
		public Integer fold(Data data) {
			return data.count();
		}
	};

	public static Fold<Double> sum(final String hdr) {
		return new Fold<Double>() {
			@Override
			public Double fold(Data data) {
				Var<Double> s = new Var<>(0.0);
				data.fold(s, Formula.sum(getNum(hdr), s));
				return s.value;
			}
		};
	}
	
	public static Fold<Double> avg(final String hdr) {
		return new Fold<Double>() {
			@Override
			public Double fold(Data data) {
				Var<Double> s = new Var<>(0.0);
				data.fold(s, Formula.sum(getNum(hdr), s));
				return s.value / data.count();
			}
		};
	}

	public static Fold<Double> min(final String hdr) {
		return new Fold<Double>() {
			@Override
			public Double fold(Data data) {
				final Var<Double> min = new Var<>(null);
				data.fold(min, new Formula<Double>() {
					@Override
					public Double calc(Row row) {
						Double v = row.getNum(hdr);
						return min.value==null ? v : Math.min(min.value, v);
					}
				});
				return min.value;
			}
		};
	}

	public static Fold<Double> max(final String hdr) {
		return new Fold<Double>() {
			@Override
			public Double fold(Data data) {
				final Var<Double> max = new Var<>(null);
				data.fold(max, new Formula<Double>() {
					@Override
					public Double calc(Row row) {
						Double v = row.getNum(hdr);
						return max.value==null ? v : Math.max(max.value, v);
					}
				});
				return max.value;
			}
		};
	}
	
	public static Fold<Integer> sumInt(final String hdr) {
		return new Fold<Integer>() {
			@Override
			public Integer fold(Data data) {
				Var<Integer> s = new Var<>(0);
				data.fold(s, Formula.sumInt(getInt(hdr), s));
				return s.value;
			}
		};
	}
	
	public static Fold<Integer> minInt(final String hdr) {
		return new Fold<Integer>() {
			@Override
			public Integer fold(Data data) {
				final Var<Integer> min = new Var<>(null);
				data.fold(min, new Formula<Integer>() {
					@Override
					public Integer calc(Row row) {
						Integer v = row.getInt(hdr);
						return min.value==null ? v : Math.min(min.value, v);
					}
				});
				return min.value;
			}
		};
	}

	public static Fold<Integer> maxInt(final String hdr) {
		return new Fold<Integer>() {
			@Override
			public Integer fold(Data data) {
				final Var<Integer> max = new Var<>(null);
				data.fold(max, new Formula<Integer>() {
					@Override
					public Integer calc(Row row) {
						Integer v = row.getInt(hdr);
						return max.value==null ? v : Math.max(max.value, v);
					}
				});
				return max.value;
			}
		};
	}

	public static double sum(Data data, String hdr) {
		return sum(hdr).fold(data);
	}

	public static double avg(Data data, String hdr) {
		return avg(hdr).fold(data);
	}

	public static double min(Data data, String hdr) {
		return min(hdr).fold(data);
	}

	public static double max(Data data, String hdr) {
		return max(hdr).fold(data);
	}

	public static int sumInt(Data data, String hdr) {
		return sumInt(hdr).fold(data);
	}

	public static int minInt(Data data, String hdr) {
		return minInt(hdr).fold(data);
	}

	public static int maxInt(Data data, String hdr) {
		return maxInt(hdr).fold(data);
	}

	public static Iterable<String> values(Data data, final String hdr) {
		final Var<ArrayList<String>> list = new Var<>(new ArrayList<String>(data.count()));
		data.fold(list, new Formula<ArrayList<String>>() {
			@Override
			public ArrayList<String> calc(Row row) {
				list.value.add(row.get(hdr));
				return list.value;
			}
		});
		return list.value;
	}

	public static Iterable<String> uniques(Data data, final String hdr) {
		final Var<LinkedHashSet<String>> set = new Var<>(new LinkedHashSet<String>());
		data.fold(set, new Formula<LinkedHashSet<String>>() {
			@Override
			public LinkedHashSet<String> calc(Row row) {
				set.value.add(row.get(hdr));
				return set.value;
			}
		});
		return set.value;
	}

	private static String join(Iterable<String> list, String open, String sep, String close) {
		StringBuilder sb = new StringBuilder();
		sb.append(open);
		boolean first = true;
		for(String s : list) {
			if(!first) sb.append(sep);
			sb.append(s);
			first = false;
		}
		sb.append(close);
		return sb.toString();
	}
	
	public static Fold<String> listValues(final String hdr, final String open, final String sep, final String close) {
		return new Fold<String>() {
			@Override
			public String fold(Data data) {
				return join(values(data, hdr), open, sep, close);
			}
		};
	}
	
	public static Fold<String> listValues(final String hdr, final String sep) {
		return listValues(hdr, "", sep, "");
	}

	public static Fold<String> listUniques(final String hdr, final String open, final String sep, final String close) {
		return new Fold<String>() {
			@Override
			public String fold(Data data) {
				return join(uniques(data, hdr), open, sep, close);
			}
		};
	}

	public static Fold<String> listUniques(final String hdr, final String sep) {
		return listUniques(hdr, "", sep, "");
	}

}
