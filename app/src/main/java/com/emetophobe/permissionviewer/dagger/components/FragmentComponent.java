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

import com.emetophobe.permissionviewer.dagger.PerActivity;
import com.emetophobe.permissionviewer.dagger.modules.ActivityModule;
import com.emetophobe.permissionviewer.dagger.modules.FragmentModule;
import com.emetophobe.permissionviewer.ui.app_list.AppListFragment;
import com.emetophobe.permissionviewer.ui.permission_list.PermissionListFragment;

import dagger.Component;


/**
 * A scope {@link PerActivity} component.
 * Injects user specific Fragments.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, FragmentModule.class})
public interface FragmentComponent extends ActivityComponent {
	void inject(AppListFragment appListFragment);
	void inject(PermissionListFragment permissionListFragment);
}
