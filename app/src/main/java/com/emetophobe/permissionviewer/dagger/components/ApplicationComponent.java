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

package com.emetophobe.permissionviewer.dagger.components;

import android.app.Application;
import android.content.SharedPreferences;

import com.emetophobe.permissionviewer.dagger.modules.ApplicationModule;
import com.emetophobe.permissionviewer.ui.MainActivity;
import com.emetophobe.permissionviewer.utils.AppListHelper;
import com.emetophobe.permissionviewer.utils.PermissionListHelper;
import com.emetophobe.permissionviewer.utils.SettingsHelper;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
	void inject(MainActivity mainActivity);

	Application application();
	SharedPreferences sharedPrefs();
	SettingsHelper settingsHelper();
	AppListHelper appListHelper();
	PermissionListHelper permissionHelper();
}