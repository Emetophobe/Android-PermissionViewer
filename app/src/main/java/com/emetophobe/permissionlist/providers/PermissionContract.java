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

package com.emetophobe.permissionlist.providers;

import android.net.Uri;
import android.provider.BaseColumns;


public class PermissionContract {
	public static final String AUTHORITY = "com.emetophobe.permissionlist.providers.PermissionProvider";

	private PermissionContract() {}

	public static final class Permissions implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/permissions");
		public static final Uri APPLICATIONS_URI = Uri.parse("content://" + AUTHORITY + "/app_list");
		public static final Uri PERMISSIONS_URI = Uri.parse("content://" + AUTHORITY + "/permission_list");

		/** The application label. */
		public static final String APP_NAME = "app_name";
		/** The package name. */
		public static final String PACKAGE_NAME = "package_name";
		/** The permission name (optional). */
		public static final String PERMISSION_NAME = "permission";
		/** The system package flag (0 or 1). */
		public static final String IS_SYSTEM = "is_system";

		private Permissions() {}
	}
}
