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

package com.emetophobe.permissionviewer.injection.modules;

import com.emetophobe.permissionviewer.injection.FragmentScope;
import com.emetophobe.permissionviewer.ui.applist.AppListPresenter;
import com.emetophobe.permissionviewer.ui.applist.AppListPresenterImpl;
import com.emetophobe.permissionviewer.ui.permissionlist.PermissionListPresenter;
import com.emetophobe.permissionviewer.ui.permissionlist.PermissionListPresenterImpl;
import com.emetophobe.permissionviewer.helper.AppListHelper;
import com.emetophobe.permissionviewer.helper.PermissionListHelper;

import dagger.Module;
import dagger.Provides;


@Module
public class FragmentModule {
	public FragmentModule() {}

	@Provides
	@FragmentScope
	AppListPresenter provideAppListPresenter(AppListHelper appListHelper) {
		return new AppListPresenterImpl(appListHelper);
	}

	@Provides
	@FragmentScope
	PermissionListPresenter providePermissionListPresenter(PermissionListHelper permissionHelper) {
		return new PermissionListPresenterImpl(permissionHelper);
	}
}
