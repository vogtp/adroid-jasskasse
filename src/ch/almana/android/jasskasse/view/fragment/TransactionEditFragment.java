package ch.almana.android.jasskasse.view.fragment;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import ch.almana.android.jasskasse.R;
import ch.almana.android.jasskasse.db.DB.Transaction;
import ch.almana.android.jasskasse.log.Logger;

public class TransactionEditFragment extends Fragment {

	//	private static final String STATE_AMOUNT = "STATE_AMOUNT";

	private Button buDeposit;
	private Button buWithdraw;
	private EditText etAmount;
	private EditText etComment;
	private DatePicker dpDate;

	private RadioButton radioDeposit;
	private RadioButton radioWithdraw;
	private RadioGroup radioGroup;
	private LinearLayout llButtons;
	private boolean saved = true;
	private boolean isUpdate = false;;
	private boolean isEditor = false;
	private Uri contentUri;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.transaction_edit_fragment, container, false);
		llButtons = (LinearLayout) v.findViewById(R.id.llButtons);
		buDeposit = (Button) v.findViewById(R.id.buDeposit);
		buWithdraw = (Button) v.findViewById(R.id.buWithdraw);
		etAmount = (EditText) v.findViewById(R.id.etAmount);
		etComment = (EditText) v.findViewById(R.id.etComment);
		dpDate = (DatePicker) v.findViewById(R.id.dpDate);
		radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);
		radioDeposit = (RadioButton) v.findViewById(R.id.radioDeposit);
		radioWithdraw = (RadioButton) v.findViewById(R.id.radioWithdraw);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		contentUri = getActivity().getIntent().getData();
		if (contentUri != null) {
			isEditor = true;
			CursorLoader cl = new CursorLoader(getActivity(), contentUri, Transaction.PROJECTION_DEFAULT, null, null, null);
			Cursor c = cl.loadInBackground();
			if (c != null && c.moveToFirst()) {
				float amount = c.getFloat(Transaction.INDEX_AMOUNT);
				radioDeposit.setChecked(amount >= 0);
				radioWithdraw.setChecked(amount < 0);
				etAmount.setText(Float.toString(Math.abs(amount)));
				etComment.setText(c.getString(Transaction.INDEX_COMMENT));
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(c.getLong(Transaction.INDEX_TIME));
				dpDate.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
				isUpdate = true;
				saved = false;
				if (!c.isClosed()) {
					c.close();
				}
			}
		}

		if (isEditor) {
			saved = false;
			//			llButtons.setVisibility(View.GONE);
			buDeposit.setText(R.string.save);
			buDeposit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					saveEditor();
					getActivity().finish();
				}

			});
			buWithdraw.setText(R.string.cancel);
			buWithdraw.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getActivity().finish();
				}
			});
		} else {
			radioGroup.setVisibility(View.GONE);
			buDeposit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					save(false);
				}
			});
			buWithdraw.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					save(true);
				}
			});
		}

	}

	private void saveEditor() {
		save(radioWithdraw.isChecked());
	}

	protected void save(boolean isNegative) {
		String numberStr = etAmount.getText().toString();
		try {
			float number = Float.parseFloat(numberStr);
			number = Math.abs(number);
			if (isNegative) {
				number *= -1f;
			}
			ContentValues values = new ContentValues();
			values.put(Transaction.NAME_AMOUNT, number);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, dpDate.getYear());
			cal.set(Calendar.MONTH, dpDate.getMonth());
			cal.set(Calendar.DAY_OF_MONTH, dpDate.getDayOfMonth());
			values.put(Transaction.NAME_TIME, cal.getTimeInMillis());
			values.put(Transaction.NAME_COMMENT, etComment.getText().toString());
			if (isUpdate) {
				getActivity().getContentResolver().update(contentUri, values, null, null);
			} else {
				getActivity().getContentResolver().insert(Transaction.CONTENT_URI, values);
			}
			etAmount.setText(null);
			etComment.setText(null);
			saved = true;
		} catch (NumberFormatException e) {
			Logger.e("Cannot parse as number: " + numberStr, e);
		}
	}

	@Override
	public void onPause() {
		if (isEditor && !saved) {
			AlertDialog.Builder builder = new Builder(getActivity());
			builder.setTitle("Save changes");
			builder.setMessage("Do you want to save the changes of the editor?");
			builder.setNegativeButton(android.R.string.no, null);
			builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					saveEditor();
				}
			});
			builder.create().show();
		}
		super.onPause();
	}

}
