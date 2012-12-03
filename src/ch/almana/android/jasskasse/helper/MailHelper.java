package ch.almana.android.jasskasse.helper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.text.format.DateFormat;
import ch.almana.android.jasskasse.R;
import ch.almana.android.jasskasse.db.DB.Transaction;
import ch.almana.android.jasskasse.log.Logger;

public class MailHelper {

	public static void sendMail(Context ctx) {
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.setType("message/rfc822");
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, ctx.getString(R.string.app_name));
		sendIntent.putExtra(Intent.EXTRA_TEXT, getBody(ctx));
		Logger.v("Send report: sending");
		ctx.startActivity(sendIntent);
	}

	private static String getBody(Context ctx) {
		Cursor c = null;
		try {
			CursorLoader cl = new CursorLoader(ctx, Transaction.CONTENT_URI, Transaction.PROJECTION_DEFAULT, null, null, Transaction.SORTORDER_REVERSE);
			c = cl.loadInBackground();
			StringBuilder sb = new StringBuilder();
			java.text.DateFormat dateFormat = DateFormat.getDateFormat(ctx);
			while (c.moveToNext()) {
				long time = c.getLong(Transaction.INDEX_TIME);
				sb.append(dateFormat.format(time));
				sb.append("\t");
				sb.append(FormatHelper.formatCurrency(c.getFloat(Transaction.INDEX_AMOUNT)));
				sb.append("\t");
				sb.append(FormatHelper.formatCurrency(SaldoHelper.calculateSaldo(ctx, time)));
				String comment = c.getString(Transaction.INDEX_COMMENT);
				if (comment != null) {
					sb.append("\t");
					sb.append(comment);
				}
				sb.append("\n");
			}
			return sb.toString();
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
		}
	}
}
