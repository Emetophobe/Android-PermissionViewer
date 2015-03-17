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


package com.emetophobe.permissionviewer.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.emetophobe.permissionviewer.R;
import com.emetophobe.permissionviewer.adapters.PermissionListAdapter;
import com.emetophobe.permissionviewer.providers.PermissionContract.Permissions;


public class AppDetailActivity extends AbstractDetailActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final String EXTRA_PACKAGE_NAME = "extra_package_name";

	private static final String[] PROJECTION = {Permissions._ID, Permissions.APP_NAME,
			Permissions.PACKAGE_NAME, Permissions.PERMISSION_NAME};
	private static final String SELECTION = Permissions.PACKAGE_NAME + "=?";
	private static final String SORT_ORDER = Permissions.PERMISSION_NAME + " ASC";

	private PermissionListAdapter mAdapter;
	private String mPackageName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get the package name from the intent extras.
		Bundle extras = getIntent().getExtras();
		mPackageName = extras != null ? extras.getString(EXTRA_PACKAGE_NAME) : null;
		if (mPackageName == null) {
			throw new IllegalArgumentException("Must pass a valid package name with EXTRA_PACKAGE_NAME.");
		}

		// Set up the adapter.
		mAdapter = new PermissionListAdapter(this);
		setListAdapter(mAdapter);

		// Load the permission list
		getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, Permissions.CONTENT_URI, PROJECTION, SELECTION, new String[]{mPackageName}, SORT_ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);

		// Set the app name, package name, and permission count.
		if (cursor != null && cursor.moveToFirst()) {
			setTitle(cursor.getString(cursor.getColumnIndex(Permissions.APP_NAME)));
			setDescription(cursor.getString(cursor.getColumnIndex(Permissions.PACKAGE_NAME)));
			setCount(String.format(getString(R.string.permission_count), cursor.getCount()));
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}
}
