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

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;


/**
 * Displays the list of installed applications.
 */
public class AppListFragment extends AbstractListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Set up the app list adapter
		mAdapter = new AppListAdapter(getActivity());
		setListAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);
	}

	/**
	 * Load the application list.
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Get the show system apps preference
		String selection = !SettingsHelper.getShowSystemApps(getActivity()) ? Permissions.IS_SYSTEM + "= 0" : null;

		// Get the application sort order preference
		String sortOrder = SettingsHelper.getAppSortOrder(getActivity()) ? Permissions.APP_NAME + " ASC" : "count DESC";

		return new CursorLoader(getActivity(), Permissions.APPLICATIONS_URI, new String[]{Permissions._ID,
				Permissions.APP_NAME, Permissions.PACKAGE_NAME, Permissions.PERMISSION_NAME,
				"Count(permission) AS count"}, selection, null, sortOrder);
	}

	/**
	 * Start the application info activity when a list item is clicked.
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Cursor cursor = (Cursor) mAdapter.getItem(position);
		if (cursor != null) {
			String packageName = cursor.getString(cursor.getColumnIndex(Permissions.PACKAGE_NAME));
			Intent i = new Intent(getActivity(), AppDetailActivity.class);
			i.putExtra(AppDetailActivity.PACKAGE_NAME_EXTRA, packageName);
			startActivity(i);
		}
	}
}
