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

import com.emetophobe.permissionlist.providers.PermissionContract.Permissions;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;


public class PermissionDetailActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final String PERMISSION_NAME_EXTRA = "permission_name";

	private static final int APPLICATION_LIST = 0;
	private static final int PERMISSION_DATA = 1;

	private String mPermissionName;

	private TextView mDescriptionView;
	private TextView mAppCountView;
	private ListView mAppList;

	private SimpleCursorAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_permission_info);

		// Setup the toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mDescriptionView = (TextView) findViewById(R.id.permission_description);
		mAppCountView = (TextView) findViewById(R.id.permission_count);
		mAppList = (ListView) findViewById(R.id.app_list);

		// Get the permission name from the intent extras
		Bundle extras = getIntent().getExtras();
		mPermissionName = extras != null ? extras.getString(PERMISSION_NAME_EXTRA) : null;
		if (mPermissionName == null) {
			throw new IllegalArgumentException("Missing intent extra PERMISSION_NAME_EXTRA");
		}

		// Create the permission list adapter
		mAdapter = new SimpleCursorAdapter(this, R.layout.adapter_simple_list_item, null,
				new String[]{Permissions.APP_NAME}, new int[]{android.R.id.text1}, 0);
		mAppList.setAdapter(mAdapter);

		// Load the permission data and the application list
		getSupportLoaderManager().initLoader(PERMISSION_DATA, null, this);
		getSupportLoaderManager().initLoader(APPLICATION_LIST, null, this);
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
		if (id == APPLICATION_LIST) {
			// Get the list of applications that use the specified permission
			return new CursorLoader(this, Permissions.CONTENT_URI,
					new String[]{Permissions._ID, Permissions.APP_NAME}, Permissions.PERMISSION_NAME + "=?",
					new String[]{mPermissionName}, Permissions.APP_NAME + " ASC");
		} else {
			// Get the permission name and application count
			return new CursorLoader(this, Permissions.CONTENT_URI, new String[]{Permissions._ID,
					Permissions.APP_NAME, Permissions.PERMISSION_NAME, "Count(app_name) as count"},
					Permissions.PERMISSION_NAME + "=?", new String[]{mPermissionName}, Permissions.APP_NAME + " ASC");
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (loader.getId() == APPLICATION_LIST) {
			mAdapter.swapCursor(cursor);
		} else if (loader.getId() == PERMISSION_DATA && cursor != null && cursor.moveToFirst()) {
			// Set the toolbar title to the permission name
			setTitle(mPermissionName);

			// Set the description and application count
			mDescriptionView.setText(getDescription());
			String count = cursor.getString(cursor.getColumnIndex("count"));
			mAppCountView.setText(String.format(getString(R.string.application_count), count));
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (loader.getId() == APPLICATION_LIST) {
			mAdapter.swapCursor(null);
		}
	}

	/**
	 * Get the permission description.
	 */
	private String getDescription() {
		int resId = getResources().getIdentifier("permission_" + mPermissionName, "string", getPackageName());
		return resId != 0 ? getString(resId) : getString(R.string.permission_unknown);
	}
}
