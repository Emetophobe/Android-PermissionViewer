/*
 * Copyright (C) 2013-2015 Mike Cunningham
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

package com.emetophobe.permissionviewer.ui.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.emetophobe.permissionviewer.threads.InitDatabaseRunnable;
import com.emetophobe.permissionviewer.R;
import com.emetophobe.permissionviewer.ui.fragments.AppListFragment;
import com.emetophobe.permissionviewer.ui.fragments.PermissionListFragment;

import java.util.Locale;


public class MainActivity extends ActionBarActivity {
	private static final String PREF_FIRST_RUN = "pref_first_run";

	private static int sViewPagerPosition = 0;
	private ProgressDialog mDialog;

	/**
	 * Initialize the main activity.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Make sure default preferences are initialized.
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		// Set up the toolbar.
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
				new IntentFilter(InitDatabaseRunnable.PERMISSION_INTENT));

		// Initialize the database the first time the application is run.
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getBoolean(PREF_FIRST_RUN, true)) {
			new Thread(new InitDatabaseRunnable(this)).start();
			prefs.edit().putBoolean(PREF_FIRST_RUN, false).apply();
		} else {
			// Just display the view pager if the database has already been initialized.
			initViewPager();
		}
	}

	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}

	/**
	 * Initialize the view pager and display the first fragment.
	 */
	private void initViewPager() {
		ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// Remember the current view pager position
				sViewPagerPosition = position;
			}
		});

		// Restore the pager position
		viewPager.setCurrentItem(sViewPagerPosition);
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
	 * Receives progress updates from the PermissionScanner.
	 */
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent
			int action = intent.getIntExtra(InitDatabaseRunnable.PROGRESS_MESSAGE, 0);
			switch (action) {
				// Display a progress dialog while the database is initializing.
				case InitDatabaseRunnable.MESSAGE_PROGRESS_INIT:
					mDialog = new ProgressDialog(MainActivity.this);
					mDialog.setTitle(getString(R.string.progress_title));
					mDialog.setMessage(getString(R.string.progress_message));
					mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					mDialog.setIndeterminate(false);
					mDialog.setCancelable(false);
					mDialog.setMax(intent.getIntExtra(InitDatabaseRunnable.PROGRESS_VALUE, 0));
					mDialog.show();
					break;

				// Increment the progress dialog
				case InitDatabaseRunnable.MESSAGE_PROGRESS_UPDATE:
					mDialog.setProgress(intent.getIntExtra(InitDatabaseRunnable.PROGRESS_VALUE, 0));
					break;

				// Complete the progress dialog and initialize the view pager once the database has been initialized.
				case InitDatabaseRunnable.MESSAGE_PROGRESS_COMPLETE:
					mDialog.cancel();
					initViewPager();
					break;
			}
		}
	};

	/**
	 * Pager adapter that is used to display the app list and permission list fragments.
	 */
	private class PagerAdapter extends FragmentPagerAdapter {
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
}
