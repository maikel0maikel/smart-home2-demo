package com.nd.hilauncherdev.myphone.nettraffic.util;



import java.text.SimpleDateFormat;
import java.util.Date;

import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficBytesItem;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class CrashTool {
	
	/**
	 * 网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo[] info = mgr.getAllNetworkInfo();
	    if (info != null) {
	    	for (int i = 0; i < info.length; i++) {
	    		if (info[i].getState() == NetworkInfo.State.CONNECTED) {
	    			return true;
	    		}
	    	}
	    }
	    
		return false;
	}
	
	//判断手机当前上网用的是sim卡还是wifi
	public static boolean isWifiNetwork(Context mContext) {
        boolean isWifiConnect = false;
        ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        //check the networkInfos numbers
        NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
        for (int i = 0; i<networkInfos.length; i++) {
            if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
               if(networkInfos[i].getType() == ConnectivityManager.TYPE_MOBILE) {
                   isWifiConnect = false;
               }
               if(networkInfos[i].getType() == ConnectivityManager.TYPE_WIFI) {
                   isWifiConnect = true;
               }
            }
        }
        return isWifiConnect;
    }
	
	//判断网络连接类型，只有在3G或wifi里进行一些数据更新。
	//http://www.douban.com/note/153116265/
	//http://www.cnblogs.com/luxiaofeng54/archive/2011/03/01/1968063.html
	//是否漫游 isNetworkRoaming
	public static boolean is3GNetwork(Context mContext) {
        boolean is3GConnect = false;
        ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mTelephony = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);  
        //check the networkInfos numbers
        NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
        for (int i = 0; i<networkInfos.length; i++) {
            if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
               if(networkInfos[i].getType() == ConnectivityManager.TYPE_MOBILE
            		   && networkInfos[i].getSubtype()== TelephonyManager.NETWORK_TYPE_UMTS && !mTelephony.isNetworkRoaming() ) {
                   is3GConnect = true;
               }               
            }
        }
        return is3GConnect;
    }

	private static Toast netStateToast;
	
	
	/*
	 * 解决 Toast 重复显示问题
	 */
	public static void toastShow(Context mContext, int resId) {
		String tipStr = mContext.getString(resId);
		if (netStateToast == null) {
			netStateToast = Toast.makeText(mContext, tipStr, Toast.LENGTH_SHORT);
		} else {
			netStateToast.cancel();
			netStateToast.setText(tipStr);
		}
		netStateToast.show();
	}
	
    public static int getNetType(Context ctx){
    	
    	int iResult = NetTrafficBytesItem.DEV_WIFI;
    	ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
		if (mobile.getState()==NetworkInfo.State.CONNECTED && wifi.getState()!=NetworkInfo.State.CONNECTED) {
			iResult = NetTrafficBytesItem.DEV_GPRS;
		}else{
			iResult = NetTrafficBytesItem.DEV_WIFI;
		}
		
		return iResult;
    }
    
    public static String getStringDate() {
        Date currentTime = new Date();
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }
    
    public static String getStringMonth() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
        String dateString = formatter.format(currentTime);
        return dateString;
    }
}
