package com.example.finalwifi;

import hu.edudroid.interfaces.AsyncMethodException;
import hu.edudroid.interfaces.BasePlugin;
import hu.edudroid.interfaces.MethodNotSupportedException;
import hu.edudroid.interfaces.PluginResult;
import hu.edudroid.interfaces.Quota;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

public class WifiPlugin extends BasePlugin
{

	private static final List<String> methods;
	private static final List<String> events;
	private static List<Quota> quotas;

	private static final String TAG = WifiPlugin.class.getName();

	static {
		List<String> tmpMethods = new ArrayList<String>();
		List<String> tmpEvents = new ArrayList<String>();
		List<Quota> tmpQuotas = new ArrayList<Quota>();

		tmpEvents.add(WifiConstants.EVENT_SPEED_TEST);
		tmpEvents.add(WifiConstants.EVENT_RSSI_CHANGED);
		tmpEvents.add(WifiConstants.EVENT_GET_SIGNAL_LEVEL);
		tmpEvents.add(WifiConstants.EVENT_GET_NETWORKINFO);
		tmpEvents.add(WifiConstants.EVENT_GET_NETWORK_PARAMETERS);
		tmpEvents.add(WifiConstants.EVENT_CHANGE_WIFI_STATE);
		
		tmpMethods.add(WifiConstants.METHOD_DO_SPEED_TEST);		
		tmpMethods.add(WifiConstants.METHOD_AVAILABLE_NETWORKS);
		tmpMethods.add(WifiConstants.METHOD_CHANGE_WIFI_STATE);
		tmpMethods.add(WifiConstants.METHOD_GET_NETWORK_PARAMETERS);
		tmpMethods.add(WifiConstants.METHOD_START_TIMER);
		
		quotas = Collections.unmodifiableList(tmpQuotas);
		methods = Collections.unmodifiableList(tmpMethods);
		events = Collections.unmodifiableList(tmpEvents);
	}
	
	public WifiPlugin()
	{
		super(WifiConstants.PLUGIN_NAME, WifiPlugin.class.getPackage().getName(), WifiListener.class.getName(), WifiConstants.PLUGIN_AUTHOR,
				WifiConstants.PLUGIN_DESCRIPTION, WifiConstants.VERSION_CODE, events, methods, quotas);
	}
	
	@Override
	public PluginResult callMethodSync(long callId, String method,
			Map<String, Object> parameters, Map<Long, Double> quotaLimits,
			Object context) throws AsyncMethodException,
			MethodNotSupportedException {
					if(method.equals(WifiConstants.METHOD_DO_SPEED_TEST))
					{
						Log.e(TAG, "USER selected method for doing a speedtest.");
						Intent intentForSpeedTest = new Intent();
						intentForSpeedTest.setAction(WifiConstants.INTENT_SPEED_TEST);
						((Context) context).sendBroadcast(intentForSpeedTest);			
						
						throw new AsyncMethodException();
					}
					else if(method.equals(WifiConstants.METHOD_AVAILABLE_NETWORKS))
					{
						Log.e(TAG, "USER selected method for getting info about the nearby networks.");
						WifiManager wifimanager = (WifiManager)((Context)context).getSystemService(Context.WIFI_SERVICE);
						wifimanager.startScan();
						
						throw new AsyncMethodException();
					}
					else if(method.equals(WifiConstants.METHOD_CHANGE_WIFI_STATE))
					{
						Log.e(TAG, "USER selected method for changing wifi state.");
						Intent intent = new Intent();
						intent.setAction(WifiConstants.INTENT_CHANGE_WIFI_STATE);
						((Context) context).sendBroadcast(intent);
						
						throw new AsyncMethodException();
					}
					else if(method.equals(WifiConstants.METHOD_GET_NETWORK_PARAMETERS))
					{
						Log.e(TAG, "USER selected method for getting information about the connected network.");
						
						Intent intent = new Intent();
						intent.setAction(WifiConstants.INTENT_GET_NETWORK_PARAMETERS);
						((Context) context).sendBroadcast(intent);
						
						throw new AsyncMethodException();
					}
					else if(method.equals(WifiConstants.METHOD_START_TIMER))
					{
						Log.e(TAG, "USER selected method for getting RSSI information in every second.");
						((Context) context).startService(new Intent((Context) context, WifiService.class)); 
						//Originally the service was started in the listener
						throw new AsyncMethodException();
					}
					else return null;
			}
	
	public static Map<String, Object> displayNetworks(Context context)
	{																		
		Map<String, Object> values = new HashMap<String, Object>();
		
		WifiManager mainWifiObj = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

//	    mainWifiObj.startScan();
	    
	    List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
     
        for(int i = 0; i < wifiScanList.size(); i++)
        {
           values.put( WifiConstants.AVAILABLE_NETWORK, ((wifiScanList.get(i)).toString()) );
        }

		return values;
	}
	
	//WIFI STATE CHANGE METHOD
	public static void changeWifiState(Context context)
	{
		WifiManager wifimanager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		
		if(wifimanager.isWifiEnabled()==false)
		{
			wifimanager.setWifiEnabled(true);
		}
		else 
			wifimanager.setWifiEnabled(false);
	}
	

	//SIGNAL LEVEL 
	public static Map<String, Object> calculateWifiSignal(Context context )
	{																		
		Map<String, Object> values = new HashMap<String, Object>();
		
		WifiManager wifimanager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		values.put( WifiConstants.SIGNAL_LEVEL, WifiManager.calculateSignalLevel(wifimanager.getConnectionInfo().getRssi(), 101) );
		
		return values;
	}

	//NETWORK PARAMETERS
//	@SuppressWarnings("deprecation")
	public static Map<String, Object> networkParameters(Context context)
	{																		
		Map<String, Object> values = new HashMap<String, Object>();
		
		WifiManager wifimanager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiinfo=wifimanager.getConnectionInfo();

		values.put( WifiConstants.NETWORK_SSID, wifiinfo.getSSID() );
		values.put( WifiConstants.NETWORK_ID, wifiinfo.getNetworkId() );
		values.put( WifiConstants.NETWORK_IP, Formatter.formatIpAddress(wifiinfo.getIpAddress())); 
		
		values.put( WifiConstants.NETWORK_MAC,wifiinfo.getMacAddress() );
		values.put( WifiConstants.NETWORK_LINKSPEED , wifiinfo.getLinkSpeed() );
		values.put( WifiConstants.SIGNAL_LEVEL, WifiManager.calculateSignalLevel(wifimanager.getConnectionInfo().getRssi(), 101));
		
		return values;
	}
	
	//SPEED TEST
	public static Map<String, Object> speedTest(String imgurl)
	{
		
		Map<String, Object> value = new HashMap<String, Object>();
		
		try {
			URL url = new URL(imgurl);
			
			long startTime = System.currentTimeMillis();
			double imgSize = 1.54*1024;
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			
			Bitmap myBitmap = BitmapFactory.decodeStream(input); // szerintem ez nem kell
			
			long time = ( (System.currentTimeMillis() - startTime) / 1000);
			double speed = imgSize / ( (double)time);
			
			value.put("Measured speed: ", speed);
			
			return value;
			
			} catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
	}

	@Override
	public Map<Long, Double> getCostOfMethod(String method, Map<String, Object> parameters) {
		return null;
	}
	
	
	
	
}
