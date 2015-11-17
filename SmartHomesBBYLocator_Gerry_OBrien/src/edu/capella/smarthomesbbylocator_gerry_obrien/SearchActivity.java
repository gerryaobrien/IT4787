package edu.capella.smarthomesbbylocator_gerry_obrien;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SearchActivity extends ActionBarActivity {
	
	private Button mSearchButton;
	private EditText mSearchRadius;
	private EditText mZipCode;
	private ListView storeListView;
	//private Context mContext = getBaseContext();
	private static final String FILE_NAME = "stores.json";
	
	
	private String zip;
	private String radius;
	private String APIKey = "zcst9kt7crjce7emn2fbmbmf";
	private String strJSON = null;
	
	
	private BBYStore store;
	private JSONArray Stores = null;
	private ArrayList<BBYStore> bbyStores = null;
	private ArrayAdapter<BBYStore> listAdapter;
	
	private static final String TAG  = "Best Buy Locator";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
			
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_search);
		File f = getFilesDir();
		//String path = f.getAbsolutePath();
		//Toast.makeText(SearchActivity.this, path, Toast.LENGTH_LONG).show();
		
		storeListView = (ListView)findViewById(R.id.store_list);
		storeListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				BBYStore s = (BBYStore) storeListView.getItemAtPosition(position);
				Toast.makeText(SearchActivity.this, s.getShortName(), Toast.LENGTH_LONG).show();
				float lat = Float.valueOf(s.getLatitude().trim()).floatValue();
				float lng = Float.valueOf(s.getLongitude().trim()).floatValue();
				String name = s.getLongName();
				
				Intent i = new Intent(SearchActivity.this, MapActivity.class);
				i.putExtra(MapActivity.LAT, lat);
				i.putExtra(MapActivity.LNG,lng);
				i.putExtra(MapActivity.S_NAME, name);
				
				startActivity(i);
			}
		});


		mSearchRadius = (EditText)findViewById(R.id.searchRadius);
		mZipCode = (EditText)findViewById(R.id.zipCode);
		
		getPersistedData();
		
		
		mSearchButton = (Button)findViewById(R.id.searchButton);
		mSearchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		        StrictMode.setThreadPolicy(policy);
		        
				zip = mZipCode.getText().toString();
				
				radius = mSearchRadius.getText().toString();
				
				
				StringBuilder address = new StringBuilder("https://api.bestbuy.com/v1/stores((area(");
				address.append(zip);
				address.append(",");
				address.append(radius);
				address.append(")))?apiKey=");
				address.append(APIKey);
				address.append("&format=json"); 
				
				strJSON = getJSON(address.toString());
				
				createStores();
				
				// writing file here to persist data as soon as it is returned and the 
				// bbyStores array list is populated.
				// we can write this out in the onPause event as well
				try {
					writeFile();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				storeListView.requestLayout();
			}
			
		});
		
		
	}
	
	
	protected void createStores(){
		
		bbyStores = new ArrayList<BBYStore>();
		listAdapter = new ArrayAdapter<BBYStore>(this, android.R.layout.simple_list_item_1,bbyStores);
		storeListView.setAdapter(listAdapter);
		
		try {
			JSONObject jsonObj = new JSONObject(strJSON);
			
			Stores = jsonObj.optJSONArray("stores");
			
			for(int i = 0; i < Stores.length(); i++) {
				JSONObject st = Stores.getJSONObject(i);
				
				store = new BBYStore();
				store.setStoreID(st.getInt("storeId"));
				store.setShortName(st.getString("name"));
				store.setLongName(st.getString("longName"));
				store.setAddress(st.getString("address"));
				store.setAddress2(st.getString("address2"));
				store.setCity(st.getString("city"));
				store.setRegion(st.getString("region"));
				store.setLatitude(st.getString("lat"));
				store.setLongitude(st.getString("lng"));
				store.setDistance(st.getLong("distance"));
				bbyStores.add(store);
				
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
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
	
	
	public String getJSON(String address) {
		
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(address);
		
		try{
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			
			int statusCode = statusLine.getStatusCode();
			
			if(statusCode == 200){
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while((line = reader.readLine()) != null){
					builder.append(line);
				}
			}else {
				Log.e(SearchActivity.class.toString(), "Failed to get JSON object");
			}
		}
			catch(ClientProtocolException e){
				e.printStackTrace();
			}
			catch(IOException e){
				e.printStackTrace();
			}
			return builder.toString();
		}
	
	@Override
	public void onPause(){
		super.onPause();
		
		if(strJSON != null){
			try {
				writeFile();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onStart(){
		super.onStart();
	}
	
	private void getPersistedData(){
		
			try {
				strJSON = readFile();
				createStores();
				storeListView.requestLayout();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	/**
	 *	Use this method to persist the data from the user's search
 	 *	Write out the file using the strJSON content 
	 */
	protected void writeFile() throws IOException, FileNotFoundException {

		// Write the JSON to storage
		
			FileOutputStream out = openFileOutput(FILE_NAME, MODE_PRIVATE);
			out.write(strJSON.getBytes());
			out.close();	
	}
	
	
	/**
	 * Read the data back in if the file exists
	 * storing it in the bbyStores array list that is used by the 
	 * ListView displaying the results of the previous search
	 * @throws IOException, FileNotFoundException 
	 */
	protected String readFile() throws IOException, FileNotFoundException {

		
		FileInputStream in = openFileInput(FILE_NAME);
		BufferedInputStream bis = new BufferedInputStream(in);
		StringBuffer b = new StringBuffer();
		while(bis.available() != 0) {
			char c = (char)bis.read();
			b.append(c);
		}
		
		String jsonString = b.toString();
		bis.close();
		in.close();

		return jsonString;
	}

	
}


	