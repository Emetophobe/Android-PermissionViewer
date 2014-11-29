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

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.emetophobe.permissionlist.providers.PermissionContract.Permissions;

import java.util.List;


public class PermissionScanner extends Thread {
	private static final String TAG = "PermissionScanner";

	private Context mContext;

	public PermissionScanner(Context context) {
		super("Permission Scanner");
		mContext = context.getApplicationContext();
	}

	@Override
	public void run() {
		// Get the list of installed packages
		PackageManager pm = mContext.getPackageManager();
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

		ContentValues values;
		String packageName, appName, permissionName;
		PackageInfo packageInfo;
		boolean system;

		// Iterate over each package in the list
		for (ApplicationInfo appInfo : packages) {
			// Get the package name and label
			packageName = appInfo.packageName;
			try {
				appName = pm.getApplicationLabel(appInfo).toString();
			} catch (Exception ex) { // application not found
				appName = packageName;
			}

			// Get the system flag
			system = (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

			// Get the list of permissions
			try {
				packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
				if (packageInfo.requestedPermissions != null && packageInfo.requestedPermissions.length > 0) {
					for (int i = 0; i < packageInfo.requestedPermissions.length; ++i) {
						if (packageInfo.requestedPermissions[i].startsWith("android.permission.")) {
							permissionName = packageInfo.requestedPermissions[i].substring("android.permission.".length());

							// Add the permission to the provider
							values = new ContentValues();
							values.put(Permissions.APP_NAME, appName);
							values.put(Permissions.PACKAGE_NAME, packageName);
							values.put(Permissions.PERMISSION_NAME, permissionName);
							values.put(Permissions.IS_SYSTEM, system);
							mContext.getContentResolver().insert(Permissions.CONTENT_URI, values);
						}
					}
				}
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
		}
	}
}
