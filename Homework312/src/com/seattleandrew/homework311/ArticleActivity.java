package com.seattleandrew.homework311;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class ArticleActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//grab the intent data
		Intent intent = getIntent();
		int message = intent.getIntExtra(MainActivity.EXTRA_MESSAGE, 0);
		
		//setup string arrays to grab appropriate data
		/*String[] titles = getResources().getStringArray(R.array.article_titles);
		String[] contents = getResources().getStringArray(R.array.article_contents);
		String[] dates = getResources().getStringArray(R.array.article_dates);
		
		//fill data on create
		TextView title = (TextView) findViewById(R.id.textView1);
		TextView content = (TextView) findViewById(R.id.textView2);
		TextView date = (TextView) findViewById(R.id.textView3);
		title.setText(titles[message]);
		content.setText(contents[message]);
		date.setText(dates[message]);*/
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.article, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
