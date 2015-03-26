package com.emetophobe.permissionviewer.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public  class PermissionDatabase extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "permissions.db";
	private static final int DATABASE_VERSION = 7;

	public static final String PERMISSION_TABLE = "permissions";

	public PermissionDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + PERMISSION_TABLE + " ("
				+ PermissionContract.Permissions._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ PermissionContract.Permissions.PACKAGE_NAME + " TEXT NOT NULL, "
				+ PermissionContract.Permissions.APP_NAME + " TEXT NOT NULL, "
				+ PermissionContract.Permissions.PERMISSION_NAME + " TEXT, "
				+ PermissionContract.Permissions.IS_SYSTEM + " INTEGER);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + PERMISSION_TABLE);
		onCreate(db);
	}
}
