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

package com.emetophobe.permissionviewer.adapters;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emetophobe.permissionviewer.R;
import com.emetophobe.permissionviewer.providers.PermissionContract.Permissions;


public class AppListAdapter extends CursorAdapter {
	private static final String PERMISSION_COUNT = "count";

	private PackageManager mPackageManager;

	public AppListAdapter(Context context) {
		super(context, null, 0);
		mPackageManager = context.getPackageManager();
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.adapter_applist_item, parent, false);
		ViewHolder holder = new ViewHolder();
		holder.icon = (ImageView) view.findViewById(R.id.app_icon);
		holder.name = (TextView) view.findViewById(R.id.app_name);
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();

		// Set the app name (with optional permission count)
		int countIndex = cursor.getColumnIndex(PERMISSION_COUNT);
		if (countIndex != -1) {
			holder.name.setText(cursor.getString(cursor.getColumnIndex(Permissions.APP_NAME)) + " (" + cursor.getString(countIndex) + ")");
		} else {
			holder.name.setText(cursor.getString(cursor.getColumnIndex(Permissions.APP_NAME)));
		}

		// Set the app icon (TODO: Should we load or cache the icons in a separate thread?)
		Drawable drawable;
		try {
			drawable = mPackageManager.getApplicationIcon(cursor.getString(cursor.getColumnIndex(Permissions.PACKAGE_NAME)));
		} catch (PackageManager.NameNotFoundException e) {
			drawable = null;
		}
		holder.icon.setImageDrawable(drawable);
	}

	private static class ViewHolder {
		public ImageView icon;
		public TextView name;
	}
}
