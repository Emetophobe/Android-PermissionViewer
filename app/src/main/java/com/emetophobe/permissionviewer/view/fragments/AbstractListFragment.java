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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.emetophobe.permissionviewer.R;
import com.emetophobe.permissionviewer.dagger.components.FragmentComponent;

import javax.inject.Inject;

import butterknife.Bind;


public abstract class AbstractListFragment extends BaseFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
	@Inject
	protected SharedPreferences mSharedPrefs;

	@Bind(R.id.view_flipper)
	protected ViewFlipper mViewFlipper;

	@Bind(R.id.recycler_view)
	protected RecyclerView mRecyclerView;

	@Bind(R.id.empty_view)
	protected TextView mEmptyTextView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_abstract_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		injectDependencies();
		mSharedPrefs.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
	}

	private void injectDependencies() {
		getComponent(FragmentComponent.class).inject(this);
	}

	protected void setLoading(boolean show) {
		mViewFlipper.setDisplayedChild(show ? 0 : 1);
	}
}
