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
import com.emetophobe.permissionviewer.model.PermissionDetail;
import com.emetophobe.permissionviewer.presenter.PermissionListPresenter;
import com.emetophobe.permissionviewer.view.PermissionListView;
import com.emetophobe.permissionviewer.view.activities.PermissionDetailActivity;
import com.emetophobe.permissionviewer.view.adapters.PermissionListAdapter;

import java.util.List;


public class PermissionListFragment extends AbstractListFragment<List<PermissionDetail>, PermissionListView, PermissionListPresenter> implements PermissionListView {
	private FragmentComponent component;
	private PermissionListAdapter adapter;

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
	public PermissionListPresenter createPresenter() {
		return component.getPermissionListPresenter();
	}

	private void injectDependencies() {
		component = DaggerFragmentComponent.builder()
				.applicationComponent(getAppComponent())
				.build();
		component.inject(this);
	}

	private void setupRecyclerView() {
		adapter = new PermissionListAdapter();
		adapter.setCallback(new PermissionListAdapter.Callback() {
			@Override
			public void onItemClick(PermissionDetail permissionDetail) {
				Intent intent = new Intent(PermissionListFragment.this.getContext(), PermissionDetailActivity.class);
				intent.putExtra(PermissionDetailActivity.EXTRA_PERMISSION_DETAIL, permissionDetail);
				startActivity(intent);
			}
		});

		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
	}

	@Override
	public void setData(List<PermissionDetail> data) {
		adapter.setPermissionList(data);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void loadData(boolean pullToRefresh) {
		getPresenter().loadPermissionList(pullToRefresh);
	}

	@Override
	protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
		return getString(R.string.error_loading_permission_list, e.toString());
	}

}
