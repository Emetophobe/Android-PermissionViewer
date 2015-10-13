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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emetophobe.permissionviewer.R;
import com.emetophobe.permissionviewer.dagger.HasComponent;
import com.emetophobe.permissionviewer.view.MvpView;

import butterknife.Bind;
import butterknife.ButterKnife;


public abstract class AbstractListFragment extends Fragment implements MvpView {
	@Bind(R.id.loading_view)
	protected View mLoadingView;

	@Bind(R.id.recycler_view)
	protected RecyclerView mRecyclerView;

	@Bind(R.id.error_view)
	protected TextView mErrorView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_abstract_list, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	@Override
	public void showLoading() {
		mLoadingView.setVisibility(View.VISIBLE);
		mRecyclerView.setVisibility(View.GONE);
		mErrorView.setVisibility(View.GONE);
	}

	@Override
	public void showContent() {
		mLoadingView.setVisibility(View.GONE);
		mRecyclerView.setVisibility(View.VISIBLE);
		mErrorView.setVisibility(View.GONE);
	}

	@Override
	public void showError(Throwable e) {
		mLoadingView.setVisibility(View.GONE);
		mRecyclerView.setVisibility(View.GONE);
		mErrorView.setVisibility(View.VISIBLE);
	}

	/**
	 * Gets a component for dependency injection by its type.
	 */
	@SuppressWarnings("unchecked")
	protected <C> C getComponent(Class<C> componentType) {
		return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
	}
}
