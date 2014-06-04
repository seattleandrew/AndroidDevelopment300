package com.example.homework314apn2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class MainActivity extends ActionBarActivity implements OnClickListener {

	private static final String TAG = MainActivity.class.getSimpleName();
    
    private static String USER_AGENT = "HelloHTTP/1.0";
	private String weatherUrl = "http://api.openweathermap.org/data/2.5/weather?q=";
	
	private String forecastUrlPre = "http://api.openweathermap.org/data/2.5/forecast/daily?q=";
	private String forecastUrlPost = "&cnt=4&mode=json";
			
	private AndroidHttpClient mClient;
    private Context mContext;
    private TextView mHeaderTextView;
    private TextView mContentTextView;
    private String mURLString;
    private StringBuilder mXMLBuilder;
    private ImageView mMainImageView;
	
    class weather {
        String temp; //the temp currently
		String main; //weather
    }
    
    class forecast {
    	String main; //weather type
    	String day; //the day the weather occurs
    }
    
    // DefaultHandler implementation
    public class SAXHandler extends DefaultHandler {

        public void startDocument() throws SAXException {
            mXMLBuilder.append("startDocument()\n");
        }

        public void endDocument() throws SAXException {
            mXMLBuilder.append("endDocument()\n");
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            mXMLBuilder.append("startElement() " + qName + "\n");
            
            for (int i = 0; i < attributes.getLength(); i++) {
                mXMLBuilder.append(attributes.getQName(i) + " : " + attributes.getValue(i) + "\n");
            }            
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            mXMLBuilder.append("endElement() " + qName + "\n");
        }

        public void characters(char ch[], int start, int length) throws SAXException {
            mXMLBuilder.append("characters() " + new String(ch, start, length) + "\n");
        }
    }
    
    /** An AsycTask used to update the retrieved HTTP header and content displays */
    private class WeatherAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {

        	mClient = AndroidHttpClient.newInstance(USER_AGENT);
             
             HttpResponse response = null;
             
             String urlString = urls[0];
             
             if (urlString == null) {
            	 Log.e(TAG, "No valid URL string provided");
            	 return null;
             }
             
             // Make a GET request and execute it to return the response 
             HttpGet request = new HttpGet(mURLString);
             try {
                 response = mClient.execute(request);
             }
             catch (IOException e) {
                 e.printStackTrace();
             }
            
            if (response == null) {
                Log.e(TAG, "Error accessing: " + mURLString);
                Toast.makeText(mContext, "Error accessing: " + mURLString, Toast.LENGTH_LONG).show();
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
            
            if (tv.getText() != "") {                
                Log.d(TAG, "onPostExecute() Parsing JSON");
                
                // Deserialize the JSON content
                Gson gson = new Gson();
                try {              
                    // Get a collection of our type of Book from GSON
                    Type currentType = new TypeToken<Collection<weather>>(){}.getType();
                    Collection<weather> current = gson.fromJson(sb.toString(), currentType);
                    
                    if (current != null && current.size() > 0) {
                        
                        sb.setLength(0); // Reuse the StringBuilder
                        
                        for (weather w : current) {
                            sb.append(w.main); 
                        }

                    }

                }
                catch (JsonSyntaxException e) {
                    Log.e(TAG, e.getMessage());
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
    }
    private class ForecastAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {

        	mClient = AndroidHttpClient.newInstance(USER_AGENT);
             
             HttpResponse response = null;
             
             String urlString = urls[0];
             
             if (urlString == null) {
            	 Log.e(TAG, "No valid URL string provided");
            	 return null;
             }
             
             // Make a GET request and execute it to return the response 
             HttpGet request = new HttpGet(mURLString);
             try {
                 response = mClient.execute(request);
             }
             catch (IOException e) {
                 e.printStackTrace();
             }
            
            if (response == null) {
                Log.e(TAG, "Error accessing: " + mURLString);
                Toast.makeText(mContext, "Error accessing: " + mURLString, Toast.LENGTH_LONG).show();
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
            
            if (tv.getText() != "") {                
                Log.d(TAG, "onPostExecute() Parsing JSON");
                
                // Deserialize the JSON content
                Gson gson = new Gson();
                try {              
                    // Get a collection of our type of Book from GSON
                    Type forecastType = new TypeToken<Collection<forecast>>(){}.getType();
                    Collection<forecast> current = gson.fromJson(sb.toString(), forecastType);
                    
                    if (current != null && current.size() > 0) {
                        
                        sb.setLength(0); // Reuse the StringBuilder
                        
                        for (forecast f : current) {
                            sb.append(f.day); 
                        }

                    }

                }
                catch (JsonSyntaxException e) {
                    Log.e(TAG, e.getMessage());
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
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		mContext = this;
		findViewById(R.id.button1).setOnClickListener(this);
		
	}
		

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	@Override
	public void onClick(View v) {
		Log.i(TAG, "onClick()");
		weatherUrl = weatherUrl.concat((((EditText) this.findViewById(R.id.editText1)).getText().toString()));	
        new WeatherAsyncTask().execute(weatherUrl);
		
	}

}
