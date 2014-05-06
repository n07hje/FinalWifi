package com.example.finalwifi;

import hu.edudroid.ictplugin.PluginCommunicationInterface;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class WifiService extends Service {

	private static final String TAG = WifiService.class.getName();
	
	PluginCommunicationInterface communicationInterface = new PluginCommunicationInterface(new WifiPlugin());

	Timer timer = new Timer();

	private TimerTask timertask = new TimerTask() {

		@Override
		public void run() {
			Intent i = new Intent();
			i.setAction(WifiConstants.INTENT_MEASURE_SIGNAL_LEVEL_NOW);
			sendBroadcast(i);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "Signal measuring service started.(OnCreate method.)");
	}

	@Override
	public void onDestroy() {
		timer.cancel();
		Log.e(TAG, "WifiPlugins Timer cancelled. ");
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		super.onStartCommand(intent, flags, startId);
		Log.e(TAG, "Signal measuring service started. Timer started with 2 sec delay and 1 sec period (onStartCommand).)");
		
		timer.scheduleAtFixedRate(timertask, 2000, 1000);
		return START_STICKY;
	}

}
