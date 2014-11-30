/*
 * Copyright (C) 2013-2014 Mike Cunningham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.emetophobe.permissionlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Locale;


public class MainActivity extends ActionBarActivity {
	private static final String PREF_FIRST_RUN = "pref_first_run";
	private static final String VIEW_PAGER_POSITION = "view_pager_position";

	private int mCurrentlySelectedIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Make sure default preferences are initialized
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		// Create the permission list the first time the app is run
		createPermissionList();

		// Set up the toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// Restore the previous tab position
		if (savedInstanceState != null) {
			mCurrentlySelectedIndex = savedInstanceState.getInt(VIEW_PAGER_POSITION, 0);
		}

		// Set up the ViewPager with the pager adapter
		ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
		viewPager.setCurrentItem(mCurrentlySelectedIndex);
		viewPager.setOnPageChangeListener(mPageChangeListener);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(VIEW_PAGER_POSITION, mCurrentlySelectedIndex);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Listen for view pager changes.
	 */
	private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageScrolled(int i, float v, int i2) {

		}

		@Override
		public void onPageSelected(int i) {
			mCurrentlySelectedIndex = i;
		}

		@Override
		public void onPageScrollStateChanged(int i) {

		}
	};

	/**
	 * Custom FragmentPagerAdapter for handling the fragment tabs/pages.
	 */
	public class PagerAdapter extends FragmentPagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return new AppListFragment();
				case 1:
					return new PermissionListFragment();
			}
			return null;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
				case 0:
					return getString(R.string.action_applications).toUpperCase(l);
				case 1:
					return getString(R.string.action_permissions).toUpperCase(l);
			}
			return null;
		}
	}

	private void createPermissionList() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getBoolean(PREF_FIRST_RUN, true)) {
			new PermissionScanner(this).start();
			prefs.edit().putBoolean(PREF_FIRST_RUN, false).apply();
		}
	}
}
