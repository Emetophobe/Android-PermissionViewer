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

package com.emetophobe.permissionviewer.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emetophobe.permissionviewer.R;
import com.emetophobe.permissionviewer.providers.PermissionContract;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class PermissionListAdapter extends CursorAdapter {
	private static final String APP_COUNT = "count";

	public PermissionListAdapter(Context context) {
		super(context, null, 0);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.adapter_simple_list_item, parent, false);
		view.setTag(new ViewHolder(view));
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();

		// Set the permission name (with optional app count)
		int countIndex = cursor.getColumnIndex(APP_COUNT);
		if (countIndex != -1) {
			holder.permission.setText(cursor.getString(cursor.getColumnIndex(PermissionContract.Permissions.PERMISSION_NAME))
					+ " (" + cursor.getString(countIndex) + ")");
		} else {
			holder.permission.setText(cursor.getString(cursor.getColumnIndex(PermissionContract.Permissions.PERMISSION_NAME)));
		}
	}

	protected static class ViewHolder {
		@InjectView(android.R.id.text1)
		public TextView permission;

		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
}
