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

package com.emetophobe.permissionviewer.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.emetophobe.permissionviewer.providers.PermissionContract;

import java.util.List;


public class UpdateDatabaseService extends IntentService {
	private static final String TAG = "UpdateDatabaseService";
	private static final String ANDROID_PERMISSION = "android.permission.";

	public static final String INTENT_ACTION_INIT_DATABASE = "intent_action_init_database";
	public static final String INTENT_ACTION_UPDATE_PACKAGE = "intent_action_update_package";
	public static final String INTENT_EXTRA_PACKAGE_NAME = "intent_extra_package_name";

	public static final int MESSAGE_PROGRESS_INIT = 0;
	public static final int MESSAGE_PROGRESS_UPDATE = 1;
	public static final int MESSAGE_PROGRESS_COMPLETE = 2;

	public static final String PERMISSION_INTENT = "permission_intent";
	public static final String PROGRESS_MESSAGE = "progress_message";
	public static final String PROGRESS_VALUE = "progress_value";

	public UpdateDatabaseService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent.getAction().equals(INTENT_ACTION_INIT_DATABASE)) {
			initDatabase();
		} else if (intent.getAction().equals(INTENT_ACTION_UPDATE_PACKAGE)) {
			updatePackage(intent);
		} else {
			throw new IllegalArgumentException("Unknown intent action: " + intent.getAction());
		}
	}

	private void initDatabase() {
		// Delete any old database records
		getContentResolver().delete(PermissionContract.Permissions.CONTENT_URI, null, null);

		// Get the list of installed packages
		List<ApplicationInfo> packages = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

		broadcastMessage(MESSAGE_PROGRESS_INIT, packages.size());

		// Add each package in the list to the database
		int count = 0;
		for (ApplicationInfo appInfo : packages) {
			insert(appInfo.packageName, appInfo);
			broadcastMessage(MESSAGE_PROGRESS_UPDATE, ++count); // update the progress dialog
		}

		broadcastMessage(MESSAGE_PROGRESS_COMPLETE, 0);
	}

	private void updatePackage(Intent intent) {
		// Get the package name from the intent extras
		String packageName = intent.getStringExtra(INTENT_EXTRA_PACKAGE_NAME);
		if (TextUtils.isEmpty(packageName)) {
			throw new IllegalArgumentException("Must pass the package name with INTENT_EXTRA_PACKAGE_NAME");
		}

		// Get the application info
		ApplicationInfo appInfo;
		try {
			appInfo = getPackageManager().getApplicationInfo(packageName, 0);
		} catch (PackageManager.NameNotFoundException e) {
			Log.d(TAG, "Failed to find the application info for package: " + packageName);
			e.printStackTrace();
			return;
		}

		// Delete any old package entries
		getContentResolver().delete(PermissionContract.Permissions.CONTENT_URI,
				PermissionContract.Permissions.PACKAGE_NAME + "=?", new String[]{packageName});

		// Insert the new package entry
		insert(packageName, appInfo);
	}


	/**
	 * Insert a package into the database.
	 *
	 * @param packageName The package name.
	 * @param appInfo     The application info.
	 */
	public void insert(String packageName, ApplicationInfo appInfo) {
		// Get the application label/name
		String appName;
		try {
			appName = getPackageManager().getApplicationLabel(appInfo).toString();
		} catch (Resources.NotFoundException e) {
			// application label not found, just use the package name.
			appName = packageName;
		}

		// Get the system app flag
		boolean isSystemApp = (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

		try {
			// Get the permission list
			PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);

			// Just add an package entry with no permissions if the list is empty
			if (packageInfo.requestedPermissions == null || packageInfo.requestedPermissions.length == 0) {
				insert(packageName, appName, null, isSystemApp);
				return;
			}

			// Create a separate package entry for each permission in the list
			String permissionName;
			for (int i = 0; i < packageInfo.requestedPermissions.length; ++i) {
				if (packageInfo.requestedPermissions[i].startsWith(ANDROID_PERMISSION)) {
					permissionName = packageInfo.requestedPermissions[i].substring(ANDROID_PERMISSION.length());
					insert(packageName, appName, permissionName, isSystemApp);
				}
			}
		} catch (PackageManager.NameNotFoundException e) {
			Log.e(TAG, e.toString());
		}
	}

	/**
	 * Insert a package entry into the database.
	 *
	 * @param packageName    The package name.
	 * @param appName        The application name.
	 * @param permissionName The permission name (optional, can be null).
	 * @param isSystemApp    Whether the package is a system app or not.
	 */
	private void insert(String packageName, String appName, String permissionName, boolean isSystemApp) {
		ContentValues values = new ContentValues();
		values.put(PermissionContract.Permissions.APP_NAME, appName);
		values.put(PermissionContract.Permissions.PACKAGE_NAME, packageName);
		values.put(PermissionContract.Permissions.IS_SYSTEM, isSystemApp);

		if (permissionName != null) {
			values.put(PermissionContract.Permissions.PERMISSION_NAME, permissionName);
		}

		getContentResolver().insert(PermissionContract.Permissions.CONTENT_URI, values);
	}

	/**
	 * Send a progress update message to the main/ui thread.
	 *
	 * @param message  The progress type.
	 * @param progress The progress value.
	 */
	private void broadcastMessage(int message, int progress) {
		Intent intent = new Intent(PERMISSION_INTENT);
		intent.putExtra(PROGRESS_MESSAGE, message);
		intent.putExtra(PROGRESS_VALUE, progress);
		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
	}
}
