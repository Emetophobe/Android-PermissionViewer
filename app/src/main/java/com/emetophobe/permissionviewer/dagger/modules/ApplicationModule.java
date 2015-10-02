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

package com.emetophobe.permissionviewer.dagger.modules;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.emetophobe.permissionviewer.utils.PermissionHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;


/**
 * Provide application-level dependencies. Mainly singleton object that can be injected from
 * anywhere in the app.
 */
@Module
public class ApplicationModule {
	private final Application mApplication;

	public ApplicationModule(Application application) {
		mApplication = application;
	}

	@Provides
	@Singleton
	Application provideApplication() {
		return mApplication;
	}

	@Provides
	SharedPreferences provideSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(mApplication);
	}

	@Provides
	EventBus provideEventBus() {
		return EventBus.getDefault();
	}

	@Provides
	@Singleton
	PermissionHelper providePermissionHelper() {
		return new PermissionHelper(mApplication);
	}
}
