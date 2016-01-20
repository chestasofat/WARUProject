package com.example.waruproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub

		Log.i("ALARM", "Received");
		Toast.makeText(arg0, "I'm running", Toast.LENGTH_SHORT).show();

	}

}
