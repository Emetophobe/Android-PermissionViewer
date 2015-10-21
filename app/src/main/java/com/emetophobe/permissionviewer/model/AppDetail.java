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
	private String packageName;
	private String appLabel;
	private int systemFlag;
	private List<String> permissionList;

	public AppDetail(String packageName, String appLabel, int systemFlag, List<String> permissions) {
		this.packageName = packageName;
		this.appLabel = appLabel;
		this.systemFlag = systemFlag;
		this.permissionList = new ArrayList<>(permissions);
	}

	public String getPackageName() {
		return packageName;
	}

	public String getAppLabel() {
		return appLabel;
	}

	public int getSystemFlag() {
		return systemFlag;
	}

	public List<String> getPermissionList() {
		return permissionList;
	}

	protected AppDetail(Parcel in) {
		packageName = in.readString();
		appLabel = in.readString();
		systemFlag = in.readInt();
		permissionList = in.createStringArrayList();

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(packageName);
		dest.writeString(appLabel);
		dest.writeInt(systemFlag);
		dest.writeStringList(permissionList);
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
