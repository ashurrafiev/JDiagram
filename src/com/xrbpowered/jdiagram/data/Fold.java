package com.xrbpowered.jdiagram.data;

import static com.xrbpowered.jdiagram.data.Formula.*;

import com.xrbpowered.jdiagram.data.Data.Row;

public abstract class Fold {

	public static double sum(Data data, String hdr) {
		Var<Double> s = new Var<>(0.0);
		data.fold(s, Formula.sum(getNum(hdr), s));
		return s.value;
	}

	public static double avg(Data data, String hdr) {
		return sum(data, hdr) / data.count();
	}

	public static double min(Data data, final String hdr) {
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

	public static double max(Data data, final String hdr) {
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

}
