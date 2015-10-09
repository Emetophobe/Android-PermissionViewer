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
import com.emetophobe.permissionviewer.helper.PermissionListHelper;
import com.emetophobe.permissionviewer.model.PermissionDetail;
import com.emetophobe.permissionviewer.view.PermissionListView;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


@PerActivity
public class PermissionListPresenterImpl extends AbstractMvpPresenter<PermissionListView> implements PermissionListPresenter {
	private Subscriber<List<PermissionDetail>> mSubscriber;
	private PermissionListHelper mPermissionHelper;

	@Inject
	public PermissionListPresenterImpl(PermissionListHelper permissionHelper) {
		mPermissionHelper = permissionHelper;
	}

	@Override
	public void loadPermissionList() {
		// Show the loading view
		if (getView() != null) {
			getView().showLoading();
		}

		unsubscribe();

		// Create the subscriber
		mSubscriber = new Subscriber<List<PermissionDetail>>() {
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
					getView().showError(e);
				}
			}

			@Override
			public void onNext(List<PermissionDetail> data) {
				// Set the adapter data
				if (getView() != null) {
					getView().setData(data);
				}
			}
		};

		// Create the observer and subscribe to it
		mPermissionHelper.getPermissionList()
				.subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(mSubscriber);
	}

	private void unsubscribe() {
		if (mSubscriber != null && !mSubscriber.isUnsubscribed()) {
			mSubscriber.unsubscribe();
		}
		mSubscriber = null;
	}

	@Override
	public void detachView() {
		super.detachView();
		unsubscribe();
	}
}
