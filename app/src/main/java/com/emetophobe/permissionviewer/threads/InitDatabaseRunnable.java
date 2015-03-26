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

package com.emetophobe.permissionviewer.threads;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.LocalBroadcastManager;

import com.emetophobe.permissionviewer.utils.DatabaseHelper;

import java.util.List;


/**
 * Initialize the permission database in background thread/runnable.
 */
public class InitDatabaseRunnable implements Runnable {
	public static final int MESSAGE_PROGRESS_INIT = 0;
	public static final int MESSAGE_PROGRESS_UPDATE = 1;
	public static final int MESSAGE_PROGRESS_COMPLETE = 2;

	public static final String PERMISSION_INTENT = "permission_intent";
	public static final String PROGRESS_MESSAGE = "progress_message";
	public static final String PROGRESS_VALUE = "progress_value";

	private Context mContext;

	public InitDatabaseRunnable(Context context) {
		mContext = context.getApplicationContext();
	}

	@Override
	public void run() {
		// Get the list of installed packages
		List<ApplicationInfo> packages = mContext.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

		// Send a message to the main thread to display the initial progress dialog
		broadcastMessage(MESSAGE_PROGRESS_INIT, packages.size());

		// Iterate over each package in the list and add them to the database
		int count = 0;
		for (ApplicationInfo appInfo : packages) {
			DatabaseHelper.insert(mContext, appInfo, appInfo.packageName);
			broadcastMessage(MESSAGE_PROGRESS_UPDATE, ++count); // update the progress dialog
		}

		// Send a message to the main thread that the thread is finished.
		broadcastMessage(MESSAGE_PROGRESS_COMPLETE, 0);
	}

	/**
	 * Send a progress update message to the main/ui thread.
	 *
	 * @param message  The progress type.
	 * @param progress The progress value.
	 */
	private void broadcastMessage(int message, int progress) {
		Intent intent = new Intent(PERMISSION_INTENT);
		intent.putExtra(PROGRESS_MESSAGE, message);
		intent.putExtra(PROGRESS_VALUE, progress);
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
	}
}

