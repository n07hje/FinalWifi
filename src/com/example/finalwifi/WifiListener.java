package com.example.finalwifi;

import hu.edudroid.ictplugin.PluginCommunicationInterface;

import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiListener extends BroadcastReceiver
{
	private static final String TAG = WifiListener.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.e(TAG, "An event received. The event is : " + intent.getAction());
		PluginCommunicationInterface communicationInterface = new PluginCommunicationInterface(new WifiPlugin());
		

		//---------------------------------------------------------------------------------INTENTFILTERS--------------
	
		IntentFilter ifilterRssi = new IntentFilter(WifiManager.RSSI_CHANGED_ACTION);
		((Context) context).registerReceiver(null, new IntentFilter(ifilterRssi));
		
		IntentFilter ifilterTimer = new IntentFilter(WifiConstants.INTENT_MEASURE_SIGNAL_LEVEL_NOW);
		((Context) context).registerReceiver(null, ifilterTimer); 
		
		IntentFilter ifilterAvailableNetworks = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		((Context) context).registerReceiver(null, ifilterAvailableNetworks);
		
		IntentFilter ifilterSpeedtest = new IntentFilter(WifiConstants.INTENT_SPEED_TEST);
		((Context) context).registerReceiver(null, ifilterSpeedtest);
		
		IntentFilter ifilterNetworkParam = new IntentFilter(WifiConstants.INTENT_GET_NETWORK_PARAMETERS);
		((Context) context).registerReceiver(null, ifilterNetworkParam);
		
		//--------------------------------------------------------------------------------ACTIONS-------------
		if(intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION))
		{
			Map<String, Object> values = WifiPlugin.calculateWifiSignal((Context) context);
			Log.e(TAG, "WiFi signal level changed event received.");
			
			communicationInterface.fireEvent(WifiConstants.EVENT_RSSI_CHANGED, values, context);
		}
		else if (intent.getAction().equals(WifiConstants.INTENT_MEASURE_SIGNAL_LEVEL_NOW))//TIMER
		{
			Map<String, Object> values = WifiPlugin.calculateWifiSignal((Context) context);
			Log.e(TAG, "Timer triggered measure event.");
			
			communicationInterface.fireEvent(WifiConstants.EVENT_GET_SIGNAL_LEVEL, values, context);
		}
		else if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))//network scan
		{
			Map<String, Object> values = WifiPlugin.displayNetworks((Context) context);
			
			Log.e(TAG, "Event received for displaying info of available networks");
			communicationInterface.fireEvent(WifiConstants.EVENT_GET_NETWORKINFO, values, context);
		}
		else if(intent.getAction().equals(WifiConstants.INTENT_SPEED_TEST))//speedtest
		{
			Map<String, Object> values = WifiPlugin.speedTest("http://wonderfulengineering.com/wp-content/uploads/2013/12/hd-wallpaper-desktop.jpg");
			
			Log.e(TAG, "Event for getting network speed.");
			communicationInterface.fireEvent(WifiConstants.EVENT_SPEED_TEST, values, context);
		}
		else if(intent.getAction().equals(WifiConstants.INTENT_GET_NETWORK_PARAMETERS))//network parameters
		{
			Map<String, Object> values = WifiPlugin.networkParameters((Context) context);
			
			Log.e(TAG, "Event  for getting network speed.");
			communicationInterface.fireEvent(WifiConstants.EVENT_GET_NETWORK_PARAMETERS, values, context);
		}
		else if(intent.getAction().equals(WifiConstants.INTENT_CHANGE_WIFI_STATE))//wifi enable-disable
		{
			Log.e(TAG, "Event  : Changing the state of the wifi.");
			WifiPlugin.changeWifiState((Context) context);
			
			communicationInterface.fireEvent(WifiConstants.EVENT_CHANGE_WIFI_STATE, null, context);
			
		}
		else 
		{
			communicationInterface.onReceive(context, intent);
		}
	}
	

}
