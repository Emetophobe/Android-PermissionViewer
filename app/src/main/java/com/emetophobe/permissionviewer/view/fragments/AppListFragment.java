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

package com.emetophobe.permissionviewer.view.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;

import com.emetophobe.permissionviewer.dagger.components.FragmentComponent;
import com.emetophobe.permissionviewer.model.AppDetail;
import com.emetophobe.permissionviewer.view.activities.AppDetailActivity;
import com.emetophobe.permissionviewer.view.adapters.AppListAdapter;
import com.emetophobe.permissionviewer.presenter.AppListPresenter;
import com.emetophobe.permissionviewer.view.AppListView;
import com.emetophobe.permissionviewer.utils.SettingsHelper;

import java.util.List;

import javax.inject.Inject;


public class AppListFragment extends AbstractListFragment implements AppListView {
	@Inject
	protected AppListPresenter mPresenter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		injectDependencies();
		setupRecyclerView();

		mPresenter.attachView(this);
		mPresenter.loadAppList(false);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mPresenter.detachView(this);
	}

	private void injectDependencies() {
		getComponent(FragmentComponent.class).inject(this);
	}

	private void setupRecyclerView() {
		AppListAdapter adapter = new AppListAdapter(getContext());
		adapter.setCallback(new AppListAdapter.Callback() {
			@Override
			public void onItemClick(AppDetail appDetail) {
				Intent intent = new Intent(AppListFragment.this.getContext(), AppDetailActivity.class);
				intent.putExtra(AppDetailActivity.EXTRA_APP_DETAIL, appDetail);
				startActivity(intent);
			}
		});

		mRecyclerView.setAdapter(adapter);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(SettingsHelper.APP_SORT_ORDER) || key.equals(SettingsHelper.SHOW_SYSTEM_APPS)) {
			mPresenter.loadAppList(true);
		}
	}

	@Override
	public void showLoading() {
		setLoading(true);
	}

	@Override
	public void showAppList(List<AppDetail> appList) {
		AppListAdapter adapter = (AppListAdapter) mRecyclerView.getAdapter();
		adapter.setAppList(appList);
		adapter.notifyDataSetChanged();
		setLoading(false);
	}

	@Override
	public void showError(@StringRes int resId) {
		mEmptyTextView.setText(resId);
		setLoading(false);
	}
}
