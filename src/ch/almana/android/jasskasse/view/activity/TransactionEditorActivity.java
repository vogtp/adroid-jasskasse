package ch.almana.android.jasskasse.view.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import ch.almana.android.jasskasse.R;
import ch.almana.android.jasskasse.view.fragment.TransactionEditFragment;

public class TransactionEditorActivity extends FragmentActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction_editor);
	}

	@Override
	public void onBackPressed() {
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentTransatctionEditor);
		if (fragment instanceof TransactionEditFragment) {
			final TransactionEditFragment editFragment = (TransactionEditFragment) fragment;
			if (editFragment.shouldSave()) {
				AlertDialog.Builder builder = new Builder(TransactionEditorActivity.this);
				builder.setTitle("Save changes");
				builder.setMessage("Do you want to save the changes of the editor?");
				builder.setNeutralButton(R.string.cancel, null);
				builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						editFragment.saveEditor();
						finish();
					}
				});
				builder.create().show();
				return;
			}
		}
		super.onBackPressed();
	}

}
