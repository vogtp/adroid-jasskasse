package ch.almana.android.jasskasse.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ch.almana.android.jasskasse.R;
import ch.almana.android.jasskasse.helper.MailHelper;

public class MailFragment extends Fragment {

	private TextView etBody;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.mail_fragment, container, false);
		etBody = (TextView) v.findViewById(R.id.etBody);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		update();
	}

	public void update() {
		MailHelper.sendMail(getActivity());
	}

}
