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
import android.support.v7.widget.LinearLayoutManager;

import com.emetophobe.permissionviewer.R;
import com.emetophobe.permissionviewer.dagger.components.FragmentComponent;
import com.emetophobe.permissionviewer.model.AppDetail;
import com.emetophobe.permissionviewer.presenter.AppListPresenter;
import com.emetophobe.permissionviewer.view.AppListView;
import com.emetophobe.permissionviewer.view.activities.AppDetailActivity;
import com.emetophobe.permissionviewer.view.adapters.AppListAdapter;

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
	}

	@Override
	public void onResume() {
		super.onResume();
		loadData();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mPresenter.detachView();
	}

	private void injectDependencies() {
		getComponent(FragmentComponent.class).inject(this);
	}

	private void setupRecyclerView() {
		AppListAdapter adapter = new AppListAdapter(getContext(), true);
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
	public void showError(Throwable e) {
		super.showError();
		mErrorView.setText(getString(R.string.error_loading_app_list, e.toString()));
	}

	@Override
	public void setData(List<AppDetail> data) {
		AppListAdapter adapter = (AppListAdapter) mRecyclerView.getAdapter();
		adapter.setAppList(data);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void loadData() {
		mPresenter.loadAppList();
	}
}
