package com.androidhive.xmlparsing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;
import com.seattleandrew.homework312apn2.R;

public class SingleMenuItemActivity  extends Activity {
	
	// XML node keys
	static final String KEY_TITLE = "title";
	static final String KEY_PUBDATE = "pubDate";
	static final String KEY_DESC = "description";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_list_item);
        
        // getting intent data
        Intent in = getIntent();
        
        // Get XML values from previous intent
        String name = in.getStringExtra(KEY_TITLE);
        String cost = in.getStringExtra(KEY_PUBDATE);
        String description = in.getStringExtra(KEY_DESC);
        
        // Displaying all values on the screen
        TextView lblName = (TextView) findViewById(R.id.name_label);
        TextView lblCost = (TextView) findViewById(R.id.cost_label);
        TextView lblDesc = (TextView) findViewById(R.id.description_label);
        WebView webview = (WebView) findViewById(R.id.webview);
        
        lblName.setText(name);
        lblCost.setText(cost);
        lblDesc.setText(description);
        webview.loadData(description, "text/html", null);
    }
}
