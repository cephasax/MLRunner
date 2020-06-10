package br.ufrn.imd.utils;

public class NumberUtils {

	public static String formatValue(Double value) {
		String s;
		if (value < 100) {
			s = String.format("%.4f", value);
		} else {
			s = String.format("%.3f", value);
		}
		return s;
	}
	
}
