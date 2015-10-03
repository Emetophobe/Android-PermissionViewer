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


public class AppDetail implements Parcelable {
	private String mPackageName;
	private String mAppLabel;
	private int mSystemFlag;
	private List<String> mPermissionList;

	public AppDetail(String packageName, String appLabel, int systemFlag, List<String> permissions) {
		mPackageName = packageName;
		mAppLabel = appLabel;
		mSystemFlag = systemFlag;
		mPermissionList = new ArrayList<>(permissions);
	}

	public String getPackageName() {
		return mPackageName;
	}

	public String getAppLabel() {
		return mAppLabel;
	}

	public int getSystemFlag() {
		return mSystemFlag;
	}

	public List<String> getPermissionList() {
		return mPermissionList;
	}

	protected AppDetail(Parcel in) {
		mPackageName = in.readString();
		mAppLabel = in.readString();
		mSystemFlag = in.readInt();
		mPermissionList = in.createStringArrayList();

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mPackageName);
		dest.writeString(mAppLabel);
		dest.writeInt(mSystemFlag);
		dest.writeStringList(mPermissionList);
	}

	public static final Creator<AppDetail> CREATOR = new Creator<AppDetail>() {
		@Override
		public AppDetail createFromParcel(Parcel in) {
			return new AppDetail(in);
		}

		@Override
		public AppDetail[] newArray(int size) {
			return new AppDetail[size];
		}
	};
}
