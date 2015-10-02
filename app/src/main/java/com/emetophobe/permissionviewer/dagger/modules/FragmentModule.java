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

import com.emetophobe.permissionviewer.dagger.PerActivity;
import com.emetophobe.permissionviewer.ui.app_list.AppListPresenter;
import com.emetophobe.permissionviewer.ui.app_list.AppListPresenterImpl;
import com.emetophobe.permissionviewer.ui.permission_list.PermissionListPresenter;
import com.emetophobe.permissionviewer.ui.permission_list.PermissionListPresenterImpl;
import com.emetophobe.permissionviewer.utils.PermissionHelper;

import dagger.Module;
import dagger.Provides;


@Module
public class FragmentModule {
	public FragmentModule() {}

	@Provides
	@PerActivity
	AppListPresenter provideAppListPresenter(PermissionHelper permissionHelper) {
		return new AppListPresenterImpl(permissionHelper);
	}

	@Provides
	@PerActivity
	PermissionListPresenter providePermissionListPresenter(PermissionHelper permissionHelper) {
		return new PermissionListPresenterImpl(permissionHelper);
	}
}
