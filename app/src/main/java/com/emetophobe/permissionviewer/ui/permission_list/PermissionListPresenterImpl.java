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

package com.emetophobe.permissionviewer.ui.permission_list;

import com.emetophobe.permissionviewer.dagger.PerActivity;
import com.emetophobe.permissionviewer.model.AppDetail;
import com.emetophobe.permissionviewer.model.PermissionDetail;
import com.emetophobe.permissionviewer.ui.base.mvp.BasePresenter;
import com.emetophobe.permissionviewer.utils.PermissionHelper;
import com.emetophobe.permissionviewer.utils.SettingsHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


@PerActivity
public class PermissionListPresenterImpl extends BasePresenter<PermissionListView> implements PermissionListPresenter {
	private PermissionHelper mPermissionHelper;
	private SettingsHelper mSettingsHelper;

	@Inject
	public PermissionListPresenterImpl(PermissionHelper permissionHelper, SettingsHelper settingsHelper) {
		mPermissionHelper = permissionHelper;
		mSettingsHelper = settingsHelper;
	}

	@Override
	public void loadPermissionList(boolean forceRefresh) {
		if (getView() != null) {
			getView().showLoading();
		}

		mPermissionHelper.getPermissionList(forceRefresh)
				.subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread())
				.map(new Func1<List<PermissionDetail>, List<PermissionDetail>>() {
					@Override
					public List<PermissionDetail> call(List<PermissionDetail> permissionList) {
						permissionList = filterPermissionList(permissionList);
						return sortPermissionList(permissionList);
					}
				})
				.subscribe(permissionList -> {
					if (getView() != null) {
						getView().showPermissionList(permissionList);
					}
				});
	}

	private List<PermissionDetail> filterPermissionList(List<PermissionDetail> permissionList) {
		// Just return the complete list if show system apps is enabled
		if (mSettingsHelper.getShowSystemApps()) {
			return permissionList;
		}

		// Remove all system apps from the permission's app list
		Iterator<PermissionDetail> pi = permissionList.iterator();
		while(pi.hasNext()) {
			PermissionDetail detail = pi.next();

			// Remove system apps from the app list
			Iterator<AppDetail> ai = detail.getAppList().iterator();
			while (ai.hasNext()) {
				AppDetail appDetail = ai.next();
				if (appDetail.getSystemFlag() == 1) {
					ai.remove();
				}
			}

			// Remove empty permissions from the list
			if (detail.getAppList().isEmpty()) {
				pi.remove();
			}
		}

		for(PermissionDetail detail : permissionList) {
			Iterator<AppDetail> i = detail.getAppList().iterator();
			while (i.hasNext()) {
				AppDetail appDetail = i.next();
				if (appDetail.getSystemFlag() == 1) {
					i.remove();
				}
			}
		}

		return permissionList;
	}

	private List<PermissionDetail> sortPermissionList(List<PermissionDetail> permissionList) {
		Collections.sort(permissionList, mSettingsHelper.getPermissionSortOrder() ? mSortByName : mSortByCount);
		return permissionList;
	}

	private Comparator<PermissionDetail> mSortByName = new Comparator<PermissionDetail>() {
		@Override
		public int compare(PermissionDetail left, PermissionDetail right) {
			return left.getName().toLowerCase().compareTo(right.getName().toLowerCase());
		}
	};

	private Comparator<PermissionDetail> mSortByCount = new Comparator<PermissionDetail>() {
		@Override
		public int compare(PermissionDetail left, PermissionDetail right) {
			return left.getAppList().size() - right.getAppList().size();
		}
	};
}
