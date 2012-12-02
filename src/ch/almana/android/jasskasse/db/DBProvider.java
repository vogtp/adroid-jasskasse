package ch.almana.android.jasskasse.db;

import android.database.sqlite.SQLiteOpenHelper;
import ch.almana.android.db.backend.DBProviderBase;
import ch.almana.android.db.backend.UriTableMapping;


public class DBProvider extends DBProviderBase {

	@Override
	protected SQLiteOpenHelper getOpenHelper() {
		return new DB.OpenHelper(getContext());
	}

	@Override
	protected UriTableMapping[] getUriTableMapping() {
		return DB.UriTableConfig.getUriTableMapping();
	}

	@Override
	protected String getAuthority() {
		return DB.AUTHORITY;
	}
}
