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

package com.emetophobe.permissionviewer.helper;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.Log;

import com.emetophobe.permissionviewer.model.AppDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;


public class AppListHelper {
	private static final String TAG = "AppListHelper";
	private static final String ANDROID_PERMISSION = "android.permission.";

	private PackageManager mPackageManager;
	private SettingsHelper mSettingsHelper;

	@Inject
	public AppListHelper(Context context, SettingsHelper settingsHelper) {
		mPackageManager = context.getPackageManager();
		mSettingsHelper = settingsHelper;
	}

	/**
	 * Get the application list observable.
	 *
	 * @return The observable.
	 */
	public Observable<List<AppDetail>> getAppList() {
		return Observable.create(new Observable.OnSubscribe<List<AppDetail>>() {
			@Override
			public void call(Subscriber<? super List<AppDetail>> subscriber) {
				subscriber.onNext(createAppList());
				subscriber.onCompleted();
			}
		}).cache();    // cache the results
	}

	/**
	 * Create the application list.
	 *
	 * @return The app list.
	 */
	private List<AppDetail> createAppList() {
		List<AppDetail> appList = new ArrayList<>();

		// Get the list of installed packages and create the AppDetail list
		List<ApplicationInfo> packages = mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA);
		for (ApplicationInfo appInfo : packages) {
			AppDetail appDetail = getAppDetail(appInfo.packageName, appInfo);
			if (appDetail != null) {
				appList.add(appDetail);
			}
		}

		// Sort the app list based on the user preference
		Collections.sort(appList, mSettingsHelper.getAppSortOrder() ? mSortByName : mSortByCount);

		return appList;
	}

	/**
	 * Create and return the AppDetail.
	 *
	 * @param packageName The package name.
	 * @param appInfo     The application info.
	 * @return The AppDetail, or null if there was an error.
	 */
	private AppDetail getAppDetail(String packageName, ApplicationInfo appInfo) {
		// Get the system app flag
		int systemFlag = (appInfo.flags & ApplicationInfo.FLAG_SYSTEM);
		if (!mSettingsHelper.getShowSystemApps() && systemFlag == 1) {
			return null;    // ignore system apps if the setting is disabled
		}

		// Get the app enabled flag
		if (!mSettingsHelper.getShowDisabledApps() && !appInfo.enabled) {
			return null;    // ignore disabled apps if the setting is disabled
		}

		// Get the application label
		String appLabel;
		try {
			appLabel = mPackageManager.getApplicationLabel(appInfo).toString();
		} catch (Resources.NotFoundException e) {
			// application label not found, just use the package name.
			appLabel = packageName;
		}

		// Get the permission list
		List<String> permissions = getPermissions(packageName);
		if (permissions == null) {
			Log.d(TAG, "Failed to retrieve the permission list for " + packageName);
			return null;
		}
		// Create the AppDetail
		return new AppDetail(packageName, appLabel, systemFlag, permissions);
	}

	/**
	 * Get the list of permissions for the given package.
	 *
	 * @param packageName The package name.
	 * @return The list of strings, or null if there was an error.
	 */
	private List<String> getPermissions(String packageName) {
		List<String> permissionList = new ArrayList<>();

		// Get the package info
		PackageInfo packageInfo;
		try {
			packageInfo = mPackageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
		} catch (PackageManager.NameNotFoundException e) {
			return null;
		}

		// Get the list of permissions and add them to the permission list
		if (packageInfo.requestedPermissions != null && packageInfo.requestedPermissions.length > 0) {
			for (String permissionName : packageInfo.requestedPermissions) {
				if (permissionName.startsWith(ANDROID_PERMISSION)) {
					permissionName = permissionName.substring(ANDROID_PERMISSION.length());
					permissionList.add(permissionName);
				}
			}
		}

		return permissionList;
	}

	/**
	 * Compare apps by name in alphabetical order.
	 */
	private Comparator<AppDetail> mSortByName = new Comparator<AppDetail>() {
		@Override
		public int compare(AppDetail left, AppDetail right) {
			return left.getAppLabel().toLowerCase().compareTo(right.getAppLabel().toLowerCase());
		}
	};

	/**
	 * Compare apps by permission count in descending order.
	 */
	private Comparator<AppDetail> mSortByCount = new Comparator<AppDetail>() {
		@Override
		public int compare(AppDetail left, AppDetail right) {
			return right.getPermissionList().size() - left.getPermissionList().size();
		}
	};
}
