package com.emetophobe.permissionviewer.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.Log;

import com.emetophobe.permissionviewer.providers.PermissionContract;


public class DatabaseHelper {
	private static final String TAG = "DatabaseHelper";
	private static final String ANDROID_PERMISSION = "android.permission.";

	/**
	 * This class cannot be instantiated.
	 */
	private DatabaseHelper() {

	}


	/**
	 * Insert a package into the database.
	 *
	 * @param context     The context to use.
	 * @param appInfo     The application info.
	 * @param packageName The package name.
	 */
	public static void insert(Context context, ApplicationInfo appInfo, String packageName) {
		PackageManager pm = context.getPackageManager();

		// Get the application label/name
		String appName;
		try {
			appName = pm.getApplicationLabel(appInfo).toString();
		} catch (Resources.NotFoundException e) {
			// application label not found, just use the package name.
			appName = packageName;
		}

		// Get the system app flag
		boolean isSystemApp = (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

		try {
			// Get the list of permissions
			PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
			if (packageInfo.requestedPermissions != null  && packageInfo.requestedPermissions.length > 0) {
				String permissionName;
				for (int i = 0; i < packageInfo.requestedPermissions.length; ++i) {
					if (packageInfo.requestedPermissions[i].startsWith(ANDROID_PERMISSION)) {
						permissionName = packageInfo.requestedPermissions[i].substring(ANDROID_PERMISSION.length());

						// Add a separate entry for each permission
						insert(context, packageName, appName, permissionName, isSystemApp);
					}
				}
			} else {
				// Add an empty permission entry for packages that contain zero permissions
				insert(context, packageName, appName, null, isSystemApp);
			}
		} catch (PackageManager.NameNotFoundException e) {
			Log.e(TAG, e.toString());
		}
	}

	/** Delete all entries that match the specified package name. */
	public static void delete(Context context, String packageName) {
		context.getContentResolver().delete(PermissionContract.Permissions.CONTENT_URI,
				PermissionContract.Permissions.PACKAGE_NAME + "=?", new String[]{packageName});

	}

	/**
	 * Insert a package entry into the database.
	 *
	 * @param context        The context to use.
	 * @param packageName    The package name.
	 * @param appName        The application name.
	 * @param permissionName The permission name (optional, can be null).
	 * @param isSystemApp    Whether the package is a system app or not.
	 */
	private static void insert(Context context, String packageName, String appName, String permissionName, boolean isSystemApp) {
		ContentValues values = new ContentValues();
		values.put(PermissionContract.Permissions.APP_NAME, appName);
		values.put(PermissionContract.Permissions.PACKAGE_NAME, packageName);
		values.put(PermissionContract.Permissions.IS_SYSTEM, isSystemApp);

		if (permissionName != null) {
			values.put(PermissionContract.Permissions.PERMISSION_NAME, permissionName);
		}

		context.getContentResolver().insert(PermissionContract.Permissions.CONTENT_URI, values);
	}
}
