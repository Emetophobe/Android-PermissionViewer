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

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.emetophobe.permissionviewer.R;
import com.emetophobe.permissionviewer.providers.PermissionContract.Permissions;
import com.emetophobe.permissionviewer.ui.adapters.PermissionListAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class AppDetailActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final String EXTRA_PACKAGE_NAME = "package_name";

	private static final String[] PROJECTION = {Permissions._ID, Permissions.APP_NAME, Permissions.PERMISSION_NAME};
	private static final String SORT_ORDER = Permissions.PERMISSION_NAME + " ASC";

	private PermissionListAdapter mAdapter;
	private String mPackageName;

	@InjectView(R.id.app_icon)
	protected ImageView mAppIcon;

	@InjectView(R.id.app_label)
	protected TextView mAppName;

	@InjectView(R.id.app_package)
	protected TextView mAppPackage;

	@InjectView(R.id.permission_count)
	protected TextView mPermissionCount;

	@InjectView(R.id.permission_list)
	protected ListView mPermissionList;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_detail);
		ButterKnife.inject(this);

		// Set up the toolbar.
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Get the package name from the intent extras.
		Bundle extras = getIntent().getExtras();
		mPackageName = extras != null ? extras.getString(EXTRA_PACKAGE_NAME) : null;
		if (mPackageName == null) {
			throw new IllegalArgumentException("Must pass a valid package name with EXTRA_PACKAGE_NAME.");
		}

		// Set the app icon and package name
		mAppIcon.setImageDrawable(getAppIcon());
		mAppPackage.setText(mPackageName);

		// Set up the permission adapter.
		mAdapter = new PermissionListAdapter(this);
		mPermissionList.setAdapter(mAdapter);

		// Load the permission list
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
		return new CursorLoader(this, Permissions.CONTENT_URI, PROJECTION, Permissions.PACKAGE_NAME + "=?", new String[]{mPackageName}, SORT_ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);

		// Set the app name and permission count.
		if (cursor != null && cursor.moveToFirst()) {
			mAppName.setText(cursor.getString(cursor.getColumnIndex(Permissions.APP_NAME)));

			int count = cursor.getString(cursor.getColumnIndex(Permissions.PERMISSION_NAME)) != null ? cursor.getCount() : 0;
			mPermissionCount.setText(String.format(getString(R.string.permission_count), count));
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	/**
	 * Load the application icon.
	 */
	private Drawable getAppIcon() {
		Drawable drawable;
		try {
			drawable = getPackageManager().getApplicationIcon(mPackageName);
		} catch (PackageManager.NameNotFoundException e) {
			drawable = null;
		}
		return drawable;
	}
}
