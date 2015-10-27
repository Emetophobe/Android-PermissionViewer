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

package com.emetophobe.permissionviewer.ui;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.emetophobe.permissionviewer.PermissionApp;
import com.emetophobe.permissionviewer.R;
import com.emetophobe.permissionviewer.injection.components.ApplicationComponent;
import com.hannesdorfmann.mosby.mvp.MvpFragment;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.lce.LceAnimator;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import butterknife.Bind;
import butterknife.ButterKnife;


public abstract class AbstractListFragment<M, V extends MvpLceView<M>, P extends MvpPresenter<V>>
		extends MvpFragment<V, P> implements MvpLceView<M> {

	@Bind(R.id.loading_view)
	protected View loadingView;

	@Bind(R.id.recycler_view)
	protected RecyclerView recyclerView;

	@Bind(R.id.error_view)
	protected TextView errorView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_abstract_list, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);

		errorView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onErrorViewClicked();
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	@Override
	public void showLoading(boolean pullToRefresh) {
		if (!pullToRefresh) {
			LceAnimator.showLoading(loadingView, recyclerView, errorView);
		}

		// otherwise the pull to refresh widget will already display a loading animation
	}

	@Override
	public void showContent() {
		LceAnimator.showContent(loadingView, recyclerView, errorView);
	}

	/**
	 * Get the error message for a certain Exception that will be shown on {@link
	 * #showError(Throwable, boolean)}
	 */
	protected abstract String getErrorMessage(Throwable e, boolean pullToRefresh);

	/**
	 * The default behaviour is to display a toast message as light error (i.e. pull-to-refresh
	 * error).
	 * Override this method if you want to display the light error in another way (like crouton).
	 */
	protected void showLightError(String msg) {
		if (getActivity() != null) {
			Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Called if the error view has been clicked. To disable clicking on the errorView use
	 * <code>errorView.setClickable(false)</code>
	 */
	protected void onErrorViewClicked() {
		loadData(false);
	}

	@Override
	public void showError(Throwable e, boolean pullToRefresh) {
		String errorMsg = getErrorMessage(e, pullToRefresh);

		if (pullToRefresh) {
			showLightError(errorMsg);
		} else {
			errorView.setText(errorMsg);
			LceAnimator.showErrorView(loadingView, recyclerView, errorView);
		}
	}

	/**
	 * Get the application component.
	 *
	 * @return The component.
	 */
	protected ApplicationComponent getAppComponent() {
		return ((PermissionApp) getActivity().getApplication()).getApplicationComponent();
	}
}
