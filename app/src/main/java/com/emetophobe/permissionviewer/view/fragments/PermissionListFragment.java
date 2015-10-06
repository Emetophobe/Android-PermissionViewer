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
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;

import com.emetophobe.permissionviewer.dagger.components.FragmentComponent;
import com.emetophobe.permissionviewer.model.PermissionDetail;
import com.emetophobe.permissionviewer.presenter.PermissionListPresenter;
import com.emetophobe.permissionviewer.view.PermissionListView;
import com.emetophobe.permissionviewer.view.activities.PermissionDetailActivity;
import com.emetophobe.permissionviewer.view.adapters.PermissionListAdapter;

import java.util.List;

import javax.inject.Inject;


public class PermissionListFragment extends AbstractListFragment implements PermissionListView {
	@Inject
	protected PermissionListPresenter mPresenter;

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
		mPresenter.loadPermissionList();
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
		PermissionListAdapter adapter = new PermissionListAdapter();
		adapter.setCallback(new PermissionListAdapter.Callback() {
			@Override
			public void onItemClick(PermissionDetail permissionDetail) {
				Intent intent = new Intent(PermissionListFragment.this.getContext(), PermissionDetailActivity.class);
				intent.putExtra(PermissionDetailActivity.EXTRA_PERMISSION_DETAIL, permissionDetail);
				startActivity(intent);
			}
		});

		mRecyclerView.setAdapter(adapter);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
	}

	@Override
	public void showLoading() {
		setLoading(true);
	}

	@Override
	public void showPermissionList(List<PermissionDetail> permissionList) {
		PermissionListAdapter adapter = (PermissionListAdapter) mRecyclerView.getAdapter();
		adapter.setPermissionList(permissionList);
		adapter.notifyDataSetChanged();
		setLoading(false);
	}

	@Override
	public void showError(@StringRes int resId) {
		mEmptyTextView.setText(resId);
		setLoading(false);
	}
}
