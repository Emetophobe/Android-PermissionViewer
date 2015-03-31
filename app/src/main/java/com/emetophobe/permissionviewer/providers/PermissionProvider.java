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

package com.emetophobe.permissionviewer.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.emetophobe.permissionviewer.providers.PermissionContract.Permissions;


public class PermissionProvider extends ContentProvider {
	private static final int PERMISSIONS = 0;
	private static final int PERMISSION_ID = 1;
	private static final int APPLICATION_LIST = 2;
	private static final int PERMISSION_LIST = 3;

	private static final UriMatcher sUriMatcher;
	private PermissionDatabase mDbHelper;

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(PermissionContract.AUTHORITY, PermissionContract.PATH_PERMISSIONS, PERMISSIONS);
		sUriMatcher.addURI(PermissionContract.AUTHORITY, PermissionContract.PATH_PERMISSIONS + "/#", PERMISSION_ID);
		sUriMatcher.addURI(PermissionContract.AUTHORITY, PermissionContract.PATH_APP_LIST, APPLICATION_LIST);
		sUriMatcher.addURI(PermissionContract.AUTHORITY, PermissionContract.PATH_PERMISSION_LIST, PERMISSION_LIST);
	}

	@Override
	public boolean onCreate() {
		mDbHelper = new PermissionDatabase(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		throw new UnsupportedOperationException("Unsupported operation");
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		Uri contentUri;
		long rowId;

		switch (sUriMatcher.match(uri)) {
			case PERMISSIONS:
				rowId = db.insertOrThrow(PermissionDatabase.PERMISSION_TABLE, null, values);
				contentUri = PermissionContract.Permissions.CONTENT_URI;
				break;

			default:
				throw new UnsupportedOperationException("Unsupported uri: " + uri);
		}

		// Notify observers if a row was added
		if (rowId > 0) {
			Uri newUri = ContentUris.withAppendedId(contentUri, rowId);
			notifyChange(newUri);
			return newUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables(PermissionDatabase.PERMISSION_TABLE);
		String groupBy = null;

		switch (sUriMatcher.match(uri)) {
			case PERMISSIONS:
				break;

			case PERMISSION_ID:
				sqlBuilder.appendWhere(PermissionContract.Permissions._ID + " = " + uri.getPathSegments().get(1));
				break;

			case APPLICATION_LIST:
				groupBy = Permissions.APP_NAME;
				break;

			case PERMISSION_LIST:
				groupBy = Permissions.PERMISSION_NAME;
				break;

			default:
				throw new UnsupportedOperationException("Unsupported uri: " + uri);
		}

		// Run the query
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = sqlBuilder.query(db, projection, selection, selectionArgs, groupBy, null, sortOrder, null);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int count;

		switch (sUriMatcher.match(uri)) {
			case PERMISSIONS:
				count = db.update(PermissionDatabase.PERMISSION_TABLE, values, selection, selectionArgs);
				break;

			case PERMISSION_ID:
				selection = PermissionContract.Permissions._ID + " = " + uri.getPathSegments().get(1)
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
				count = db.update(PermissionDatabase.PERMISSION_TABLE, values, selection, selectionArgs);
				break;

			default:
				throw new UnsupportedOperationException("Unsupported uri: " + uri);
		}

		// Notify observers if row(s) were updated
		if (count > 0) {
			notifyChange(uri);
		}
		return count;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int count;

		switch (sUriMatcher.match(uri)) {
			case PERMISSIONS:
				count = db.delete(PermissionDatabase.PERMISSION_TABLE, selection, selectionArgs);
				break;

			case PERMISSION_ID:
				selection = PermissionContract.Permissions._ID + " = " + uri.getPathSegments().get(1)
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
				count = db.delete(PermissionDatabase.PERMISSION_TABLE, selection, selectionArgs);
				break;

			default:
				throw new UnsupportedOperationException("Unsupported uri: " + uri);
		}

		// Notify observers if row(s) were deleted
		if (count > 0) {
			notifyChange(uri);
		}
		return count;
	}

	private void notifyChange(Uri uri) {
		getContext().getContentResolver().notifyChange(uri, null);
		getContext().getContentResolver().notifyChange(Permissions.APPLICATIONS_URI, null);
		getContext().getContentResolver().notifyChange(Permissions.PERMISSIONS_URI, null);
	}
}
