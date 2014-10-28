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

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emetophobe.permissionlist.providers.PermissionContract;


public class PermissionListAdapter extends CursorAdapter {

	public PermissionListAdapter(Context context) {
		super(context, null, 0);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.adapter_permissionlist_item, parent, false);
		ViewHolder holder = new ViewHolder();
		holder.permission = (TextView) view.findViewById(R.id.permission);
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();

		String count = cursor.getString(cursor.getColumnIndex("count"));
		holder.permission.setText(cursor.getString(cursor.getColumnIndex(PermissionContract.Permissions.PERMISSION_NAME)) + " (" + count + ")");
	}

	private static class ViewHolder {
		public TextView permission;
	}
}
