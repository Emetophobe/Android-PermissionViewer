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

import com.emetophobe.permissionviewer.dagger.FragmentScope;
import com.emetophobe.permissionviewer.dagger.modules.FragmentModule;
import com.emetophobe.permissionviewer.presenter.AppListPresenter;
import com.emetophobe.permissionviewer.presenter.PermissionListPresenter;
import com.emetophobe.permissionviewer.view.fragments.AppListFragment;
import com.emetophobe.permissionviewer.view.fragments.PermissionListFragment;

import dagger.Component;


/**
 * A scope {@link FragmentScope} component.
 * Injects user specific Fragments.
 */
@FragmentScope
@Component(dependencies = ApplicationComponent.class, modules = {FragmentModule.class})
public interface FragmentComponent {
	void inject(AppListFragment appListFragment);
	void inject(PermissionListFragment permissionListFragment);

	// Exposed to sub-graphs.
	AppListPresenter getAppListPresenter();
	PermissionListPresenter getPermissionListPresenter();
}
