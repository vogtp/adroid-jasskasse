package ch.almana.android.jasskasse.view.fragment;

import java.text.NumberFormat;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ch.almana.android.jasskasse.R;
import ch.almana.android.jasskasse.db.DB;
import ch.almana.android.jasskasse.db.DB.Transaction;
import ch.almana.android.jasskasse.helper.FormatHelper;

public class OverviewFragment extends Fragment implements LoaderCallbacks<Cursor> {

	private static final int LOADER_SALDO = 1;
	private static final int LOADER_LASTTRANSACTION = 2;

	private TextView tvSaldo;
	private TextView tvLastTransaction;
	private TextView tvLastTransactionAmount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.overview, container, false);
		tvSaldo = (TextView) view.findViewById(R.id.tvSaldo);
		tvLastTransaction = (TextView) view.findViewById(R.id.tvLastTransaction);
		tvLastTransactionAmount = (TextView) view.findViewById(R.id.tvLastTransactionAmount);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateView();
	}

	private void updateView() {
		getLoaderManager().initLoader(LOADER_SALDO, null, this);
		getLoaderManager().initLoader(LOADER_LASTTRANSACTION, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loader, Bundle arg1) {
		switch (loader) {
		case LOADER_SALDO:
			String time = Long.toString(System.currentTimeMillis() + 1);
			return new CursorLoader(getActivity(), Transaction.CONTENT_URI, Transaction.PROJECTION_SALDO, Transaction.SELECTION_TIME,
					new String[] { time }, Transaction.SORTORDER_DEFAULT);
		case LOADER_LASTTRANSACTION:
			return new CursorLoader(getActivity(), DB.Transaction.CONTENT_URI, DB.Transaction.PROJECTION_DEFAULT, null, null, DB.Transaction.SORTORDER_DEFAULT);

		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		switch (loader.getId()) {
		case LOADER_SALDO:
			if (c != null && c.moveToFirst()) {
				tvSaldo.setText(NumberFormat.getCurrencyInstance().format(c.getFloat(Transaction.INDEX_SALDO_SUM)));
			}
			break;
		case LOADER_LASTTRANSACTION:
			if (c != null && c.moveToFirst()) {
				StringBuilder dateTime = new StringBuilder();
				long ts = c.getLong(Transaction.INDEX_TIME);
				dateTime.append(DateFormat.getDateFormat(getActivity()).format(ts));
				dateTime.append(" ");
				dateTime.append(DateFormat.getTimeFormat(getActivity()).format(ts));
				tvLastTransaction.setText(dateTime);
				tvLastTransactionAmount.setText(FormatHelper.formatCurrency(c.getFloat(Transaction.INDEX_AMOUNT)));
			}
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// nothing to do?
	}

}
