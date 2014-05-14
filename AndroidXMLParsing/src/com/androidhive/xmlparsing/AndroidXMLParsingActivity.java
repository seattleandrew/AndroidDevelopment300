package com.androidhive.xmlparsing;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.seattleandrew.homework312apn2.R;

public class AndroidXMLParsingActivity extends ListActivity {

	// All static variables
	static final String URL = "https://news.google.com/news/section?topic=w&output=rss";
	static final String URL2 = "https://news.yahoo.com/rss/world/";
	// XML node keys
	static final String KEY_ITEM = "item"; // parent node
	static final String KEY_TITLE = "title";
	static final String KEY_PUBDATE = "pubDate";
	static final String KEY_DESC = "description";
	static final String KEY_SITE = "site";
	static final String KEY_ICON = "icon";
	public ListAdapter adapter;
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
			if (mAccel > 10) { 
				Toast toast = Toast.makeText(getApplicationContext(), "Refreshing List", Toast.LENGTH_LONG);
				toast.show();

				setListAdapter(null);
				refreshList();
			}
	    }

	    public void onAccuracyChanged(Sensor sensor, int accuracy) {
	    }
  	};
  	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);


		refreshList();
		

		// selecting single ListView item
		ListView lv = getListView();

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting values from selected ListItem
				String name = ((TextView) view.findViewById(R.id.name)).getText().toString();
				String cost = ((TextView) view.findViewById(R.id.cost)).getText().toString();
				String description = ((TextView) view.findViewById(R.id.desciption)).getText().toString();
				
				// Starting new intent
				Intent in = new Intent(getApplicationContext(), SingleMenuItemActivity.class);
				in.putExtra(KEY_TITLE, name);
				in.putExtra(KEY_PUBDATE, cost);
				in.putExtra(KEY_DESC, description);
				startActivity(in);

			}
		});
		
		 mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		    mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		    mAccel = 0.00f;
		    mAccelCurrent = SensorManager.GRAVITY_EARTH;
		    mAccelLast = SensorManager.GRAVITY_EARTH;
	}
	
	public void refreshList(){
		ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();
		
		XMLParser parser = new XMLParser();
		String xml = parser.getXmlFromUrl(URL); // getting XML
		String xml2 = parser.getXmlFromUrl(URL2);
		Document doc = parser.getDomElement(xml); // getting DOM element
		NodeList nl = doc.getElementsByTagName(KEY_ITEM);
		// looping through all item nodes <item>
		//Google
		for (int i = 0; i < nl.getLength(); i++) {
			// creating new HashMap
			HashMap<String, String> map = new HashMap<String, String>();
			Element e = (Element) nl.item(i);
			// adding each child node to HashMap key => value
			map.put(KEY_TITLE, parser.getValue(e, KEY_TITLE));
			String Date = parser.getValue(e, KEY_PUBDATE);
			String trueDate = Date.substring(5, 16);
			String trueTime = Date.substring(17, 25);
			//stupid math to make the String into a usable number
			trueTime = trueTime.replaceAll("[^0-9]", "");
			map.put(KEY_PUBDATE, Date);
			map.put("TRUE_DATE", trueDate);
			map.put("TRUE_TIME", trueTime);
			map.put(KEY_DESC, parser.getValue(e, KEY_DESC));
			map.put(KEY_ICON, "@drawable/google");
			map.put(KEY_SITE, "Google");

			// adding HashList to ArrayList
			menuItems.add(map);
		}
		doc = parser.getDomElement(xml2); // getting DOM element

		nl = doc.getElementsByTagName(KEY_ITEM);
		// looping through all item nodes <item>
		//yahoo
		for (int i = 0; i < nl.getLength(); i++) {
			// creating new HashMap
			HashMap<String, String> map = new HashMap<String, String>();
			Element e = (Element) nl.item(i);
			// adding each child node to HashMap key => value
			map.put(KEY_TITLE, parser.getValue(e, KEY_TITLE));
			String Date = parser.getValue(e, KEY_PUBDATE);
			String trueDate = Date.substring(5, 16);
			String trueTime = Date.substring(17, 25);
			//stupid math to make the String into a usable number
			trueTime = trueTime.replaceAll("[^0-9]", "");
			map.put(KEY_PUBDATE, Date);
			map.put("TRUE_DATE", trueDate);
			map.put("TRUE_TIME", trueTime);
			map.put(KEY_DESC, parser.getValue(e, KEY_DESC));
			map.put(KEY_ICON, "@drawable/yahoo");
			map.put(KEY_SITE, "Yahoo");
			
			//SORT THE NEW ITEMS INTO THE ARRAYLIST
			for(int j = 0; j < menuItems.size(); j++){
				//check if dates match
				String test = menuItems.get(j).get("TRUE_DATE");
				if(test.equals(trueDate)){
					//check if the time of the current article is NEWER (i.e. greater) than the article at the current element
					int foo = Integer.parseInt(menuItems.get(j).get("TRUE_TIME"));
					int bar = Integer.parseInt(trueTime);
					if(foo < bar){
					// adding HashList to ArrayList
						menuItems.add(j, map);
						j = menuItems.size() + 2;
					}
				}
			}
		}
		//sort the hashmap
		
		
		// Adding menuItems to ListView
		adapter = new SimpleAdapter(this, menuItems,
				R.layout.list_item,
				new String[] { KEY_TITLE, KEY_PUBDATE, KEY_DESC, KEY_SITE }, new int[] {
						R.id.name, R.id.cost, R.id.desciption, R.id.site });

		setListAdapter(adapter);
	}
}