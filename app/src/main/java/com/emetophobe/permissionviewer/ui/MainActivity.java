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

package com.emetophobe.permissionviewer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.emetophobe.permissionviewer.R;
import com.emetophobe.permissionviewer.ui.applist.AppListFragment;
import com.emetophobe.permissionviewer.ui.permissionlist.PermissionListFragment;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		setupViewPager();
	}

	private void setupViewPager() {
		ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
		viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
		tabLayout.setupWithViewPager(viewPager);
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
			Locale locale = Locale.getDefault();
			switch (position) {
				case 0:
					return getString(R.string.title_applications).toUpperCase(locale);
				case 1:
					return getString(R.string.title_permissions).toUpperCase(locale);
			}
			return null;
		}
	}
}
