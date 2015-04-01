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
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.emetophobe.permissionviewer.R;
import com.emetophobe.permissionviewer.utils.SettingsUtils;
import com.emetophobe.permissionviewer.ui.adapters.AppListAdapter;
import com.emetophobe.permissionviewer.providers.PermissionContract.Permissions;


public class PermissionDetailActivity extends AbstractDetailActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final String EXTRA_PERMISSION_NAME = "extra_permission_name";

	private static final String[] PROJECTION = {Permissions._ID, Permissions.APP_NAME, Permissions.PACKAGE_NAME};
	private static final String SORT_ORDER = Permissions.APP_NAME + " ASC";

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
			setCount(String.format(getString(R.string.application_count), cursor.getCount()));
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
