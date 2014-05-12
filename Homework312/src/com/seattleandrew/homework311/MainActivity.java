package com.seattleandrew.homework311;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {

	// All static variables
	static final String URL = "https://news.google.com/news/section?topic=w&output=rss";
	// XML node keys
	static final String KEY_ITEM = "item"; // parent node
	static final String KEY_TITLE = "title";
	static final String KEY_PUBDATE = "pubDate";
	static final String KEY_DESC = "description";
	
	static final String EXTRA_MESSAGE = "com.seattleandrew.homework311.MESSAGE";
	List<String> li;
	ListView lv;
	private SensorManager mSensorManager;
	private float mAccel; // acceleration apart from gravity
	private float mAccelCurrent; // current acceleration including gravity
	private float mAccelLast; // last acceleration including gravity

	private final SensorEventListener mSensorListener = new SensorEventListener() {

		public void onSensorChanged(SensorEvent se) {
			float x = se.values[0];
			float y = se.values[1];
			float z = se.values[2];
			mAccelLast = mAccelCurrent;
			mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
			float delta = mAccelCurrent - mAccelLast;
			mAccel = mAccel * 0.9f + delta; // perform low-cut filter
			if (mAccel > 1) { 
				Toast toast = Toast.makeText(getApplicationContext(), "Refreshing List", Toast.LENGTH_LONG);
				toast.show();

				//TODO if I had a content provider, I would actually refresh the list at this point
				//refreshList();
			}
	    }

	    public void onAccuracyChanged(Sensor sensor, int accuracy) {
	    }
  	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Add stuff for shake detection
	    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	    mAccel = 0.00f;
	    mAccelCurrent = SensorManager.GRAVITY_EARTH;
	    mAccelLast = SensorManager.GRAVITY_EARTH;

	    ArrayList<String> titles = null;
		ArrayList<String> dates = null;
		ArrayList<String> contents = null;
		
		titles.add(getString(R.string.example_title));
		dates.add(getResources().getString(R.string.example_date));
		contents.add(getResources().getString(R.string.example_content));
		
		//oh shoot son, this stuff is gonna break everything--------------------------------------------------------------------------
		ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();

		XMLParser parser = new XMLParser();
		String xml = parser.getXmlFromUrl(URL); // getting XML
		Document doc = parser.getDomElement(xml); // getting DOM element

		NodeList nl = doc.getElementsByTagName(KEY_ITEM);
		// looping through all item nodes <item>
		for (int i = 0; i < nl.getLength(); i++) {
			// creating new HashMap
			HashMap<String, String> map = new HashMap<String, String>();
			Element e = (Element) nl.item(i);
			// adding each child node to HashMap key => value
			map.put(KEY_TITLE, parser.getValue(e, KEY_TITLE));
			map.put(KEY_PUBDATE, parser.getValue(e, KEY_PUBDATE));
			map.put(KEY_DESC, parser.getValue(e, KEY_DESC));

			// adding HashList to ArrayList
			items.add(map);
			AlertDialog dialog = new AlertDialog.Builder(this).setMessage("test").setTitle("Test").create();
			if (dialog != null)
			    dialog.show();
		}

		//cross fingers-------------------------------------------------------------------------------------------------------------
		
		//Add stuff for the listView implementation
				lv = (ListView) findViewById(R.id.listView1);

				//Create an ArrayList and import the XML data... which is in a String array... this is messy
				li=new ArrayList<String>();
				for(int i = 0; i < 1; i++){
					li.add(getString(R.string.example_title));
				}
				addContent();
	}

  	@Override
  	protected void onResume() {
  		super.onResume();
	    mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
  	}

  	@Override
  	protected void onPause() {
	    mSensorManager.unregisterListener(mSensorListener);
	    super.onPause();
  	}
  	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void addContent(){
    	ArrayAdapter<String> adp=new ArrayAdapter<String> 
		(getBaseContext(),R.layout.list,R.id.label,li);
		lv.setAdapter(adp);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				openArticle(arg2);
			}
		});
    }
	public void openArticle(int index){
		Intent intent = new Intent(this, ArticleActivity.class);
	    intent.putExtra(EXTRA_MESSAGE, index);
	    startActivity(intent);
	}
}