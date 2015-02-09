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

package com.emetophobe.permissionlist.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.MenuItem;

import com.emetophobe.permissionlist.R;
import com.emetophobe.permissionlist.SettingsHelper;
import com.emetophobe.permissionlist.adapters.AppListAdapter;
import com.emetophobe.permissionlist.providers.PermissionContract.Permissions;


public class PermissionDetailActivity extends AbstractDetailActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final String EXTRA_PERMISSION_NAME = "extra_permission_name";

	private AppListAdapter mAdapter;
	private String mPermissionName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get the permission name from the intent extras.
		Bundle extras = getIntent().getExtras();
		mPermissionName = extras != null ? extras.getString(EXTRA_PERMISSION_NAME) : null;
		if (mPermissionName == null) {
			throw new IllegalArgumentException("Must pass a valid permission name with EXTRA_PERMISSION_NAME.");
		}

		// Set the title and description.
		setTitle(mPermissionName);
		setDescription(getDescription());

		// Set up the adapter.
		mAdapter = new AppListAdapter(this);
		setListAdapter(mAdapter);

		// Load the list of applications that match the permission name
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
		if (!SettingsHelper.getShowSystemApps(this)) {
			selection += " AND " + Permissions.IS_SYSTEM + "=0";
		}

		final String[] projection = {Permissions._ID, Permissions.APP_NAME,	Permissions.PACKAGE_NAME};
		final String[] selectionArgs = {mPermissionName};
		final String sortOrder = Permissions.APP_NAME + " ASC";

		return new CursorLoader(this, Permissions.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);

		// Set the application count
		setCount(String.format(getString(R.string.application_count), cursor.getCount()));
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
