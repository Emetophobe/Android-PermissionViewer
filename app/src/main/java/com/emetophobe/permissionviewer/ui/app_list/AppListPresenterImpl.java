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

package com.emetophobe.permissionviewer.ui.app_list;

import com.emetophobe.permissionviewer.dagger.PerActivity;
import com.emetophobe.permissionviewer.model.AppDetail;
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
public class AppListPresenterImpl extends BasePresenter<AppListView> implements AppListPresenter {
	private PermissionHelper mPermissionHelper;
	private SettingsHelper mSettingsHelper;

	@Inject
	public AppListPresenterImpl(PermissionHelper permissionHelper, SettingsHelper settingsHelper) {
		mPermissionHelper = permissionHelper;
		mSettingsHelper = settingsHelper;
	}

	@Override
	public void loadAppList(boolean forceRefresh) {
		if (getView() != null) {
			getView().showLoading();
		}

		mPermissionHelper.getAppList(forceRefresh)
				.subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread())
				.map(new Func1<List<AppDetail>, List<AppDetail>>() {
					@Override
					public List<AppDetail> call(List<AppDetail> appList) {
						appList = filterAppList(appList);
						return sortAppList(appList);
					}
				})
				.subscribe(appList -> {
					if (getView() != null) {
						getView().showAppList(appList);
					}
				});
	}

	private List<AppDetail> filterAppList(List<AppDetail> appList) {
		// Just return the complete list if show system apps is enabled
		if (mSettingsHelper.getShowSystemApps()) {
			return appList;
		}

		// Remove all system apps from the list
		Iterator<AppDetail> i = appList.iterator();
		while (i.hasNext()) {
			AppDetail detail = i.next();
			if (detail.getSystemFlag() == 1) {
				i.remove();
			}
		}

		return appList;
	}

	private List<AppDetail> sortAppList(List<AppDetail> appList) {
		Collections.sort(appList, mSettingsHelper.getAppSortOrder() ? mSortByName : mSortByCount);
		return appList;
	}

	private Comparator<AppDetail> mSortByName = new Comparator<AppDetail>() {
		@Override
		public int compare(AppDetail left, AppDetail right) {
			return left.getAppLabel().toLowerCase().compareTo(right.getAppLabel().toLowerCase());
		}
	};

	private Comparator<AppDetail> mSortByCount = new Comparator<AppDetail>() {
		@Override
		public int compare(AppDetail left, AppDetail right) {
			return left.getPermissionList().size() - right.getPermissionList().size();
		}
	};
}
