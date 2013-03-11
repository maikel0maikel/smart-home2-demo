package com.felix.demo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * 监听网络变化
 * 
 * @author cfb
 * 
 */

public class NetTrafficConnectivityChangeBroadcast extends BroadcastReceiver {

	protected Context mContext;

	public static final String TAG = "NetTrafficConnectivityChangeBroadcast";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (TextUtils.equals(action, WifiManager.WIFI_STATE_CHANGED_ACTION)) {

			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			int state = wifiManager.getWifiState();
			String s = "";
			switch (state) {
				case WifiManager.WIFI_STATE_ENABLED:
					s = "可用";
					break;
				case WifiManager.WIFI_STATE_DISABLED:
					s = "不可用";
					break;
				case WifiManager.WIFI_STATE_UNKNOWN:
					s = "未知";
					break;
				case WifiManager.WIFI_STATE_DISABLING:
					s = "正在关闭";
					//保存信息
					 Log.d(TAG, "正在关闭  保存信息 登记Wifi流量"); 
					NetTrafficBytesAccessor.logRealTimeTrafficBytes(context);
					
					break;
				case WifiManager.WIFI_STATE_ENABLING:
					s = "正在开启";
					break;
			}
			//Toast.makeText(context, "wifi state=" + s, Toast.LENGTH_SHORT).show();
		}
	
		if (TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION )) {
			
			//判断网络是否可用
			if ( CrashTool.isNetworkAvailable(context) ){
				//启动服务
				Intent intentService = new Intent(context, NetTrafficBytesService.class);
				intentService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startService(intentService);
			}else{
				//关闭服务
				Intent intentService = new Intent(context, NetTrafficBytesService.class);
				intentService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.stopService(intentService);
			}
			
			//判断wifi是否可用
			if ( !CrashTool.isWifiNetwork(context) ) {
				NetTrafficBytesAccessor.getInstance(context).
					setPrefsKey(NetTrafficBytesAccessor.bootCompletedBytesWifiKey, true);
				NetTrafficBytesAccessor.WIFI_DATA_ID = -1;
			}
			
			/*
			Log.d(TAG, "网络变化了");

			mContext = context;

			// 第一种
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetInfo = connectivityManager
					.getActiveNetworkInfo();
			NetworkInfo mobileNetInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			showToast("Active Network Type", activeNetInfo);
			showToast("Mobile Network Type", mobileNetInfo);
			// 第二种
			NetworkInfo networkInfo = intent
					.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			String extraInfo = intent
					.getStringExtra(ConnectivityManager.EXTRA_EXTRA_INFO);
			boolean isFailOver = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_IS_FAILOVER, false);
			boolean noConnectivity = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			NetworkInfo otherNetworkInfo = intent
					.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
			String reason = intent
					.getStringExtra(ConnectivityManager.EXTRA_REASON);
			//
			showToast("networkInfo", networkInfo);
			showToast("extraInfo", extraInfo);
			showToast("isFailOver", isFailOver);
			showToast("noConnectivity", noConnectivity);
			showToast("otherNetworkInfo", otherNetworkInfo);
			showToast("reason", reason);
			 */
		}
	}

	public void showToast(String info, Object object) {
		if (object == null) {
			return;
		}
		Toast.makeText(mContext, info + " : " + object.toString(), 0).show();
		
		Log.d(TAG, info + " : " + object.toString());
	}

}
