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
import com.emetophobe.permissionviewer.ui.base.mvp.BasePresenter;
import com.emetophobe.permissionviewer.utils.PermissionHelper;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


@PerActivity
public class AppListPresenterImpl extends BasePresenter<AppListView> implements AppListPresenter {
	private PermissionHelper mPermissionHelper;

	@Inject
	public AppListPresenterImpl(PermissionHelper permissionHelper) {
		mPermissionHelper = permissionHelper;
	}

	@Override
	public void loadAppList(boolean forceRefresh) {
		if (getView() != null) {
			getView().showLoading();
		}

		mPermissionHelper.getAppList(forceRefresh)
				.subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(appList -> {
					if (getView() != null) {
						getView().showAppList(appList);
					}
				});
	}
}
