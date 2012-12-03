package ch.almana.android.jasskasse.view.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import ch.almana.android.jasskasse.R;
import ch.almana.android.jasskasse.view.fragment.OverviewFragment;
import ch.almana.android.jasskasse.view.fragment.TransactionsListFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

	private static final int FRAG_OVERVIEW = 0;
	private static final int FRAG_TRANSACTIONS = 1;

	private final ViewPager viewPager;

	public SectionsPagerAdapter(ViewPager viewPager, FragmentManager fm) {
		super(fm);
		this.viewPager = viewPager;
	}

	@Override
	public Fragment getItem(int i) {
		switch (i) {
		case FRAG_OVERVIEW:
			return new OverviewFragment();

		case FRAG_TRANSACTIONS:
			return new TransactionsListFragment();

		default:
			return new Fragment();
		}
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Context ctx = viewPager.getContext();
		switch (position) {
		case FRAG_OVERVIEW:
			return ctx.getString(R.string.frag_title_overview);
		case FRAG_TRANSACTIONS:
			return ctx.getString(R.string.frag_title_transactions);
		default:
			return ctx.getString(R.string.frag_title_unknown);
		}
	}



}