package ch.almana.android.jasskasse.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import ch.almana.android.jasskasse.R;
import ch.almana.android.jasskasse.helper.MailHelper;
import ch.almana.android.jasskasse.view.adapter.SectionsPagerAdapter;

public class MainActivity extends FragmentActivity {

	private TabHost tabHost;
	private ViewPager viewPager;
	private PagerTabStrip pagerTabStrip;
	private SectionsPagerAdapter sectionsPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jasskasse_main);
		viewPager = (ViewPager) findViewById(R.id.pager);
		pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_title_strip);
		//		pagerTabStrip.setDrawFullUnderline(true);
		sectionsPagerAdapter = new SectionsPagerAdapter(viewPager, getSupportFragmentManager());

		viewPager.setAdapter(sectionsPagerAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemSend:
			MailHelper.sendMail(this);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
