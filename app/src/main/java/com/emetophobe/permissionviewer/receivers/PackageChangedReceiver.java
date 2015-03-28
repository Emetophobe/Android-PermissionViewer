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

package com.emetophobe.permissionviewer.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.emetophobe.permissionviewer.services.UpdateDatabaseService;


/**
 * Update the permission database whenever a package is modified.
 * Receives PACKAGE_ADDED, PACKAGE_REMOVED, and PACKAGE_REPLACED events.
 */
public class PackageChangedReceiver extends BroadcastReceiver {
	private static final String TAG = "UpdateReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// Get the package name
		Uri uri = intent.getData();
		String packageName = uri != null ? uri.getSchemeSpecificPart() : null;
		if (TextUtils.isEmpty(packageName)) {
			Log.d(TAG, "Failed to get the package name from intent: " + intent);
			return;
		}

		// Start the update service
		Intent updateIntent = new Intent(context, UpdateDatabaseService.class);
		updateIntent.setAction(UpdateDatabaseService.INTENT_ACTION_UPDATE_PACKAGE);
		updateIntent.putExtra(UpdateDatabaseService.INTENT_EXTRA_PACKAGE_NAME, packageName);
		context.startService(updateIntent);
	}
}
