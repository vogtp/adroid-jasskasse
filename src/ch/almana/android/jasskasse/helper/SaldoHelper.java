package ch.almana.android.jasskasse.helper;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import ch.almana.android.jasskasse.db.DB.Transaction;

public class SaldoHelper {

	public static float calculateSaldo(Context ctx, long time) {
		String timeStr = Long.toString(time + 1);
		CursorLoader cl = new CursorLoader(ctx, Transaction.CONTENT_URI, Transaction.PROJECTION_SALDO, Transaction.SELECTION_TIME,
				new String[] { timeStr }, Transaction.SORTORDER_DEFAULT);
		Cursor c = cl.loadInBackground();
		if (c != null && c.moveToFirst()) {
			return c.getFloat(Transaction.INDEX_SALDO_SUM);
		}
		return Float.NaN;
	}

}
