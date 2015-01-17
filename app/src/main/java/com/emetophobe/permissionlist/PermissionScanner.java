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

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.emetophobe.permissionlist.providers.PermissionContract.Permissions;

import java.util.List;


public class PermissionScanner extends Thread {
	public static final int MESSAGE_PROGRESS_INIT = 0;
	public static final int MESSAGE_PROGRESS_UPDATE = 1;
	public static final int MESSAGE_PROGRESS_COMPLETE = 2;

	private static final String TAG = "PermissionScanner";

	private Context mContext;
	private final Handler mHandler;

	public PermissionScanner(Context context, Handler handler) {
		super(TAG);
		mContext = context.getApplicationContext();
		mHandler = handler;
	}

	@Override
	public void run() {
		// Get the list of installed packages
		PackageManager pm = mContext.getPackageManager();
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

		// Initialize the progress dialog
		sendMessage(MESSAGE_PROGRESS_INIT, packages.size());

		String packageName, appName, permissionName;
		PackageInfo packageInfo;
		boolean system;
		int count = 0;

		// Iterate over each package in the list
		for (ApplicationInfo appInfo : packages) {
			// Get the package name and label
			packageName = appInfo.packageName;
			try {
				appName = pm.getApplicationLabel(appInfo).toString();
			} catch (Exception e) { // application not found
				appName = packageName;
			}

			// Get the system flag
			system = (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

			try {
				// Get the list of permissions
				packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
				if (packageInfo.requestedPermissions != null) {
					for (int i = 0; i < packageInfo.requestedPermissions.length; ++i) {
						if (packageInfo.requestedPermissions[i].startsWith("android.permission.")) {
							permissionName = packageInfo.requestedPermissions[i].substring("android.permission.".length());

							// Add a separate package entry for each permission that it has
							addPackage(appName, packageName, permissionName, system);
						}
					}
				} else {
					// Package contains no permission, just add a single package entry
					addPackage(appName, packageName, null, system);
				}
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}

			// Update the progress dialog
			sendMessage(MESSAGE_PROGRESS_UPDATE, ++count);
		}

		// Finish the progress dialog
		sendMessage(MESSAGE_PROGRESS_COMPLETE, 0);
	}

	// Add the package to the content provider
	private void addPackage(String appName, String packageName, String permission, boolean isSystemApp) {
		ContentValues values = new ContentValues();
		values.put(Permissions.APP_NAME, appName);
		values.put(Permissions.PACKAGE_NAME, packageName);
		values.put(Permissions.IS_SYSTEM, isSystemApp);

		if (permission != null) {
			values.put(Permissions.PERMISSION_NAME, permission);
		}

		mContext.getContentResolver().insert(Permissions.CONTENT_URI, values);
	}

	// Send a message to the main thread using the handler.
	private void sendMessage(int message, int arg1) {
		Message msg = mHandler.obtainMessage(message);
		msg.arg1 = arg1;
		msg.sendToTarget();
	}
}
