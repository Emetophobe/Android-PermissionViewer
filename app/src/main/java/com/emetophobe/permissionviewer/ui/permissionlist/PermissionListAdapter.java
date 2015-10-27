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

package com.emetophobe.permissionviewer.ui.permissionlist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emetophobe.permissionviewer.R;
import com.emetophobe.permissionviewer.model.PermissionDetail;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


// Used by the PermissionListFragment
public class PermissionListAdapter extends RecyclerView.Adapter<PermissionListAdapter.ViewHolder> {
	private List<PermissionDetail> permissionList;
	private Callback callback;

	public PermissionListAdapter() {
		permissionList = new ArrayList<>();
	}

	public void setPermissionList(List<PermissionDetail> data) {
		permissionList.clear();
		if (data != null) {
			permissionList.addAll(data);
		}
		notifyDataSetChanged();
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_permission_list_item, parent, false);
		final ViewHolder viewHolder = new ViewHolder(itemView);
		viewHolder.name.setOnClickListener(view -> {
			if (callback != null) {
				callback.onItemClick(viewHolder.permissionDetail);
			}
		});

		return viewHolder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		PermissionDetail detail = permissionList.get(position);
		holder.permissionDetail = detail;

		// Set the permission name
		String name = detail.getName() + " (" + detail.getAppList().size() + ")";
		holder.name.setText(name);

	}

	@Override
	public int getItemCount() {
		return permissionList.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.permission_name)
		TextView name;

		PermissionDetail permissionDetail;

		public ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
			//name = (TextView) itemView.findViewById(R.id.permission_name);
		}
	}

	public interface Callback {
		void onItemClick(PermissionDetail permissionDetail);
	}
}

