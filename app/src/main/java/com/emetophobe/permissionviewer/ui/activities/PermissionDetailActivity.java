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

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.emetophobe.permissionviewer.R;
import com.emetophobe.permissionviewer.utils.SettingsUtils;
import com.emetophobe.permissionviewer.ui.adapters.AppListAdapter;
import com.emetophobe.permissionviewer.providers.PermissionContract.Permissions;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class PermissionDetailActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final String EXTRA_PERMISSION_NAME = "extra_permission_name";

	private static final String[] PROJECTION = {Permissions._ID, Permissions.APP_NAME, Permissions.PACKAGE_NAME};
	private static final String SORT_ORDER = Permissions.APP_NAME + " ASC";

	private AppListAdapter mAdapter;
	private String mPermissionName;

	@InjectView(R.id.permission_name)
	protected TextView mPermissionLabel;

	@InjectView(R.id.permission_description)
	protected TextView mPermissionDescription;

	@InjectView(R.id.application_count)
	protected TextView mAppCount;

	@InjectView(R.id.application_list)
	protected ListView mAppList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_permission_detail);
		ButterKnife.inject(this);

		// Set up the toolbar.
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Get the permission name from the intent extras.
		Bundle extras = getIntent().getExtras();
		mPermissionName = extras != null ? extras.getString(EXTRA_PERMISSION_NAME) : null;
		if (mPermissionName == null) {
			throw new IllegalArgumentException("Must pass a valid permission name with EXTRA_PERMISSION_NAME.");
		}

		// Set the permission name and description
		mPermissionLabel.setText(mPermissionName);
		mPermissionDescription.setText(getDescription());

		// Set up the adapter.
		mAdapter = new AppListAdapter(this);
		mAppList.setAdapter(mAdapter);

		// Load the application list
		getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String selection = Permissions.PERMISSION_NAME + "=?";
		if (!SettingsUtils.getShowSystemApps(this)) {
			selection += " AND " + Permissions.IS_SYSTEM + "=0";
		}

		return new CursorLoader(this, Permissions.CONTENT_URI, PROJECTION, selection, new String[]{mPermissionName}, SORT_ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);

		// Set the application count
		if (cursor != null) {
			mAppCount.setText(String.format(getString(R.string.application_count), cursor.getCount()));
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	/**
	 * Get the permission description string.
	 */
	private String getDescription() {
		int resId = getResources().getIdentifier("permission_" + mPermissionName, "string", getPackageName());
		return resId != 0 ? getString(resId) : getString(R.string.permission_unknown);
	}
}
