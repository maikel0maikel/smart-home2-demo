package com.nd.hilauncherdev.myphone.nettraffic.receiver;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.nd.hilauncherdev.kitset.util.ThreadUtil;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficBytesAccessor;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficBytesItem;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficRankingGprsWifiAccessor;
import com.nd.hilauncherdev.myphone.nettraffic.util.CrashTool;
import com.nd.hilauncherdev.myphone.nettraffic.util.NetTrafficSettingTool;

/**
 * 监听网络变化
 * 
 * @author cfb
 */

public class NetTrafficConnectivityChangeBroadcast extends BroadcastReceiver {

	protected Context mContext;

	public static final String TAG = "NetTrafficConnectivityChangeBroadcast";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		if (TextUtils.equals(action, "netTrafficAlarm")) {
			logToFile(TAG, "每日12点定时记录=="+action);
		}
		
		logToFile(TAG, "网络变化了=="+action);
		
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
					logToFile(TAG, "正在关闭  保存信息 登记Wifi流量"); 
					NetTrafficBytesAccessor.getInstance(context).insertNetTrafficBytesToDB(CrashTool.getStringDate());
					
					break;
				case WifiManager.WIFI_STATE_ENABLING:
					s = "正在开启";
					break;
			}
			Toast.makeText(context, "wifi state=" + s, Toast.LENGTH_LONG).show();
			logToFile(TAG, "WIFI =="+"wifi state=" + s);
		}
	
		if (TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION )) {
			 
			//判断网络是否可用
			if ( CrashTool.isNetworkAvailable(context) ){
				//启动服务
				/*
				Intent intentService = new Intent(context, NetTrafficBytesService.class);
				intentService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startService(intentService);
				 */
				logToFile(TAG, " isNetworkAvailable =="+" 可用");
			}else{
				//关闭服务
				/*
				Intent intentService = new Intent(context, NetTrafficBytesService.class);
				intentService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.stopService(intentService);
				*/
				logToFile(TAG, " isNetworkAvailable =="+" 网络不可用");
			}
			
			//判断wifi是否可用
			if ( !CrashTool.isWifiNetwork(context) ) {
				NetTrafficSettingTool.setPrefsBoolean(context, NetTrafficSettingTool.bootCompletedBytesWifiKey, true);
				NetTrafficBytesAccessor.WIFI_DATA_ID = -1;
				logToFile(TAG, " isNetworkAvailable =="+" 不是 WIFI网络");
			}
			
			if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {  
				logToFile(TAG, "netWork has lost");  
	        }  
	      
	        final NetworkInfo networkInfo = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);  
	        logToFile(TAG, networkInfo.toString() + " {isConnected = " + networkInfo.isConnected() + "}");  
	        final Context ctx = context;
	        ThreadUtil.executeNetTraffic(new Runnable() {
				@Override
				public void run() {
					if (networkInfo != null && !networkInfo.isConnected()) {
						if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
							NetTrafficRankingGprsWifiAccessor.getInstance(ctx).
								insertALLAppNetTrafficToDB(NetTrafficBytesItem.DEV_GPRS, CrashTool.getStringDate());
						}
						if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
							NetTrafficRankingGprsWifiAccessor.getInstance(ctx).
								insertALLAppNetTrafficToDB(NetTrafficBytesItem.DEV_WIFI, CrashTool.getStringDate());
						}
					}							
				}
			});
		}
	}

	public void showToast(String info, Object object) {
		if (object == null) {
			return;
		}
		Toast.makeText(mContext, info + " : " + object.toString(), 0).show();
		
		Log.d(TAG, info + " : " + object.toString());
	}

	public static void logToFile(String tag, String info){
		
		Log.d(TAG, info);
		
		try {
			// 用于格式化日期,作为日志文件名的一部分
			StringBuffer sb = new StringBuffer();
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName = "netlog.log";
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				
				sb.append(time + "-" + timestamp + ":"+tag+"=="+info);
				String path = "/sdcard/NetTraffic/crash/";
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + fileName, true);
				fos.write(sb.toString().getBytes());
				fos.write(System.getProperty("line.separator").getBytes());
				fos.close();
			}
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}
	}
	
}
