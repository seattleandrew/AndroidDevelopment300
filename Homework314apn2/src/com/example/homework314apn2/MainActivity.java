package com.example.homework314apn2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import android.app.AlertDialog;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private static final String TAG = MainActivity.class.getSimpleName();
    
    private static String USER_AGENT = "HelloHTTP/1.0";
	private static String weatherUrl = "http://api.openweathermap.org/data/2.5/weather?q=";
	
	private static String forecastUrlPre = "http://api.openweathermap.org/data/2.5/forecast/daily?q=";
	private static String forecastUrlPost = "&cnt=4&mode=json";
			
	private AndroidHttpClient mClient;
    private Context mContext;
    private TextView mHeaderTextView;
    private TextView mContentTextView;
    private String mURLString;
    private StringBuilder mXMLBuilder;
    private ImageView mMainImageView;
    
    private Bundle data;
    
	
    class weather {
        String temp; //the temp currently
		String main; //weather
    }
    
    class forecast {
    	String main; //weather type
    	String day; //the day the weather occurs
    }
    
    // DefaultHandler implementation
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            
            String c_temp = msg.getData().getString("c_temp");
            String c_icon = msg.getData().getString("c_icon");
            String f_temp1 = msg.getData().getString("f_temp1");
            String f_icon1 = msg.getData().getString("f_icon1");
            String f_temp2 = msg.getData().getString("f_temp2");
            String f_icon2 = msg.getData().getString("f_icon2");
            String f_temp3 = msg.getData().getString("f_temp3");
            String f_icon3 = msg.getData().getString("f_icon3");
            
            if (c_temp != null) {
            	TextView textV = (TextView) findViewById(R.id.textView2);
                textV.setText(c_temp);
            }
            if (f_temp1 != null){
            	TextView textV = (TextView) findViewById(R.id.textView3);
                textV.setText(f_temp1);
            }
            if (f_temp2 != null){
            	TextView textV = (TextView) findViewById(R.id.textView4);
                textV.setText(f_temp2);
            }
            if (f_temp3 != null){
            	TextView textV = (TextView) findViewById(R.id.textView5);
                textV.setText(f_temp3);
            }
            if (c_icon != null){
            	//fix the name
            	String text = "abc_";
            	String icon = text.concat(c_icon);
            	ImageView iv = (ImageView) findViewById(R.id.imageView1);
            	int id = getResources().getIdentifier(icon, "drawable", getPackageName());
            	iv.setImageResource(id);
            }
            if (f_icon1 != null){
            	//fix the name
            	String text = "abc_";
            	String icon = text.concat(c_icon);
            	ImageView iv = (ImageView) findViewById(R.id.imageView2);
            	int id = getResources().getIdentifier(icon, "drawable", getPackageName());
            	iv.setImageResource(id);
            }
            if (f_icon2 != null){
            	//fix the name
            	String text = "abc_";
            	String icon = text.concat(c_icon);
            	ImageView iv = (ImageView) findViewById(R.id.imageView3);
            	int id = getResources().getIdentifier(icon, "drawable", getPackageName());
            	iv.setImageResource(id);
            }
            if (f_icon3 != null){
            	//fix the name
            	String text = "abc_";
            	String icon = text.concat(c_icon);
            	ImageView iv = (ImageView) findViewById(R.id.imageView4);
            	int id = getResources().getIdentifier(icon, "drawable", getPackageName());
            	iv.setImageResource(id);
            }
        }                
    };
    
    /** An AsycTask used to update the retrieved HTTP header and content displays */
    public class WeatherAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {

        	mClient = AndroidHttpClient.newInstance(USER_AGENT);
             
			HttpResponse response = null;
			 
			String urlString = urls[0];
			 
			if (urlString == null || urlString.isEmpty()) {
				Log.e(TAG, "No valid URL string provided");
				return null;
			}
			
			Log.i(TAG, "doInBackground|urlString: " + urlString);
			 
			// Make a GET request and execute it to return the response 
			HttpGet request = new HttpGet(urlString);
			try {
				response = mClient.execute(request);
			}
			catch (Exception e) {
			     e.printStackTrace();
			}
            
            if (response == null) {
                Log.e(TAG, "Error accessing: " + urlString);
                Toast.makeText(mContext, "Error accessing: " + urlString, Toast.LENGTH_LONG).show();
                return null;
            }
            
            // Get the content
            BufferedReader bf;
            StringBuilder sb = new StringBuilder();
            try {
                bf = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 8192);
                sb.setLength(0); // Reuse the StringBuilder
                String line;
                while ((line = bf.readLine()) != null) {
                    sb.append(line);
                }
                bf.close();
            }
            catch (IllegalStateException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            
            TextView tv = (TextView) findViewById(R.id.textView1);
            
            
            Log.i(TAG, "onPostExecute() sb: " + sb.toString());
            
            
            if (tv.getText() != "") {                
                Log.d(TAG, "onPostExecute() Parsing JSON");
                
                try {
					JSONObject resultObject = new JSONObject(sb.toString());				
					JSONArray resultArray = resultObject.getJSONArray("weather");
					JSONObject weatherObject = new JSONObject(resultArray.getString(0));
					
					JSONObject mainObject = resultObject.getJSONObject("main");
					
					Log.i(TAG, "onPostExecute() mainObject: " + mainObject.toString());
					
					String weather_icon = weatherObject.getString("icon");			
					String weather_description = weatherObject.getString("description");
					int weather_temp = mainObject.getInt("temp");
					Log.i(TAG, "onPostExecute() temp: " + weather_temp);
					int C = weather_temp - 273;
					int F = ((9/5)*C) + 32;
					String current_temp = ("C: " + C + " / F : " + F);
					Log.i(TAG, "onPostExecute current_temp: " + current_temp);
					Log.i(TAG, "onPostExecute() icon: " + weather_icon + " description: " + weather_description);

					setText(weather_icon, current_temp);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
                   

            } 
            else {

            	sb.setLength(0); // Reuse the StringBuilder
            	
                // Set the text of the Content
                String body = sb.toString();                                
                
                Header[] headers = response.getAllHeaders();           
                for (Header h : headers) {
                    sb.append(h.getName() + ":" + h.getValue() + "\n");
                }
    
                // Set the text of the Header
                //mHeaderTextView.setText(sb.toString());
                
                String header = sb.toString();

            }
            
            mClient.close();
			return null;            
        }
        private void setText(String c_icon, String c_temp) {
            data = new Bundle();
            data.putString("c_temp", c_temp);
            data.putString("c_icon", c_icon);
            Log.i(TAG, "Bundle: " + data);
        }
    }
    private class ForecastAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {

        	mClient = AndroidHttpClient.newInstance(USER_AGENT);
            
			HttpResponse response = null;
			 
			String urlString = urls[0];
			 
			if (urlString == null || urlString.isEmpty()) {
				Log.e(TAG, "No valid URL string provided");
				return null;
			}
			
			Log.i(TAG, "doInBackground2|urlString: " + urlString);
			 
			// Make a GET request and execute it to return the response 
			HttpGet request = new HttpGet(urlString);
			try {
				response = mClient.execute(request);
			}
			catch (Exception e) {
			     e.printStackTrace();
			}
            
            if (response == null) {
                Log.e(TAG, "Error accessing2: " + urlString);
                Toast.makeText(mContext, "Error accessing: " + urlString, Toast.LENGTH_LONG).show();
                return null;
            }
            
            // Get the content
            BufferedReader bf;
            StringBuilder sb = new StringBuilder();
            try {
                bf = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 8192);
                sb.setLength(0); // Reuse the StringBuilder
                String line;
                while ((line = bf.readLine()) != null) {
                    sb.append(line);
                }
                bf.close();
            }
            catch (IllegalStateException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            
            TextView tv = (TextView) findViewById(R.id.textView1);
            
            
            Log.i(TAG, "onPostExecute2() sb: " + sb.toString());
            
            
            if (tv.getText() != "") {                
                Log.d(TAG, "onPostExecute2() Parsing JSON");
                
                try {
					JSONObject resultObject = new JSONObject(sb.toString());				
					JSONArray resultArray = resultObject.getJSONArray("list");
					Log.i(TAG, "onPostExecute2 resultArray: " + resultArray);
					JSONObject listObject1 = new JSONObject(resultArray.getString(1));
					Log.i(TAG, "onPostExecute2 List1: " + listObject1);
					JSONObject listObject2 = new JSONObject(resultArray.getString(2));
					JSONObject listObject3 = new JSONObject(resultArray.getString(3));
					
					JSONObject tempObject1    = listObject1.getJSONObject("temp");
					Log.i(TAG, "onPostExecute2 temp1: " + tempObject1);
					JSONArray weatherArray1 = listObject1.getJSONArray("weather");
					Log.i(TAG, "onPostExecute2 weather1: " + weatherArray1);
					JSONObject tempObject2    = listObject2.getJSONObject("temp");
					JSONArray weatherArray2 = listObject2.getJSONArray("weather");
					JSONObject tempObject3    = listObject3.getJSONObject("temp");
					JSONArray weatherArray3 = listObject3.getJSONArray("weather");
					
					//I have no idea why they save this as an array
					JSONObject weatherObject1 = new JSONObject(weatherArray1.getString(0));
					JSONObject weatherObject2 = new JSONObject(weatherArray2.getString(0));
					JSONObject weatherObject3 = new JSONObject(weatherArray3.getString(0));
					Log.i(TAG, "onPostExecute2 weatherObj1: " + weatherObject1);
					
					String      icon1 = weatherObject1.getString("icon");
					Log.i(TAG, "onPostExecute2 icon1: " + icon1);
					int weather_temp1 =    tempObject1.getInt("day");
					String      icon2 = weatherObject2.getString("icon");			
					int weather_temp2 =    tempObject2.getInt("day");
					String      icon3 = weatherObject3.getString("icon");			
					int weather_temp3 =    tempObject3.getInt("day");
					
					int C1 = weather_temp1 - 273;
					int F1 = ((9/5)*C1) + 32;
					int C2 = weather_temp2 - 273;
					int F2 = ((9/5)*C2) + 32;
					int C3 = weather_temp3 - 273;
					int F3 = ((9/5)*C3) + 32;
					Log.i(TAG, "onPostExecute2 setText: " + C1);
							
					String temp1 = ("C: " + C1 + " / F: " + F1);
					String temp2 = ("C: " + C2 + " / F: " + F2);
					String temp3 = ("C: " + C3 + " / F: " + F3);
					Log.i(TAG, "onPostExecute2 setText: " + temp1 + " + " + temp2 + " + " + temp3);					

					setText(icon1, icon2, icon3, temp1, temp2, temp3);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
                   

            } 
            else {

            	sb.setLength(0); // Reuse the StringBuilder
            	
                // Set the text of the Content
                String body = sb.toString();                                
                
                Header[] headers = response.getAllHeaders();           
                for (Header h : headers) {
                    sb.append(h.getName() + ":" + h.getValue() + "\n");
                }
    
                // Set the text of the Header
                //mHeaderTextView.setText(sb.toString());
                
                String header = sb.toString();

            }
            
            mClient.close();
			return null;            
        }
        private void setText(String f_icon1, String f_icon2, String f_icon3, String f_temp1, String f_temp2, String f_temp3) {
        	//we're assuming the bundle has been built so far
            data.putString("f_icon1", f_icon1);
            data.putString("f_icon2", f_icon2);
            data.putString("f_icon3", f_icon3);
            
            data.putString("f_temp1", f_temp1);
            data.putString("f_temp2", f_temp2);
            data.putString("f_temp3", f_temp3);
            
            Message msg = new Message();
            msg.setData(data);
            mHandler.sendMessage(msg);
        }
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new WeatherFragment()).commit();
		}
		
		mContext = this;		
	}
		

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}


	public static class WeatherFragment extends Fragment implements OnClickListener {

		public WeatherFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,	false);
			
			rootView.findViewById(R.id.button1).setOnClickListener(this);
			
			return rootView;
		}
		
		@Override
		public void onClick(View v) {
			Log.i(TAG, "onClick()");
			
			// prepare URL for current weather
			String weatherUrl2 = weatherUrl.concat((((EditText) this.getView().findViewById(R.id.editText1)).getText().toString()));	
	        ((MainActivity) this.getActivity()).new WeatherAsyncTask().execute(weatherUrl2);
	        //prepare URL for forecast weather
	        String temp_forecastUrl2 = forecastUrlPre.concat((((EditText) this.getView().findViewById(R.id.editText1)).getText().toString()));
	        String forecastUrl2 = temp_forecastUrl2.concat(forecastUrlPost);
	        Log.i(TAG, "forecastURL: " + forecastUrl2);
	        ((MainActivity) this.getActivity()).new ForecastAsyncTask().execute(forecastUrl2);
			
		}
	}
	
}
