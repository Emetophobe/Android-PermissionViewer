/*
 * Copyright (C) 2013-2014 Mike Cunningham
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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.emetophobe.permissionlist.PermissionScanner;
import com.emetophobe.permissionlist.providers.PermissionContract.Permissions;


public class PermissionProvider extends ContentProvider {
	private static final String PERMISSION_TABLE = "permissions";

	private static final int PERMISSIONS = 0;
	private static final int PERMISSION_ID = 1;
	private static final int APPLICATION_LIST = 2;
	private static final int PERMISSION_LIST = 3;

	private static final UriMatcher sUriMatcher;
	private DatabaseHelper mDbHelper;

	// Internal database helper class.
	private static class DatabaseHelper extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "permissions.db";
		private static final int DATABASE_VERSION = 7;

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// Create the applications table
			db.execSQL("CREATE TABLE " + PERMISSION_TABLE + " ("
					+ PermissionContract.Permissions._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ PermissionContract.Permissions.PACKAGE_NAME + " TEXT NOT NULL, "
					+ PermissionContract.Permissions.APP_NAME + " TEXT NOT NULL, "
					+ PermissionContract.Permissions.PERMISSION_NAME + " TEXT, "
					+ PermissionContract.Permissions.IS_SYSTEM + " INTEGER);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + PERMISSION_TABLE);
			onCreate(db);
		}
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(PermissionContract.AUTHORITY, "permissions", PERMISSIONS);
		sUriMatcher.addURI(PermissionContract.AUTHORITY, "permissions/#", PERMISSION_ID);
		sUriMatcher.addURI(PermissionContract.AUTHORITY, "app_list", APPLICATION_LIST);
		sUriMatcher.addURI(PermissionContract.AUTHORITY, "permission_list", PERMISSION_LIST);
	}

	@Override
	public boolean onCreate() {
		mDbHelper = new DatabaseHelper(getContext());
		return (mDbHelper != null);
	}

	@Override
	public String getType(Uri uri) {
		// TODO: Implement this to handle requests for the MIME type of the data at the given URI.
		throw new UnsupportedOperationException("Unsupported operation");
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		Uri contentUri = null;
		long rowId = -1;

		switch (sUriMatcher.match(uri)) {
			case PERMISSIONS:
				rowId = db.insertOrThrow(PERMISSION_TABLE, null, values);
				contentUri = PermissionContract.Permissions.CONTENT_URI;
				break;

			default:
				throw new UnsupportedOperationException("Unsupported URI " + uri);
		}

		// Notify observers if a row was added
		if (rowId > 0) {
			Uri newUri = ContentUris.withAppendedId(contentUri, rowId);
			getContext().getContentResolver().notifyChange(newUri, null);
			return newUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables(PERMISSION_TABLE);
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
		int count = 0;

		switch (sUriMatcher.match(uri)) {
			case PERMISSIONS:
				count = db.update(PERMISSION_TABLE, values, selection, selectionArgs);
				break;

			case PERMISSION_ID:
				selection = PermissionContract.Permissions._ID + " = " + uri.getPathSegments().get(1)
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
				count = db.update(PERMISSION_TABLE, values, selection, selectionArgs);
				break;

			default:
				throw new UnsupportedOperationException("Unsupported URI " + uri);
		}

		// Notify observers if row(s) were updated
		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return count;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int count = 0;

		switch (sUriMatcher.match(uri)) {
			case PERMISSIONS:
				count = db.delete(PERMISSION_TABLE, selection, selectionArgs);
				break;

			case PERMISSION_ID:
				selection = PermissionContract.Permissions._ID + " = " + uri.getPathSegments().get(1)
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
				count = db.delete(PERMISSION_TABLE, selection, selectionArgs);
				break;

			default:
				throw new UnsupportedOperationException("Unsupported URI " + uri);
		}

		// Notify observers if row(s) were deleted
		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return count;
	}
}
