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

package com.emetophobe.permissionlist.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.emetophobe.permissionlist.adapters.AppListAdapter;
import com.emetophobe.permissionlist.SettingsHelper;
import com.emetophobe.permissionlist.activities.AppDetailActivity;
import com.emetophobe.permissionlist.providers.PermissionContract.Permissions;


public class AppListFragment extends AbstractListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Set up the adapter.
		mAdapter = new AppListAdapter(getActivity());
		setListAdapter(mAdapter);

		// Load the app list.
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		setLoading(true);

		// Set the show system selection clause
		String selection = !SettingsHelper.getShowSystemApps(getActivity())
				? Permissions.IS_SYSTEM + "= 0" : null;

		// Set the application sort order.
		String sortOrder = SettingsHelper.getAppSortOrder(getActivity())
				? Permissions.APP_NAME + " COLLATE NOCASE ASC" : "count DESC";

		// Create the loader.
		return new CursorLoader(getActivity(), Permissions.APPLICATIONS_URI, new String[]{Permissions._ID,
				Permissions.APP_NAME, Permissions.PACKAGE_NAME, Permissions.PERMISSION_NAME,
				"Count(permission) AS count"}, selection, null, sortOrder);
	}

	/** Start the app detail activity when a list item is clicked. */
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
