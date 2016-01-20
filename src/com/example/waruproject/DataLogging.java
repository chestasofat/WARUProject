package com.example.waruproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class DataLogging extends Activity implements OnClickListener,
		LocationListener {

	public TextView txt, result;
	Intent x;
	Button btn;
	Button stopBtn;
	ConnectivityManager connManager;
	NetworkInfo mWifi;
	BroadcastReceiver conFin;
	static ArrayList<storeHouse> store = new ArrayList<storeHouse>();
	Location location; // location
	double latitude = 0.00; // latitude
	double longitude = 0.00;
	LocationManager locationManager;
	boolean isGPSEnabled = false;
	static String ip = "115.248.81.147";
	//static int stopButount = 0;

	private long startTime = 0L;

	private Handler customHandler = new Handler();

	long timeInMilliseconds = 0L;

	long timeSwapBuff = 0L;

	long updatedTime = 0L;

	// flag for network status
	boolean isNetworkEnabled = false;

	boolean canGetLocation = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_logging_layout);
		// alarmTrigger();
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast,
				(ViewGroup) findViewById(R.id.toast_layout_root));

		ImageView image = (ImageView) layout.findViewById(R.id.image);
		image.setImageResource(R.drawable.winner);
		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText("Welcome to the Application");

		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
		result = (TextView) findViewById(R.id.result);
		result.setText("00:00:00");

		txt = (TextView) findViewById(R.id.WifiDetails);
		txt.setText("");

		btn = (Button) findViewById(R.id.button1);
		btn.setVisibility(View.INVISIBLE);

		stopBtn = (Button) findViewById(R.id.stopButton);
		stopBtn.setVisibility(View.INVISIBLE);

		connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			Toast.makeText(getApplicationContext(), "Wifi Connected",
					Toast.LENGTH_LONG).show();

			btn.setVisibility(View.VISIBLE);
			stopBtn.setVisibility(View.VISIBLE);
			txt.setText("Click button for Wifi details");

			btn.setOnClickListener(this);
			stopBtn.setOnClickListener(this);
		} else {
			Toast.makeText(getApplicationContext(),
					"No Wifi Connections Available", Toast.LENGTH_LONG).show();
		}

	}

	public void alarmTrigger() {
		Calendar cal = Calendar.getInstance();
		Log.i("ALARM", "Inside alarm");
		Intent intent = new Intent(this, AlarmReceiver.class);
		PendingIntent pintent = PendingIntent.getService(this, 1234, intent, 0);

		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		// schedule for every 30 seconds
		// alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
		// 3 * 1000, pintent);
		alarm.setRepeating(AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime(), 2 * 60 * 60, pintent);
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

	@Override
	public void onClick(View arg0) {

		if (R.id.button1 == arg0.getId()) {
			
			//stopButount = 1;

			startTime = SystemClock.uptimeMillis();

			customHandler.postDelayed(updateTimerThread, 0);
/**
			x = registerReceiver(new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					conFin = this;
					// WifiInfo info = wifi.getConnectionInfo();

					WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
					// locationManager =
					// (LocationManager)getSystemService(Context.LOCATION_SERVICE);
					// int level = WifiManager.calculateSignalLevel(wifi
					// .getConnectionInfo().getRssi(), 100);
					int level = wifi.getConnectionInfo().getRssi();
					storeHouse st = new storeHouse();
					st.setSsid(wifi.getConnectionInfo().getSSID());
					st.setStrength(level);
					Date dt = new Date();
					st.setTmpst(dt);
					// location =
					// locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

					locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					// Define the criteria how to select the locatioin provider
					// -> use
					// default
					Criteria criteria = new Criteria();
					String provider = locationManager.getBestProvider(criteria,
							false);
					Location location = locationManager
							.getLastKnownLocation(provider);
					if (location != null) {

						// Location loc = getLocation();
						latitude = location.getLatitude();
						longitude = location.getLongitude();
						st.setLat(latitude);
						st.setLongd(longitude);
					} else {
						st.setLat(latitude);
						st.setLongd(longitude);
					}

					st.setBatterVal(getBatteryCurentValue());
					store.add(st);
					
					func1();
				}

			}, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
			
			
			**/
			
			int delay = 1000; // delay for 1 sec. 
			int period = 1000; // repeat every 10 sec. 
			Timer timer = new Timer(); 
			timer.scheduleAtFixedRate(new TimerTask() 
			    { 
			        public void run() 
			        { 
			        	//conFin = getApplicationContext();
						// WifiInfo info = wifi.getConnectionInfo();

						WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
						// locationManager =
						// (LocationManager)getSystemService(Context.LOCATION_SERVICE);
						// int level = WifiManager.calculateSignalLevel(wifi
						// .getConnectionInfo().getRssi(), 100);
						int level = wifi.getConnectionInfo().getRssi();
						storeHouse st = new storeHouse();
						st.setSsid(wifi.getConnectionInfo().getSSID());
						st.setStrength(level);
						Date dt = new Date();
						st.setTmpst(dt);
						

						st.setBatterVal(getBatteryCurentValue());
						store.add(st);
						
						func1();
			        } 
			    }, delay, period);  
			
			
			
			
			
			

		}
		
		if ((R.id.stopButton == arg0.getId()) ) {

			
				Toast.makeText(getApplicationContext(), "Logging stopped",
						Toast.LENGTH_SHORT).show();
				//stopButount = 0;

				timeSwapBuff += timeInMilliseconds;

				customHandler.removeCallbacks(updateTimerThread);

				exportToExcel();
				// store.clear();
				String object = convertToJSONObject();
				androidToServer(object);
				// getApplicationContext().unregisterReceiver(conFin);
				store.clear();
				
			} 
		}

	

	private Runnable updateTimerThread = new Runnable() {

		public void run() {

			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

			updatedTime = timeSwapBuff + timeInMilliseconds;

			int secs = (int) (updatedTime / 1000);

			int mins = secs / 60;

			secs = secs % 60;

			int milliseconds = (int) (updatedTime % 1000);

			result.setText("" + mins + ":"

			+ String.format("%02d", secs) + ":"

			+ String.format("%03d", milliseconds));

			customHandler.postDelayed(this, 0);

		}

	};

	double getBatteryCurentValue() {

		Intent batteryIntent = getApplicationContext().registerReceiver(null,
				new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int rawlevel = batteryIntent.getIntExtra("level", -1);
		double scale = batteryIntent.getIntExtra("scale", -1);
		double level = -1;
		if (rawlevel >= 0 && scale > 0) {
			level = (rawlevel * 100) / scale;
		}
		return level;
	}

	private void func1() {

		
		 try {
             runOnUiThread(new Runnable() {

                 @Override
                 public void run() {
                	 WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
             		// int level = WifiManager.calculateSignalLevel(wifi.getConnectionInfo()
             		// .getRssi(), 5);
             		int level = wifi.getConnectionInfo().getRssi();
             		// txt = (TextView)arg0.findViewById(R.id.WifiDetails);
             		txt.setText("Strength " + level + "SSID: "
             				+ wifi.getConnectionInfo().getSSID());
                 }
             });
             Thread.sleep(300);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
		
		
		

	}

	File getFileName(String fileName) {

		// Saving file in external storage
		File sdCard = Environment.getExternalStorageDirectory();
		Log.i("MEMORY MAP", sdCard.getName());
		File directory = new File(sdCard.getAbsolutePath() + "/Waru");
		Log.i("MEMORY MAP", directory.getAbsolutePath());

		// create directory if not exist
		if (!directory.isDirectory()) {
			directory.mkdirs();
		}

		// file path
		File file = new File(directory, fileName);

		return file;

	}

	private void exportToExcel() {

		Date dt = new Date();
		// final String fileName = "an_" + dt + ".xls";
		final String fileName = "Log.xls";

		Log.i("ARRAYLIST", store.toString());

		File file = getFileName(fileName);
		WorkbookSettings wbSettings = new WorkbookSettings();
		wbSettings.setLocale(new Locale("en", "EN"));

		WritableWorkbook workbook;

		try {
			workbook = Workbook.createWorkbook(file, wbSettings);

			// Excel sheet name. 0 represents first sheet
			WritableSheet sheet = workbook.createSheet("Observations", 0);

			try {
				Label l00 = new Label(0, 0, "Access Point");
				sheet.addCell(l00);
				Label l10 = new Label(1, 0, "RSSI");
				sheet.addCell(l10);
				Label l20 = new Label(2, 0, "Timestamp");
				sheet.addCell(l20);
				Label l30 = new Label(3, 0, "Latitude");
				sheet.addCell(l30);
				Label l40 = new Label(4, 0, "Longitude");
				sheet.addCell(l40);
				Label l50 = new Label(5, 0, "Current Phone Battery Level");
				sheet.addCell(l50);
				int i = 1;
				Iterator<storeHouse> iterator = store.iterator();
				while (iterator.hasNext()) {
					// i = store.indexOf(iterator);
					// if (i < 3) {
					Log.i("ARRAY-EXCEL", "Exported" + i);
					storeHouse xyz = iterator.next();
					sheet.insertRow(i);
					Label l1 = new Label(0, i, xyz.getSsid());
					sheet.addCell(l1);
					Label l2 = new Label(1, i, Integer.toString(xyz
							.getStrength()));
					sheet.addCell(l2);
					Label l3 = new Label(2, i, xyz.getTmpst().toString());
					sheet.addCell(l3);
					Label l4 = new Label(3, i, String.valueOf(xyz.getLat()));
					sheet.addCell(l4);
					Label l5 = new Label(4, i, String.valueOf(xyz.getLongd()));
					sheet.addCell(l5);
					Label l6 = new Label(5, i, String.valueOf(xyz
							.getBatterVal()));
					sheet.addCell(l6);
					// Log.i("Before", "Write" + i);
					// workbook.write();
					// Log.i("After", "Write");
					i++;

					// }
				}

				Log.i("Before", "Write");
				workbook.write();
				Log.i("After", "Write");
				// i++;
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}

			// Log.i("Before", "Write");
			// workbook.write();
			// Log.i("After", "Write");

			try {
				workbook.close();
				// unregisterReceiver(receiver);
			} catch (WriteException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void androidToServer(String jsonArray) {

		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();

		client.setTimeout(90 * 1000);

		params.put("usersJSON", jsonArray);
		Log.e("tast", params + "");
		client.post("http://" + ip
				+ "/project8/datacollection_data.php", params,
				new AsyncHttpResponseHandler() {
					public void onSuccess(String response) {
						// result.setText(response);
						Log.i("RESPONSE", response);
					}

					public void onFailure(int statusCode, Throwable error,
							String content) {

					}
				});

	}

	public String convertToJSONObject() {

		ArrayList<HashMap<String, String>> wordList;
		wordList = new ArrayList<HashMap<String, String>>();
		File file = getFileName("Log.xls");
		FileInputStream inp = null;
		try {
			inp = new FileInputStream(file);
		} catch (FileNotFoundException e1) {

			e1.printStackTrace();
		}
		JSONArray jsonArray = new JSONArray();
		Workbook w = null;
		try {
			try {
				w = Workbook.getWorkbook(inp);
			} catch (IOException e) {

				e.printStackTrace();
			}
			// Get the first sheet
			Sheet sheet = w.getSheet(0);
			// Loop over first 10 column and lines
			int columns = sheet.getColumns();
			int rows = sheet.getRows();
			Cell cell;
			// storeHouse clist = new storeHouse();
			ArrayList<storeHouse> contacts = new ArrayList<storeHouse>();
			for (int j = 1; j < rows; j++) {
				HashMap<String, String> jsonlist = new HashMap<String, String>();
				cell = sheet.getCell(0, j);
				jsonlist.put("Name", cell.getContents());

				cell = sheet.getCell(1, j);
				jsonlist.put("Strength", cell.getContents());

				// contact.setTmpst(cell.getContents());

				cell = sheet.getCell(2, j);
				jsonlist.put("Timestamp", cell.getContents());

				cell = sheet.getCell(5, j);
				jsonlist.put("BatteryValue", cell.getContents());

				jsonlist.put("Email", mainScreenActivity.emailAddress.getText()
						.toString());
				jsonlist.put("UserLocation", mainScreenActivity.LocationName);

				// }
				wordList.add(jsonlist);
				// contacts.add(contact);

			}

		} catch (BiffException e) {
			e.printStackTrace();
			System.out.println("error");
		}

		System.out.println(jsonArray);
		Gson gson = new GsonBuilder().create();
		return gson.toJson(wordList);

	}

	/*
	 * public JSONArray convertToJSONObject() {
	 * 
	 * File file = getFileName("Log.xls"); FileInputStream inp = null; try { inp
	 * = new FileInputStream(file); } catch (FileNotFoundException e1) {
	 * 
	 * e1.printStackTrace(); } JSONArray jsonArray = new JSONArray(); Workbook w
	 * = null; try { try { w = Workbook.getWorkbook(inp); } catch (IOException
	 * e) {
	 * 
	 * e.printStackTrace(); } // Get the first sheet Sheet sheet =
	 * w.getSheet(0); // Loop over first 10 column and lines int columns =
	 * sheet.getColumns(); int rows = sheet.getRows(); // storeHouse clist = new
	 * storeHouse(); ArrayList<storeHouse> contacts = new
	 * ArrayList<storeHouse>(); for (int j = 1; j < rows; j++) { JSONObject
	 * jsonlist = new JSONObject(); // storeHouse contact = new storeHouse(); //
	 * for (int i = 0; i < columns; i++) {
	 * 
	 * // contact.setSsid(cell.getContents()); try { Cell cell =
	 * sheet.getCell(0, j); jsonlist.put("Name", "nw"); } catch (JSONException
	 * e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * // contact.setStrength(Integer.valueOf(cell.getContents())); try { Cell
	 * cell = sheet.getCell(1, j); jsonlist.put("Strength", cell.getContents());
	 * } catch (JSONException e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * // contact.setTmpst(cell.getContents());
	 * 
	 * try { Cell cell = sheet.getCell(2, j); jsonlist.put("Timestamp",
	 * cell.getContents()); } catch (JSONException e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * // contact.setLat(Double.valueOf((cell.getContents()))); try { Cell cell
	 * = sheet.getCell(3, j); jsonlist.put("Latitude", cell.getContents()); }
	 * catch (JSONException e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * // contact.setLongd(Double.valueOf((cell.getContents()))); try { Cell
	 * cell = sheet.getCell(4, j); jsonlist.put("Longitude",
	 * cell.getContents()); } catch (JSONException e) {
	 * 
	 * e.printStackTrace(); } //
	 * contact.setBatterVal(Float.valueOf(cell.getContents())); try { Cell cell
	 * = sheet.getCell(5, j); jsonlist.put("Battery Value", cell.getContents());
	 * } catch (JSONException e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * 
	 * try {
	 * 
	 * jsonlist.put("Email",
	 * mainScreenActivity.emailAddress.getText().toString()); } catch
	 * (JSONException e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * // } jsonArray.put(jsonlist); // contacts.add(contact);
	 * 
	 * } System.out.println("done");
	 * 
	 * 
	 * } catch (BiffException e) { e.printStackTrace();
	 * System.out.println("error"); }
	 * 
	 * System.out.println(jsonArray); return jsonArray;
	 * 
	 * }
	 */

	public String convertToJSON() {

		File file = getFileName("Log.xls");
		FileInputStream inp;
		JSONObject json = new JSONObject();
		try {
			inp = new FileInputStream(file);
			try {
				Workbook workbook = Workbook.getWorkbook(inp);
				Sheet sheet = workbook.getSheet(0);

				for (int j = 0; j < sheet.getColumns(); j++) {

					for (int i = 0; i < sheet.getRows(); i++) {
						JSONObject jRow = new JSONObject();
						JSONArray cells = new JSONArray();
						Cell cell = sheet.getCell(j, i);
						CellType type = cell.getType();
						if (type == CellType.LABEL) {
							System.out.println("I got a label "
									+ cell.getContents());
						}

						if (type == CellType.NUMBER) {
							System.out.println("I got a number "
									+ cell.getContents());
						}

					}
				}

			} catch (BiffException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		return null;

	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager) getApplicationContext()
					.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			if (isGPSEnabled) {

				Log.d("GPS Enabled", "GPS Enabled");
				if (locationManager != null) {
					location = locationManager
							.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if (location != null) {
						latitude = location.getLatitude();
						longitude = location.getLongitude();
					}
				}

			} else {
				location.setLatitude(0.00);
				location.setLongitude(0.00);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return location;

	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

}
