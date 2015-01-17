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

package com.emetophobe.permissionlist;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.emetophobe.permissionlist.providers.PermissionContract.Permissions;


public class UpdateReceiver extends BroadcastReceiver {
	private static final String TAG = "UpdateReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// Get the package name
		Uri uri = intent.getData();
		String packageName = uri != null ? uri.getSchemeSpecificPart() : null;
		if (TextUtils.isEmpty(packageName)) {
			Log.d(TAG, "Failed to get the package name from intent: " + intent);
			return;
		}

		PackageManager pm = context.getPackageManager();

		// Get the application info
		ApplicationInfo appInfo;
		try {
			appInfo = pm.getApplicationInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			Log.d(TAG, "Failed to find the application info for package: " + packageName);
			e.printStackTrace();
			return;
		}

		// Get the application name
		String appName;
		try {
			appName = pm.getApplicationLabel(appInfo).toString();
		} catch (Exception ex) { // application not found
			appName = packageName;
		}

		// Get the system flag
		int system = (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 ? 1 : 0;

		// Delete old permissions from the database
		context.getContentResolver().delete(Permissions.CONTENT_URI, Permissions.PACKAGE_NAME + "=?", new String[]{packageName});

		String permissionName;
		ContentValues values;
		try {
			// Get the permission list
			PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
			if (packageInfo.requestedPermissions != null && packageInfo.requestedPermissions.length > 0) {
				for (int i = 0; i < packageInfo.requestedPermissions.length; ++i) {
					if (packageInfo.requestedPermissions[i].startsWith("android.permission.")) {
						permissionName = packageInfo.requestedPermissions[i].substring("android.permission.".length());

						// Insert the permissions into the database
						values = new ContentValues();
						values.put(Permissions.APP_NAME, appName);
						values.put(Permissions.PACKAGE_NAME, packageName);
						values.put(Permissions.PERMISSION_NAME, permissionName);
						values.put(Permissions.IS_SYSTEM, system);
						context.getContentResolver().insert(Permissions.CONTENT_URI, values);
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}
}
