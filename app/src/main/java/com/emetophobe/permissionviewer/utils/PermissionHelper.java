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

package com.emetophobe.permissionviewer.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.Log;

import com.emetophobe.permissionviewer.model.AppDetail;
import com.emetophobe.permissionviewer.model.PermissionDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;


public class PermissionHelper {
	private static final String TAG = "PermissionHelper";
	private static final String ANDROID_PERMISSION = "android.permission.";

	private PackageManager mPackageManager;
	private List<AppDetail> mAppList;
	private List<PermissionDetail> mPermissionList;

	public PermissionHelper(Context context) {
		mPackageManager = context.getPackageManager();
		mAppList = new ArrayList<>();
		mPermissionList = new ArrayList<>();
	}

	public Observable<List<AppDetail>> getAppList(boolean forceRefresh) {
		if (mAppList.isEmpty() || forceRefresh) {
			build();
		}
		return Observable.just(mAppList);
	}

	public Observable<List<PermissionDetail>> getPermissionList(boolean forceRefresh) {
		if (mPermissionList.isEmpty() || forceRefresh) {
			build();
		}

		return Observable.just(mPermissionList);
	}

	/**
	 * Create the app and permission lists.
	 */
	private void build() {
		buildAppList();
		buildPermissionList();
	}

	/**
	 * Create the app list.
	 */
	private void buildAppList() {
		// Clear old data
		mAppList.clear();

		// Get the list of installed packages
		List<ApplicationInfo> packages = mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA);

		// Create the app list
		int count = 0;
		for (ApplicationInfo appInfo : packages) {
			createAppDetail(appInfo.packageName, appInfo);
		}

	}

	/**
	 * Create an AppDetail object and add it the app list.
	 *
	 * @param packageName The package name.
	 * @param appInfo     The application info.
	 */
	private void createAppDetail(String packageName, ApplicationInfo appInfo) {
		// Get the application label
		String appLabel;
		try {
			appLabel = mPackageManager.getApplicationLabel(appInfo).toString();
		} catch (Resources.NotFoundException e) {
			// application label not found, just use the package name.
			appLabel = packageName;
		}

		// Get the system app flag
		int systemFlag = (appInfo.flags & ApplicationInfo.FLAG_SYSTEM);

		// Get the package info
		PackageInfo packageInfo;
		try {
			packageInfo = mPackageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
		} catch (PackageManager.NameNotFoundException e) {
			Log.d(TAG, "Failed to get the package info for " + packageName);
			e.printStackTrace();
			return;
		}

		// Create the AppInfo entry
		AppDetail app = new AppDetail(packageName, appLabel, systemFlag);
		mAppList.add(app);

		// Get the list of permissions
		if (packageInfo.requestedPermissions != null && packageInfo.requestedPermissions.length > 0) {
			for (String permissionName : packageInfo.requestedPermissions) {
				if (permissionName.startsWith(ANDROID_PERMISSION)) {
					permissionName = permissionName.substring(ANDROID_PERMISSION.length());

					// Add the permission to the AppDetail
					app.getPermissionList().add(permissionName);
				}
			}
		}
	}


	/**
	 * Create the permission list from the app list.
	 */
	private void buildPermissionList() {
		// Clear old data
		mPermissionList.clear();

		// Build a temporary permission map
		HashMap<String, List<AppDetail>> map = new HashMap<>();
		for (AppDetail appDetail : mAppList) {
			for (String permission : appDetail.getPermissionList()) {
				List<AppDetail> apps = map.get(permission);
				if (apps == null) {
					apps = new ArrayList<>();
					map.put(permission, apps);
				}
				apps.add(appDetail);
			}
		}

		// Build the permission list from the map
		for (String permission : map.keySet()) {
			mPermissionList.add(new PermissionDetail(permission, map.get(permission)));
		}
	}
}
