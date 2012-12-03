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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import ch.almana.android.jasskasse.R;
import ch.almana.android.jasskasse.db.DB;
import ch.almana.android.jasskasse.db.DB.Transaction;
import ch.almana.android.jasskasse.helper.FormatHelper;
import ch.almana.android.jasskasse.helper.SaldoHelper;
import ch.almana.android.jasskasse.log.Logger;

public class TransactionsListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter adapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		setListShown(true);

		adapter = new SimpleCursorAdapter(getActivity(), R.layout.transactions_list_item, null,
				new String[] { Transaction.NAME_TIME, Transaction.NAME_AMOUNT, DB.NAME_ID /*dummy*/, Transaction.NAME_COMMENT },
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
				} else if (Transaction.INDEX_AMOUNT == idx || DB.INDEX_ID == idx) {
					if (view instanceof TextView) {
						float money = cursor.getFloat(idx);
						if (DB.INDEX_ID == idx) {
							money = SaldoHelper.calculateSaldo(getActivity(), cursor.getLong(Transaction.INDEX_TIME));
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
		getListView().setOnCreateContextMenuListener(this);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Uri uri = ContentUris.withAppendedId(DB.Transaction.CONTENT_URI, id);
		startActivity(new Intent(Intent.ACTION_EDIT, uri));
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getActivity().getMenuInflater().inflate(R.menu.list_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);

		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			Logger.e("bad menuInfo", e);
			return false;
		}

		final Uri uri = ContentUris.withAppendedId(DB.Transaction.CONTENT_URI, info.id);
		switch (item.getItemId()) {
		case R.id.itemInsert:
			startActivity(new Intent(Intent.ACTION_INSERT, DB.Transaction.CONTENT_URI));
			return true;

		case R.id.itemEdit:
			startActivity(new Intent(Intent.ACTION_EDIT, uri));
			return true;

		case R.id.itemDelete:
			getActivity().getContentResolver().delete(uri, null, null);
			return true;

		default:
			return false;
		}

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
