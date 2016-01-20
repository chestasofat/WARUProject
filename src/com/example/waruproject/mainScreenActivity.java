package com.example.waruproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class mainScreenActivity extends Activity implements
		OnClickListener, OnItemSelectedListener {

	static EditText emailAddress;
	static String LocationName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen_activity);

		emailAddress = (EditText) findViewById(R.id.editText1);
		Button bt1 = (Button) findViewById(R.id.button1);
		bt1.setOnClickListener(this);

		Spinner spinner = (Spinner) findViewById(R.id.location_spinner);
		spinner.setBackgroundColor(Color.BLACK);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.location_array, R.layout.spinner_text);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		if (emailAddress.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(),
					"Please enter a valid Email address", Toast.LENGTH_SHORT)
					.show();
		} else if (LocationName.equals("Your Current Location")) {
			Toast.makeText(getApplicationContext(),
					"Please enter your Location", Toast.LENGTH_SHORT).show();
		}

		else {
			checkWifiState();
		}

	}

	void checkWifiState() {

		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (wifi.isWifiEnabled()) {

			Intent recordDataActivity = new Intent(this, DataLogging.class);
			startActivity(recordDataActivity);

		} else {
			enableWifiGPS();

		}

	}

	void enableWifiGPS() {
		new AlertDialog.Builder(this)
				.setTitle("Enable WiFi and Location ")
				.setMessage(
						"Please enable your Wifi and Location settings")
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// continue with delete
							}
						})

				.setIcon(android.R.drawable.ic_dialog_alert).show();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		LocationName = arg0.getSelectedItem().toString();
		Log.e("Location", LocationName);

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}
