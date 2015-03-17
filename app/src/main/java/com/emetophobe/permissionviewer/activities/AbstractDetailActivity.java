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

package com.emetophobe.permissionviewer.activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.emetophobe.permissionviewer.R;


public abstract class AbstractDetailActivity extends ActionBarActivity {
	private TextView mDescriptionText;
	private TextView mCountText;
	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_abstractdetail);

		// Set up the toolbar.
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Find the views.
		mListView = (ListView) findViewById(R.id.list);
		mDescriptionText = (TextView) findViewById(R.id.description);
		mCountText = (TextView) findViewById(R.id.count);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Convenience method to set the listview adapter.
	 *
	 * @param adapter The listview adapter.
	 */
	protected void setListAdapter(ListAdapter adapter) {
		mListView.setAdapter(adapter);
	}

	/**
	 * Convenience method to set the description text.
	 *
	 * @param text The text to display.
	 */
	protected void setDescription(String text) {
		mDescriptionText.setText(text);
	}

	/**
	 * Convenience method to set the application or permission count.
	 *
	 * @param text The text to display.
	 */
	protected void setCount(String text) {
		mCountText.setText(text);
	}
}
