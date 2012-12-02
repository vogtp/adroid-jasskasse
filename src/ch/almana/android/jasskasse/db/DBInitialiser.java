package ch.almana.android.jasskasse.db;

import java.util.Calendar;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import ch.almana.android.jasskasse.db.DB.Transaction;
import ch.almana.android.jasskasse.log.Logger;

public class DBInitialiser {

	private static final boolean RECREATE = false;
	private final Context ctx;
	private final ContentResolver resolver;

	public DBInitialiser(Context ctx) {
		super();
		this.ctx = ctx;
		this.resolver = ctx.getContentResolver();

		new DB.OpenHelper(ctx).getWritableDatabase();
	}

	public void initDemoData() {
		if (Logger.DEBUG) {
			Logger.w("Initalising with demo data");
			if (RECREATE) {
				resolver.delete(Transaction.CONTENT_URI, null, null);
			} else {
				Cursor cursor = resolver.query(Transaction.CONTENT_URI, Transaction.PROJECTION_DEFAULT, null, null, null);
				if (cursor != null && cursor.moveToFirst()) {
					return;
				}
			}
			Calendar cal = Calendar.getInstance();
			for (int i = 0; i < 1000; i++) {
				float amount = (float) (Math.random() * 100f);
				amount -= 40f;
				insert(cal.getTimeInMillis(), amount);
				cal.add(Calendar.DAY_OF_YEAR, -7);
			}
			Logger.w("Finished creating demo data");
		}
	}

	private void insert(long time, float amount) {
		ContentValues values = new ContentValues();
		values.put(Transaction.NAME_TIME, time);
		values.put(Transaction.NAME_AMOUNT, amount);
		if (amount < 0) {
			values.put(Transaction.NAME_COMMENT, "Pizza essen");
		}
		resolver.insert(Transaction.CONTENT_URI, values);
	}

}
