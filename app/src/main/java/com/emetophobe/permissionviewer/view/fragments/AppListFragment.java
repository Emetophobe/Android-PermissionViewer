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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emetophobe.permissionviewer.R;
import com.emetophobe.permissionviewer.dagger.components.DaggerFragmentComponent;
import com.emetophobe.permissionviewer.dagger.components.FragmentComponent;
import com.emetophobe.permissionviewer.model.AppDetail;
import com.emetophobe.permissionviewer.presenter.AppListPresenter;
import com.emetophobe.permissionviewer.view.AppListView;
import com.emetophobe.permissionviewer.view.activities.AppDetailActivity;
import com.emetophobe.permissionviewer.view.adapters.AppListAdapter;

import java.util.List;


public class AppListFragment extends AbstractListFragment<List<AppDetail>, AppListView, AppListPresenter> implements AppListView {
	private FragmentComponent component;
	private AppListAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		injectDependencies();
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setupRecyclerView();
	}

	@Override
	public void onResume() {
		super.onResume();
		loadData(false);
	}

	@NonNull
	@Override
	public AppListPresenter createPresenter() {
		return component.getAppListPresenter();
	}

	private void injectDependencies() {
		component = DaggerFragmentComponent.builder()
				.applicationComponent(getAppComponent())
				.build();
		component.inject(this);
	}

	private void setupRecyclerView() {
		adapter = new AppListAdapter(getContext());
		adapter.setCallback(new AppListAdapter.Callback() {
			@Override
			public void onItemClick(AppDetail appDetail) {
				Intent intent = new Intent(AppListFragment.this.getContext(), AppDetailActivity.class);
				intent.putExtra(AppDetailActivity.EXTRA_APP_DETAIL, appDetail);
				startActivity(intent);
			}
		});

		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
	}

	@Override
	public void setData(List<AppDetail> data) {
		adapter.setAppList(data);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void loadData(boolean pullToRefresh) {
		getPresenter().loadAppList(pullToRefresh);
	}

	@Override
	protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
		return getString(R.string.error_loading_app_list, e.toString());
	}
}
