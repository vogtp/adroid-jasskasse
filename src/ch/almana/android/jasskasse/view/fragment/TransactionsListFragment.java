package ch.almana.android.jasskasse.view.fragment;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import ch.almana.android.jasskasse.R;
import ch.almana.android.jasskasse.db.DB;
import ch.almana.android.jasskasse.db.DB.Transaction;
import ch.almana.android.jasskasse.helper.FormatHelper;

public class TransactionsListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter adapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		setListShown(true);

		adapter = new SimpleCursorAdapter(getActivity(), R.layout.transactions_list_item, null,
				new String[] { Transaction.NAME_TIME, Transaction.NAME_AMOUNT, Transaction.NAME_SALDO, Transaction.NAME_COMMENT },
				new int[] { R.id.tvDate, R.id.tvAmount, R.id.tvSaldo, R.id.tvComment }, 0);
		ViewBinder binder = new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor, int idx) {
				if (Transaction.INDEX_TIME == idx) {
					if (view instanceof TextView) {
						long time = cursor.getLong(idx);
						((TextView) view).setText(DateFormat.getDateFormat(getActivity()).format(time));
						return true;
					}
				} else if (Transaction.INDEX_AMOUNT == idx || Transaction.INDEX_SALDO == idx) {
					if (view instanceof TextView) {
						float money = cursor.getFloat(idx);
						if (Transaction.INDEX_SALDO == idx) {
							String time = Long.toString(cursor.getLong(Transaction.INDEX_TIME) + 1);
							CursorLoader cl = new CursorLoader(getActivity(), Transaction.CONTENT_URI, Transaction.PROJECTION_SALDO, Transaction.SELECTION_TIME,
									new String[] { time }, Transaction.SORTORDER_DEFAULT);
							Cursor c = cl.loadInBackground();
							if (c != null && c.moveToFirst()) {
								money = c.getFloat(Transaction.INDEX_SALDO_SUM);
							}
						}
						int color = getActivity().getResources().getColor(R.color.moneyGreen);
						if (money < 0f) {
							color = getActivity().getResources().getColor(R.color.moneyRed);
						}
						((TextView) view).setTextColor(color);

						((TextView) view).setText(FormatHelper.formatCurrency(money));
						return true;
					}
				} else if (Transaction.INDEX_COMMENT == idx) {
					String comment = cursor.getString(Transaction.INDEX_COMMENT);
					if (comment == null || "".equals(comment.trim())) {
						view.setVisibility(View.GONE);
					} else {
						view.setVisibility(View.VISIBLE);
					}
				}
				return false;
			}

		};
		adapter.setViewBinder(binder);
		setListAdapter(adapter);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Uri uri = ContentUris.withAppendedId(DB.Transaction.CONTENT_URI, id);
		startActivity(new Intent(Intent.ACTION_EDIT, uri));
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loader, Bundle bundle) {
		return new CursorLoader(getActivity(), DB.Transaction.CONTENT_URI, DB.Transaction.PROJECTION_DEFAULT, null, null, DB.Transaction.SORTORDER_DEFAULT);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		adapter.swapCursor(c);
		// The list should now be shown.
		if (isResumed()) {
			setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

}
