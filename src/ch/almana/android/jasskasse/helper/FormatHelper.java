package ch.almana.android.jasskasse.helper;

import java.text.NumberFormat;
import java.util.regex.Pattern;

public class FormatHelper {
	private static final String REGEX_REPLACE_CURRENCY_SYMBOL = "[\\w\\$£€]+";
	static Pattern removeCurrenySymbol = Pattern.compile(REGEX_REPLACE_CURRENCY_SYMBOL);

	public static CharSequence formatCurrency(float n) {
		String format = NumberFormat.getCurrencyInstance().format(n);
		format.replaceAll("$", "");
		format.replaceAll("£", "");
		format.replaceAll("€", "");
		return format;
	}

}
