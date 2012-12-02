package ch.almana.android.jasskasse.application;

import android.app.Application;
import ch.almana.android.jasskasse.db.DBInitialiser;

public class JasskasseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		DBInitialiser dbInitialiser = new DBInitialiser(this);
		dbInitialiser.initDemoData();
	}

}
