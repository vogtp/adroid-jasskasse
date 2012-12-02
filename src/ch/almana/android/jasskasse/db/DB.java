package ch.almana.android.jasskasse.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import ch.almana.android.db.backend.DBBase;
import ch.almana.android.db.backend.UriTableMapping;
import ch.almana.android.jasskasse.log.Logger;

public interface DB extends DBBase {

	public static final String AUTHORITY = "ch.almana.android.jasskasse";

	public static final String DATABASE_NAME = "jasskasse";

	public class UriTableConfig {

		private static UriTableMapping[] map;

		public static UriTableMapping[] getUriTableMapping() {
			return map;
		}

		static {
			map = new UriTableMapping[] {
					Transaction.URI_TABLE_MAPPING
			};
		}

	}

	public class OpenHelper extends SQLiteOpenHelper {

		private static final int DATABASE_VERSION = 1;

		private static final String CREATE_TRANSACTIONS_TABLE = "create table if not exists " + Transaction.TABLE_NAME + " (" + DB.NAME_ID + " integer primary key, "
				+ DB.Transaction.NAME_TIME + " long, " + DB.Transaction.NAME_AMOUNT + " real," + DB.Transaction.NAME_SALDO + " int,"
				+ DB.Transaction.NAME_COMMENT + " text)";


		public OpenHelper(Context context) {
			super(context, DB.DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TRANSACTIONS_TABLE);
			//			db.execSQL("create index idx_transaction_time " + Transaction.TABLE_NAME + " (" + Transaction.NAME_TIME + "); ");
			Logger.i("Created tables ");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			switch (oldVersion) {
			case 1:
				Logger.w("Upgrading to DB Version 2...");
				// nobreak

			case 2:
				Logger.w("Upgrading to DB Version 3...");

			default:
				Logger.w("Finished DB upgrading!");
				break;
			}
		}
	}


	public interface Transaction {

		public static final String TABLE_NAME = "transactions";
		public static final String CONTENT_ITEM_NAME = "transaction";

		public static final boolean NOTIFY_ON_CHANGE = true;

		public static final String NAME_TIME = "time";
		public static final String NAME_AMOUNT = "amount";
		public static final String NAME_SALDO = "saldo";
		public static final String NAME_COMMENT = "comment";

		public static final int INDEX_TIME = 1;
		public static final int INDEX_AMOUNT = 2;
		public static final int INDEX_SALDO = 3;
		public static final int INDEX_COMMENT = 4;

		public static final int INDEX_SALDO_SUM = 2;

		public static String CONTENT_URI_STRING = "content://" + AUTHORITY + "/" + CONTENT_ITEM_NAME;
		public static Uri CONTENT_URI = Uri.parse(CONTENT_URI_STRING);
		static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + AUTHORITY + "." + CONTENT_ITEM_NAME;
		static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + AUTHORITY + "." + CONTENT_ITEM_NAME;
		public static final UriTableMapping URI_TABLE_MAPPING = new UriTableMapping(CONTENT_URI, TABLE_NAME, CONTENT_ITEM_NAME, CONTENT_TYPE, CONTENT_ITEM_TYPE, NOTIFY_ON_CHANGE);

		public static final String[] PROJECTION_DEFAULT = new String[] { NAME_ID, NAME_TIME, NAME_AMOUNT, NAME_SALDO, NAME_COMMENT };
		public static final String[] PROJECTION_SALDO = new String[] { NAME_ID, NAME_TIME, "sum(" + NAME_AMOUNT + ") as " + NAME_SALDO };

		public static final String SORTORDER_DEFAULT = NAME_TIME + " DESC";

		static final String SORTORDER_REVERSE = NAME_TIME + " ASC";

		public static final String SELECTION_TIME = NAME_TIME + " <?";

	}

}