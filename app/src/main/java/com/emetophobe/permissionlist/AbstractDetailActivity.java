package com.emetophobe.permissionlist;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


public abstract class AbstractDetailActivity extends ActionBarActivity {
	protected View mHeaderView;
	protected TextView mDescriptionView;
	protected TextView mCountView;
	protected ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_abstract_detail);

		// Set up the toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Get the views
		mListView = (ListView) findViewById(R.id.list);
		mDescriptionView = (TextView) findViewById(R.id.description);
		mCountView = (TextView) findViewById(R.id.count);
	}
}
