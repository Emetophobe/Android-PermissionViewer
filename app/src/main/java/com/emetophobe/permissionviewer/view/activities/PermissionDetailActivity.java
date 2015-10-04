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

package com.emetophobe.permissionviewer.view.activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.emetophobe.permissionviewer.R;
import com.emetophobe.permissionviewer.model.PermissionDetail;
import com.emetophobe.permissionviewer.view.adapters.AppListAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PermissionDetailActivity extends AppCompatActivity {
	public static final String EXTRA_PERMISSION_DETAIL = "extra_permission_detail";

	private PermissionDetail mPermissionDetail;

	@Bind(R.id.permission_name)
	protected TextView mPermissionName;

	@Bind(R.id.permission_description)
	protected TextView mPermissionDescription;

	@Bind(R.id.application_count)
	protected TextView mAppCount;

	@Bind(R.id.recycler_view)
	protected RecyclerView mRecyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_permission_detail);
		ButterKnife.bind(this);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayShowTitleEnabled(true);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		// Get the permission detail from the intent extras.
		mPermissionDetail = getIntent().getParcelableExtra(EXTRA_PERMISSION_DETAIL);
		if (mPermissionDetail == null) {
			throw new IllegalArgumentException("Must pass a valid PermissionDetail with EXTRA_PERMISSION_DETAIL.");
		}

		mPermissionName.setText(mPermissionDetail.getName());
		mPermissionDescription.setText(getDescription());
		mAppCount.setText(getString(R.string.application_count, mPermissionDetail.getAppList().size()));

		setupRecyclerView();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setupRecyclerView() {
		AppListAdapter adapter = new AppListAdapter(this, mPermissionDetail.getAppList(), false);
		mRecyclerView.setAdapter(adapter);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
	}

	/**
	 * Get the permission description string.
	 */
	private String getDescription() {
		int resId = getResources().getIdentifier("permission_" + mPermissionDetail.getName(), "string", getPackageName());
		return resId != 0 ? getString(resId) : getString(R.string.permission_unknown);
	}
}
