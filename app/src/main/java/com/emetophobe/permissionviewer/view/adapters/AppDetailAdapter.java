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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emetophobe.permissionviewer.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


// Used by the AppDetailActivity
public class AppDetailAdapter extends RecyclerView.Adapter<AppDetailAdapter.ViewHolder> {
	private List<String> mPermissionList;

	public AppDetailAdapter(List<String> permissionList) {
		mPermissionList = permissionList;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_permission_list_item, parent, false);
		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		String permission = mPermissionList.get(position);
		holder.name.setText(permission);
	}

	@Override
	public int getItemCount() {
		return mPermissionList.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.permission_name)
		public TextView name;

		public ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
