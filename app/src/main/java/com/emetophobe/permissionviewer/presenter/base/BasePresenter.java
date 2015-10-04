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

package com.emetophobe.permissionviewer.presenter.base;

import android.support.annotation.Nullable;

import com.emetophobe.permissionviewer.view.MvpView;

import java.lang.ref.WeakReference;


public abstract class BasePresenter<V extends MvpView> implements MvpPresenter<V> {
	private WeakReference<V> mView;

	@Override
	public void attachView(V view) {
		mView = new WeakReference<>(view);
	}

	@Override
	public void detachView(V view) {
		if (mView != null) {
			mView.clear();
			mView = null;
		}
	}

	@Nullable
	public V getView() {
		return mView != null ? mView.get() : null;
	}
}
