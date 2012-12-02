package ch.almana.android.jasskasse.view.fragment;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Intent;
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
import ch.almana.android.logdebug.StringUtils;

public class TransactionEditFragment extends Fragment {

	//	private static final String STATE_AMOUNT = "STATE_AMOUNT";

	private static final float NO_AMOUNT = Float.POSITIVE_INFINITY;
	private Button buDeposit;
	private Button buWithdraw;
	private EditText etAmount;
	private EditText etComment;
	private DatePicker dpDate;

	private RadioButton radioDeposit;
	private RadioButton radioWithdraw;
	private RadioGroup radioGroup;
	private LinearLayout llButtons;
	private boolean isUpdate = false;;
	private boolean isEditor = false;
	private Uri contentUri;
	private float amount;
	private Calendar time;
	private String comment;

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
			if (Intent.ACTION_EDIT.equals(getActivity().getIntent().getAction())) {
				CursorLoader cl = new CursorLoader(getActivity(), contentUri, Transaction.PROJECTION_DEFAULT, null, null, null);
				Cursor c = cl.loadInBackground();
				if (c != null && c.moveToFirst()) {
					amount = c.getFloat(Transaction.INDEX_AMOUNT);
					radioDeposit.setChecked(amount >= 0);
					radioWithdraw.setChecked(amount < 0);
					etAmount.setText(Float.toString(Math.abs(amount)));
					comment = c.getString(Transaction.INDEX_COMMENT);
					etComment.setText(comment);
					time = Calendar.getInstance();
					time.setTimeInMillis(c.getLong(Transaction.INDEX_TIME));
					dpDate.updateDate(time.get(Calendar.YEAR), time.get(Calendar.MONTH), time.get(Calendar.DAY_OF_MONTH));
					isUpdate = true;
					if (!c.isClosed()) {
						c.close();
					}
				}
			}
		}

		if (isEditor) {
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

	public void saveEditor() {
		save(radioWithdraw.isChecked());
	}

	protected void save(boolean withdraw) {
		float number = getAmount();
		if (number == NO_AMOUNT) {
			return;
		}
		number = Math.abs(number);
		if (withdraw) {
			number *= -1f;
		}
		ContentValues values = new ContentValues();
		values.put(Transaction.NAME_AMOUNT, number);
		values.put(Transaction.NAME_TIME, getTime().getTimeInMillis());
		values.put(Transaction.NAME_COMMENT, getComment());
		if (isUpdate) {
			getActivity().getContentResolver().update(contentUri, values, null, null);
		} else {
			getActivity().getContentResolver().insert(Transaction.CONTENT_URI, values);
		}
		etAmount.setText(null);
		etComment.setText(null);
	}

	private float getAmount() {
		String a = etAmount.getText().toString();
		try {
			return Float.parseFloat(a);
		} catch (NumberFormatException e) {
			Logger.e("Cannot parse as number: " + a, e);
			return NO_AMOUNT;
		}
	}

	private String getComment() {
		String c = etComment.getText().toString();
		return c != "" ? c : null;
	}

	private Calendar getTime() {
		Calendar cal = Calendar.getInstance();
		if (time != null) {
			cal.setTimeInMillis(time.getTimeInMillis());
		}
		cal.set(Calendar.YEAR, dpDate.getYear());
		cal.set(Calendar.MONTH, dpDate.getMonth());
		cal.set(Calendar.DAY_OF_MONTH, dpDate.getDayOfMonth());
		return cal;
	}

	public boolean shouldSave() {
		return isEditor && !(getTime().equals(time) && StringUtils.equal(getComment(), comment) && getAmount() != NO_AMOUNT && getAmount() == amount);
	}

}
