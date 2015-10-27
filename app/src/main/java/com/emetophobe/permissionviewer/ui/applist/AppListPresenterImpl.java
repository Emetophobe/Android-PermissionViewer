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

package com.emetophobe.permissionviewer.ui.applist;

import com.emetophobe.permissionviewer.injection.FragmentScope;
import com.emetophobe.permissionviewer.helper.AppListHelper;
import com.emetophobe.permissionviewer.model.AppDetail;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


@FragmentScope
public class AppListPresenterImpl extends MvpBasePresenter<AppListView> implements AppListPresenter {
	private Subscriber<List<AppDetail>> subscriber;
	private AppListHelper appListHelper;

	@Inject
	public AppListPresenterImpl(AppListHelper appListHelper) {
		this.appListHelper = appListHelper;
	}

	@Override
	public void loadAppList(boolean pullToRefresh) {
		// Show the loading view
		if (getView() != null) {
			getView().showLoading(pullToRefresh);
		}

		unsubscribe();

		// Create the subscriber
		subscriber = new Subscriber<List<AppDetail>>() {
			@Override
			public void onCompleted() {
				// Show the content view
				if (getView() != null) {
					getView().showContent();
				}
			}

			@Override
			public void onError(Throwable e) {
				// Show the error view
				if (getView() != null) {
					getView().showError(e, pullToRefresh);
				}
			}

			@Override
			public void onNext(List<AppDetail> data) {
				// Set the adapter data
				if (getView() != null) {
					getView().setData(data);
				}
			}
		};

		// Get the observable and subscribe to it
		appListHelper.getAppList()
				.subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(subscriber);
	}

	private void unsubscribe() {
		if (subscriber != null && !subscriber.isUnsubscribed()) {
			subscriber.unsubscribe();
		}
		subscriber = null;
	}


	@Override
	public void detachView(boolean retainInstance) {
		super.detachView(retainInstance);
		unsubscribe();
	}
}
