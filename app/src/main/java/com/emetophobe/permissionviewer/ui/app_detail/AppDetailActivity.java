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


package com.emetophobe.permissionviewer.ui.app_detail;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.emetophobe.permissionviewer.R;
import com.emetophobe.permissionviewer.model.AppDetail;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AppDetailActivity extends AppCompatActivity {
	public static final String EXTRA_APP_DETAIL = "extra_app_detail";

	private AppDetail mAppDetail;

	@Bind(R.id.app_icon)
	protected ImageView mAppIcon;

	@Bind(R.id.app_label)
	protected TextView mAppName;

	@Bind(R.id.app_package)
	protected TextView mAppPackage;

	@Bind(R.id.permission_count)
	protected TextView mPermissionCount;

	@Bind(R.id.recycler_view)
	protected RecyclerView mRecyclerView;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_detail);
		ButterKnife.bind(this);

		// Set up the toolbar.
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayShowTitleEnabled(true);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		// Get the app detail from the intent extras
		mAppDetail = getIntent().getParcelableExtra(EXTRA_APP_DETAIL);
		if (mAppDetail == null) {
			throw new IllegalArgumentException("Must pass a valid AppDetail with EXTRA_APP_DETAIL.");
		}

		// Set the app icon and package name
		mAppIcon.setImageDrawable(getAppIcon());
		mAppPackage.setText(mAppDetail.getPackageName());
		mAppName.setText(mAppDetail.getAppLabel());
		mPermissionCount.setText(getString(R.string.permission_count, mAppDetail.getPermissionList().size()));

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
		AppDetailAdapter adapter = new AppDetailAdapter(mAppDetail.getPermissionList());
		mRecyclerView.setAdapter(adapter);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
	}

	/**
	 * Load the application icon.
	 */
	private Drawable getAppIcon() {
		Drawable drawable;
		try {
			drawable = getPackageManager().getApplicationIcon(mAppDetail.getPackageName());
		} catch (PackageManager.NameNotFoundException e) {
			drawable = null;
		}
		return drawable;
	}
}
