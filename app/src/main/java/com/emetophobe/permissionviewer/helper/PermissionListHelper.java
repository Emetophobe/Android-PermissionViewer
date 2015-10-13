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

import com.emetophobe.permissionviewer.model.AppDetail;
import com.emetophobe.permissionviewer.model.PermissionDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;


public class PermissionListHelper {
	private AppListHelper mAppListHelper;
	private SettingsHelper mSettingsHelper;

	@Inject
	public PermissionListHelper(AppListHelper appListHelper, SettingsHelper settingsHelper) {
		mAppListHelper = appListHelper;
		mSettingsHelper = settingsHelper;
	}

	/**
	 * Get the permissions list observable.
	 *
	 * @return The observable.
	 */
	public Observable<List<PermissionDetail>> getPermissionList() {
		// Use the app list observable to create the permission list observable
		return mAppListHelper.getAppList()
				.map(new Func1<List<AppDetail>, List<PermissionDetail>>() {
					@Override
					public List<PermissionDetail> call(List<AppDetail> appList) {
						return createPermissionList(appList);
					}
				});
	}

	/**
	 * Create the permission list.
	 *
	 * @param appList The application list.
	 * @return The permission list.
	 */
	private List<PermissionDetail> createPermissionList(List<AppDetail> appList) {
		List<PermissionDetail> permissionList = new ArrayList<>();

		// Build a temporary permission map
		HashMap<String, List<AppDetail>> map = new HashMap<>();
		for (AppDetail appDetail : appList) {
			for (String permission : appDetail.getPermissionList()) {
				List<AppDetail> apps = map.get(permission);
				if (apps == null) {
					apps = new ArrayList<>();
					map.put(permission, apps);
				}
				apps.add(appDetail);
			}
		}

		// Create the permission list from the map
		for (String permission : map.keySet()) {
			permissionList.add(new PermissionDetail(permission, map.get(permission)));
		}

		// Sort the permission list based on the user preference
		Collections.sort(permissionList, mSettingsHelper.getPermissionSortOrder() ? mSortByName : mSortByCount);

		return permissionList;
	}


	/**
	 * Compare permissions by name in alphabetical order.
	 */
	private Comparator<PermissionDetail> mSortByName = new Comparator<PermissionDetail>() {
		@Override
		public int compare(PermissionDetail left, PermissionDetail right) {
			return left.getName().toLowerCase().compareTo(right.getName().toLowerCase());
		}
	};

	/**
	 * Compare permissions by app count in descending order.
	 */
	private Comparator<PermissionDetail> mSortByCount = new Comparator<PermissionDetail>() {
		@Override
		public int compare(PermissionDetail left, PermissionDetail right) {
			return right.getAppList().size() - left.getAppList().size();
		}
	};
}
