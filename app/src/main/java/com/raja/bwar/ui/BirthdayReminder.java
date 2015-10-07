package com.raja.bwar.ui;

import com.raja.bwar.R;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class BirthdayReminder extends TabActivity {
	/** Called when the activity is first created. */

	TabHost tabHost;
	final String FRIENDS_TAB = "friends";
	final String MONTH_TAB = "month";
	final String WEEK_TAB = "week";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		TabSpec contactSpec = tabHost
				.newTabSpec(FRIENDS_TAB)
				.setIndicator("Friends",
						getResources().getDrawable(R.drawable.image))
				.setContent(
						new Intent(BirthdayReminder.this, ContactTab.class));

		TabSpec monthSpec = tabHost
				.newTabSpec(MONTH_TAB)
				.setIndicator("Coming Month",
						getResources().getDrawable(R.drawable.image))
				.setContent(new Intent(BirthdayReminder.this, MonthTab.class));

		// intent = new Intent().setClass(this, WeekTab.class);
		TabSpec weekSpec = tabHost
				.newTabSpec(WEEK_TAB)
				.setIndicator("Coming Week",
						getResources().getDrawable(R.drawable.image))
				.setContent(new Intent(BirthdayReminder.this, WeekTab.class));

		tabHost.addTab(contactSpec);
		tabHost.addTab(monthSpec);
		tabHost.addTab(weekSpec);

		tabHost.setCurrentTab(0);
	}

}