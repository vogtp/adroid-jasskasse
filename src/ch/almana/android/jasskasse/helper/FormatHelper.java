package ch.almana.android.jasskasse.helper;

import java.text.NumberFormat;

public class FormatHelper {
	private static final String REGEX_REPLACE_CURRENCY_SYMBOL = "[a-zA-Z\\$£€]+";

	public static CharSequence formatCurrency(float n) {
		String format = NumberFormat.getCurrencyInstance().format(n);
		format = format.replaceAll(REGEX_REPLACE_CURRENCY_SYMBOL, "");
		return format;
	}

}
