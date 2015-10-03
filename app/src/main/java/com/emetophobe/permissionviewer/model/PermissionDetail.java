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

package com.emetophobe.permissionviewer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class PermissionDetail implements Parcelable {
	private String mName;
	private List<AppDetail> mAppList;

	public PermissionDetail(String name, List<AppDetail> appList) {
		mName = name;
		mAppList = new ArrayList<>(appList);
	}

	public String getName() {
		return mName;
	}

	public List<AppDetail> getAppList() {
		return mAppList;
	}

	protected PermissionDetail(Parcel in) {
		mName = in.readString();
		mAppList = new ArrayList<>();
		in.readList(mAppList, AppDetail.class.getClassLoader());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mName);
		dest.writeList(mAppList);
	}

	public static final Creator<PermissionDetail> CREATOR = new Creator<PermissionDetail>() {
		@Override
		public PermissionDetail createFromParcel(Parcel in) {
			return new PermissionDetail(in);
		}

		@Override
		public PermissionDetail[] newArray(int size) {
			return new PermissionDetail[size];
		}
	};
}
