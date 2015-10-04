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

package com.emetophobe.permissionviewer.presenter;

import com.emetophobe.permissionviewer.dagger.PerActivity;
import com.emetophobe.permissionviewer.view.AppListView;
import com.emetophobe.permissionviewer.utils.AppListHelper;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


@PerActivity
public class AppListPresenterImpl extends BasePresenter<AppListView> implements AppListPresenter {
	private AppListHelper mAppListHelper;

	@Inject
	public AppListPresenterImpl(AppListHelper appListHelper) {
		mAppListHelper = appListHelper;
	}

	@Override
	public void loadAppList(boolean forceRefresh) {
		if (getView() != null) {
			getView().showLoading();
		}

		mAppListHelper.getAppList(forceRefresh)
				.subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(appList -> {
					if (getView() != null) {
						getView().showAppList(appList);
					}
				});
	}
}
