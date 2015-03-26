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

package com.emetophobe.permissionviewer.ui.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.emetophobe.permissionviewer.ui.adapters.PermissionListAdapter;
import com.emetophobe.permissionviewer.utils.SettingsHelper;
import com.emetophobe.permissionviewer.ui.activities.PermissionDetailActivity;
import com.emetophobe.permissionviewer.providers.PermissionContract.Permissions;


/**
 * Display the list of permissions used by the installed applications.
 */
public class PermissionListFragment extends AbstractListFragment {
	private static final String APP_COUNT = "Count(app_name) AS count";
	private static final String SORT_BY_PERMISSION_NAME = Permissions.PERMISSION_NAME + " ASC";
	private static final String SORT_BY_COUNT = "count DESC";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Set up the adapter.
		mAdapter = new PermissionListAdapter(getActivity());
		setListAdapter(mAdapter);

		// Load the permission list.
		getLoaderManager().initLoader(0, null, this);
	}

	/**
	 * Create the permission list loader.
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		setLoading(true);

		// Create the selection clause.
		String selection = Permissions.PERMISSION_NAME + " IS NOT NULL";
		if (!SettingsHelper.getShowSystemApps(getActivity())) {
			selection += " AND " + Permissions.IS_SYSTEM + " = 0";
		}

		// Get the permission sort order preference and set the sort order.
		String sortOrder = SettingsHelper.getPermissionSortOrder(getActivity())
				? SORT_BY_PERMISSION_NAME : SORT_BY_COUNT;

		// Create the loader.
		return new CursorLoader(getActivity(), Permissions.PERMISSIONS_URI, new String[]{Permissions._ID,
				Permissions.PERMISSION_NAME, Permissions.APP_NAME, APP_COUNT}, selection, null,
				sortOrder);
	}

	/**
	 * Start the PermissionDetailActivity when a permission in the list is clicked.
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Cursor cursor = (Cursor) mAdapter.getItem(position);
		if (cursor != null) {
			String permissionName = cursor.getString(cursor.getColumnIndex(Permissions.PERMISSION_NAME));
			Intent intent = new Intent(getActivity(), PermissionDetailActivity.class);
			intent.putExtra(PermissionDetailActivity.EXTRA_PERMISSION_NAME, permissionName);
			startActivity(intent);
		}
	}
}
