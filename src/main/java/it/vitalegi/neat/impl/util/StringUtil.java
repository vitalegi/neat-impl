package it.vitalegi.neat.impl.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StringUtil {

	public static String format(double value) {
		BigDecimal d = BigDecimal.valueOf(value);
		d = d.setScale(2, RoundingMode.HALF_UP);
		return d.toPlainString();
	}

	public static String rightPadding(String str, int padding) {
		return rightPadding(str, padding, ' ');
	}

	public static String leftPadding(String str, int padding) {
		return leftPadding(str, padding, ' ');
	}

	public static String leftPadding(String str, int padding, char paddingChar) {
		while (str.length() < padding) {
			str = paddingChar + str;
		}
		return str;
	}

	public static String rightPadding(String str, int padding, char paddingChar) {
		while (str.length() < padding) {
			str = str + paddingChar;
		}
		return str;
	}
}
