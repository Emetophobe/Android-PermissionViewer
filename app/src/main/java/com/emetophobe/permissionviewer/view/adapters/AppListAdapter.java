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

package com.emetophobe.permissionviewer.view.adapters;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emetophobe.permissionviewer.R;
import com.emetophobe.permissionviewer.model.AppDetail;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppListViewHolder> {
	private Callback mCallback;
	private Context mContext;

	private List<AppDetail> mAppList;
	private boolean mShowPermissionCount;

	public AppListAdapter(Context context, boolean showPermissionCount) {
		mContext = context;
		mAppList = Collections.emptyList();
		mShowPermissionCount = showPermissionCount;
	}

	public AppListAdapter(Context context, List<AppDetail> appList, boolean showPermissionCount) {
		mContext = context;
		mAppList = appList;
		mShowPermissionCount = showPermissionCount;
	}

	public void setAppList(List<AppDetail> appList) {
		mAppList = appList;
	}

	public void setCallback(Callback callback) {
		mCallback = callback;
	}

	@Override
	public AppListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_app_list_item, parent, false);
		final AppListViewHolder viewHolder = new AppListViewHolder(itemView);
		viewHolder.contentLayout.setOnClickListener(view -> {
			if (mCallback != null) {
				mCallback.onItemClick(viewHolder.appDetail);
			}
		});

		return viewHolder;
	}

	@Override
	public void onBindViewHolder(AppListViewHolder holder, int position) {
		AppDetail detail = mAppList.get(position);
		holder.appDetail = detail;

		// Set the app label
		String appLabel = detail.getAppLabel();
		if (mShowPermissionCount) {
			appLabel += " (" + detail.getPermissionList().size() + ")";
		}
		holder.label.setText(appLabel);

		// Set the app icon (TODO: Should we load or cache the drawables in a separate thread?)
		Drawable drawable;
		try {
			drawable = mContext.getPackageManager().getApplicationIcon(detail.getPackageName());
		} catch (PackageManager.NameNotFoundException e) {
			drawable = null; // TODO: use a default placeholder icon
		}
		holder.icon.setImageDrawable(drawable);

	}

	@Override
	public int getItemCount() {
		return mAppList.size();
	}

	public static class AppListViewHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.layout_content)
		public View contentLayout;

		@Bind(R.id.app_icon)
		public ImageView icon;

		@Bind(R.id.app_label)
		public TextView label;

		public AppDetail appDetail;

		public AppListViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

	public interface Callback {
		void onItemClick(AppDetail appDetail);
	}
}

