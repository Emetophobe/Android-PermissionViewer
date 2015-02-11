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

package com.emetophobe.permissionviewer.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.emetophobe.permissionviewer.SettingsHelper;
import com.emetophobe.permissionviewer.activities.AppDetailActivity;
import com.emetophobe.permissionviewer.adapters.AppListAdapter;
import com.emetophobe.permissionviewer.providers.PermissionContract.Permissions;


/**
 * Display the list of installed applications.
 */
public class AppListFragment extends AbstractListFragment {
	private static final String PERMISSION_COUNT = "Count(permission) AS count";
	private static final String SORT_BY_APP_NAME = Permissions.APP_NAME + " COLLATE NOCASE ASC";
	private static final String SORT_BY_COUNT = "count DESC";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Set up the adapter.
		mAdapter = new AppListAdapter(getActivity());
		setListAdapter(mAdapter);

		// Load the app list.
		getLoaderManager().initLoader(0, null, this);
	}

	/**
	 * Create the app list loader.
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		setLoading(true);

		// Get the show system apps preference and set the selection clause.
		String selection = !SettingsHelper.getShowSystemApps(getActivity())
				? Permissions.IS_SYSTEM + "= 0" : null;

		// Get the application sort order preference and set the sort order.
		String sortOrder = SettingsHelper.getAppSortOrder(getActivity())
				? SORT_BY_APP_NAME : SORT_BY_COUNT;

		// Create the loader.
		return new CursorLoader(getActivity(), Permissions.APPLICATIONS_URI, new String[]{Permissions._ID,
				Permissions.APP_NAME, Permissions.PACKAGE_NAME, Permissions.PERMISSION_NAME,
				PERMISSION_COUNT}, selection, null, sortOrder);
	}

	/**
	 * Start the AppDetailActivity when an application in the list is clicked.
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Cursor cursor = (Cursor) mAdapter.getItem(position);
		if (cursor != null) {
			String packageName = cursor.getString(cursor.getColumnIndex(Permissions.PACKAGE_NAME));
			Intent i = new Intent(getActivity(), AppDetailActivity.class);
			i.putExtra(AppDetailActivity.EXTRA_PACKAGE_NAME, packageName);
			startActivity(i);
		}
	}
}
