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

package com.emetophobe.permissionviewer.ui.applist;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


// Used by the AppListFragment
public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {
	private List<AppDetail> appList;
	private Callback callback;
	private Context context;

	public AppListAdapter(Context context) {
		this.context = context;
		this.appList = new ArrayList<>();
	}

	public void setAppList(List<AppDetail> data) {
		appList.clear();
		if (data != null) {
			appList.addAll(data);
		}
		notifyDataSetChanged();
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_app_list_item, parent, false);
		final ViewHolder holder = new ViewHolder(itemView);

		holder.contentLayout.setOnClickListener(view -> {
			if (callback != null) {
				callback.onItemClick(holder.appDetail);
			}
		});

		return holder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		AppDetail detail = appList.get(position);
		holder.appDetail = detail;

		// Set the app label
		String appLabel = detail.getAppLabel() + " (" + detail.getPermissionList().size() + ")";
		holder.label.setText(appLabel);

		// Set the app icon (TODO: Should we load or cache the drawables in a separate thread?)
		Drawable drawable;
		try {
			drawable = context.getPackageManager().getApplicationIcon(detail.getPackageName());
		} catch (PackageManager.NameNotFoundException e) {
			drawable = null; // TODO: use a default placeholder icon
		}
		holder.icon.setImageDrawable(drawable);

	}

	@Override
	public int getItemCount() {
		return appList.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.layout_content)
		View contentLayout;

		@Bind(R.id.app_icon)
		ImageView icon;

		@Bind(R.id.app_label)
		TextView label;

		AppDetail appDetail;

		public ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

	public interface Callback {
		void onItemClick(AppDetail appDetail);
	}
}

