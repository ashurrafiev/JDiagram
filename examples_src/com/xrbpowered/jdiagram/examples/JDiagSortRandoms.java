package com.xrbpowered.jdiagram.examples;

import java.util.Random;

import com.xrbpowered.jdiagram.data.Data;
import com.xrbpowered.jdiagram.data.Fold;
import com.xrbpowered.jdiagram.data.Formula;

import static com.xrbpowered.jdiagram.data.Formula.*;

public class JDiagSortRandoms {

	public static void main(String[] args) {
		Random random = new Random();
		String numFmt = "%.5f";
		
		Data data = new Data("label", "x", "y");
		for(int i=0; i<10; i++) {
			data.addRow(
				Character.valueOf((char)(i+'A')).toString(),
				String.format(numFmt, random.nextDouble()),
				String.format(numFmt, random.nextDouble())
			);
		}
		
		data.addCol("dist", format(numFmt, new Formula<Double>() {
			@Override
			public Double calc(Data.Row row) {
				double x = row.getNum("x");
				double y = row.getNum("y");
				return Math.sqrt(x*x+y*y);
			}
		}));
		
		data.print();

		System.out.printf("\nx in [%.5f, %.5f]\n", Fold.min(data, "x"), Fold.max(data, "x"));
		System.out.printf("y in [%.5f, %.5f]\n", Fold.min(data, "y"), Fold.max(data, "y"));
		System.out.printf("Avg dist = %.5f\n", Fold.avg(data, "dist"));

		data.sort(getNum("dist"));
		System.out.println("\nSorted by dist:");
		data.print();
	}

}
