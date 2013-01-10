package com.nd.hilauncherdev.lib.theme.util;



import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * 功能说明: 获取手机相关参数的工具类
 * 
 * @Author 陈小冬
 * @Date 2011-1-6
 * @Version 1.0
 */
public class TelephoneUtil {
	private static final String TAG = "TelephoneUtil";

	/**
	 * 取得IMEI号
	 */
	public static String getIMEI(Context ctx) {
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	/**
	 * 取得IMSI号
	 */
	public static String getIMSI(Context ctx) {
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
		String result =  tm.getSubscriberId();
		if (result == null)
			return "";
		
		return result;
	}

	/**
	 * 获取机器名称 如 milestone
	 * 
	 * @return
	 */
	public static String getMachineName() {
		return android.os.Build.MODEL;
	}

	/**
	 * 获取字符串型的固件版本，如1.5、1.6、2.1
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getFirmWareVersion(){
		final String version_3 =  "1.5";
		final String version_4 =  "1.6";
		final String version_5 =  "2.0";
		final String version_6 =  "2.0.1";
		final String version_7 =  "2.1";
		final String version_8 =  "2.2";
		final String version_9 =  "2.3";
		final String version_10 =  "2.3.3";
		final String version_11 =  "3.0";
		final String version_12 =  "3.1";
		final String version_13 =  "3.2";
		final String version_14 =  "4.0";
		final String version_15 =  "4.0.3";
		final String version_16 =  "4.1.1";
		final String version_17 =  "4.2";
		String versionName = "";
		try{
			// android.os.Build.VERSION.SDK_INT  Since: API Level 4
			// int version = android.os.Build.VERSION.SDK_INT;
			int version = Integer.parseInt(android.os.Build.VERSION.SDK);
			switch(version){
			case 3:
				versionName = version_3;
				break;
			case 4:
				versionName = version_4;
				break;
			case 5:
				versionName = version_5;
				break;
			case 6:
				versionName = version_6;
				break;
			case 7:
				versionName = version_7;
				break;
			case 8:
				versionName = version_8;
				break;
			case 9:
				versionName = version_9;
				break;
			case 10:;
				versionName = version_10;
				break;
			case 11:
				versionName = version_11;
				break;
			case 12:
				versionName = version_12;
				break;
			case 13:
				versionName = version_13;
				break;
			case 14:
				versionName = version_14;
				break;
			case 15:
				versionName = version_15;
				break;
			case 16:
				versionName = version_16;
				break;
			case 17:
				versionName = version_17;
				break;
			default:
				versionName = version_7;
			}
		}
		catch(Exception e){
			versionName = version_7;
		}
		return versionName;
	}

	/**
	 * 获取软件版本名称
	 */
	public static String getVersionName(Context ctx) {
		String versionName = "";
		try {
			PackageInfo packageinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_INSTRUMENTATION);
			versionName = packageinfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}
	

	/**
	 * <br>Description:获取versionName
	 * <br>Author:caizp
	 * <br>Date:2011-5-13下午01:43:16
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static String getVersionName(Context context, String packageName) {
		PackageInfo pInfo = null;
		String rs = "";
		try {
			pInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
			rs = pInfo.versionName;
		} catch (Exception e) {
			e.printStackTrace();	
		}
		return rs;
	}

	/**
	 * 获取软件版本号 code
	 */
	public static int getVersionCode(Context ctx) {
		int versionCode = 0;
		try {
			PackageInfo packageinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_INSTRUMENTATION);
			versionCode = packageinfo.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 比较versionName,是否存在新版本
	 * @param newVersionName 新版本号
	 * @param oldeVersionName 	旧版本号
	 * @return 新版本号> 旧版本号  return true
	 */
	public static boolean isExistNewVersion(String newVersionName, String oldeVersionName) {
		if (oldeVersionName.toLowerCase().startsWith("v")) {
			oldeVersionName = oldeVersionName.substring(1, oldeVersionName.length());
		}
		if (newVersionName.toLowerCase().startsWith("v")) {
			newVersionName = newVersionName.substring(1, oldeVersionName.length());
		}

		if (oldeVersionName == null || newVersionName == null) {
			return false;
		}
		if (oldeVersionName.trim().length() == 0 || newVersionName.trim().length() == 0) {
			return false;
		}
		try {
			List<String> codes = parser(oldeVersionName.trim(), '.');
			List<String> versionCodes = parser(newVersionName.trim(), '.');
			for (int i = 0; i < codes.size(); i++) {
				if (i > (versionCodes.size() - 1)) {
					return false;
				}
				int a = Integer.parseInt(codes.get(i).trim());
				int b = Integer.parseInt(versionCodes.get(i).trim());
				if (a < b) {
					return true;
				} else if (a > b) {
					return false;
				}
			}
			if (codes.size() < versionCodes.size()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 2.60.3=>[2,60,3]
	 * @param value
	 * @param c
	 * @return
	 */
	private static List<String> parser(String value, char c) {
		List<String> ss = new ArrayList<String>();
		char[] cs = value.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < cs.length; i++) {
			if (c == cs[i]) {
				ss.add(sb.toString());
				sb = new StringBuffer();
				continue;
			}
			sb.append(cs[i]);
		}
		if (sb.length() > 0) {
			ss.add(sb.toString());
		}
		return ss;
	}

	/**
	 * 网络是否可用
	 */
	public synchronized static boolean isNetworkAvailable(Context context) {
		boolean result = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = null;
		if (null != connectivityManager) {
			networkInfo = connectivityManager.getActiveNetworkInfo();
			if (null != networkInfo && networkInfo.isAvailable() && networkInfo.isConnected()) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * wifi是否启动
	 */
	public static boolean isWifiEnable(Context ctx) {
		ConnectivityManager tele = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (tele.getActiveNetworkInfo() == null || !tele.getActiveNetworkInfo().isAvailable()) {
			return false;
		}
		int type = tele.getActiveNetworkInfo().getType();
		return type == ConnectivityManager.TYPE_WIFI;
	}

	/**
	 * 返回网络连接方式
	 */
	public static int getNetworkState(Context ctx) {
		if (isWifiEnable(ctx)) {
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * sim卡是否存在
	 */
	public static boolean isSimExist(Context ctx) {
		TelephonyManager manager = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
		if (manager.getSimState() == TelephonyManager.SIM_STATE_ABSENT)
			return false;
		else
			return true;
	}

	/**
	 * sd卡是否存在
	 */
	public static boolean isSdcardExist() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 返回屏幕分辨率,字符串型。如 320x480
	 */
	public static String getScreenResolution(Context ctx) {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) ctx.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		String resolution = width + "x" + height;
		return resolution;
	}

	/**
	 * 返回屏幕分辨率,数组型。
	 */
	public static int[] getScreenResolutionXY(Context ctx) {
		int[] resolutionXY = new int[2];
		if (resolutionXY[0] != 0) {
			return resolutionXY;
		}
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) ctx.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);
		resolutionXY[0] = metrics.widthPixels;
		resolutionXY[1] = metrics.heightPixels;
		return resolutionXY;
	}

	/**
	 * 返回屏幕密度
	 */
	public static float getScreenDensity(Context ctx) {
		return ctx.getResources().getDisplayMetrics().density;
	}

	/**
	 * 查询系统广播
	 */
	public static boolean queryBroadcastReceiver(Context ctx, String actionName) {
		PackageManager pm = ctx.getPackageManager();
		try {
			Intent intent = new Intent(actionName);
			List<ResolveInfo> apps = pm.queryBroadcastReceivers(intent, 0);
			if (apps.isEmpty())
				return false;
			else
				return true;
		} catch (Exception e) {
			Log.d(TAG, "queryBroadcastReceivers: " + e.toString());
			return false;
		}
	}

	/**
	 * 获取IP地址
	 */
	public static String getWifiAddress(Context ctx) {

		try {
			// 获取wifi服务
			WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
			// 判断wifi是否开启
			if (wifiManager.isWifiEnabled()) {
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				int ipAddress = wifiInfo.getIpAddress();
				String ip = intToIp(ipAddress);
				return ip;
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 获取数字型API_LEVEL 如：4、6、7
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int getApiLevel(){
		int apiLevel = 7;
		try{
			apiLevel = Integer.parseInt(android.os.Build.VERSION.SDK);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return apiLevel;
		// android.os.Build.VERSION.SDK_INT  Since: API Level 4
		//return android.os.Build.VERSION.SDK_INT;
	}
	
	public static String getCPUABI() {
		String abi = Build.CPU_ABI;
		abi = (abi == null || abi.trim().length() == 0) ? "" : abi;
		// 检视是否有第二类型，1.6没有这个字段
		try {
			String cpuAbi2 = Build.class.getField("CPU_ABI2").get(null).toString();
			cpuAbi2 = (cpuAbi2 == null || cpuAbi2.trim().length() == 0) ? null : cpuAbi2;
			if (cpuAbi2 != null) {
				abi = abi + "," + cpuAbi2;
			}
		} catch (Exception e) {
		}
		return abi;
	}

	private static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
	}

	/**
	 * 是否中文环境 
	 */
	public static boolean isZh(Context ctx) {
		Locale lo = ctx.getResources().getConfiguration().locale;
		if (lo.getLanguage().equals("zh"))
			return true;
		return false;
	}
	
	/**
	 * Log显示手机基本信息
	 * <br>Author:ryan
	 * <br>Date:2012-8-13下午08:48:47
	 */
	public static void logPhoneState() {
		Log.i(TAG, "MachineName:" + TelephoneUtil.getMachineName());
		Log.i(TAG, "Resolution WxH:" + ScreenUtil.getInstance().getScreenWH()[0] + ":" + ScreenUtil.getInstance().getScreenWH()[1]);
		Log.i(TAG, "Densit:" + ScreenUtil.getInstance().getDensity());
		Log.i(TAG, "DensityDpi:" + ScreenUtil.getInstance().getMetrics().densityDpi);
	}
	
	/**
	 * 是否拥有root权限
	 * 
	 * @return
	 */
	public static boolean hasRootPermission() {
		boolean rooted = true;
		try {
			File su = new File("/system/bin/su");
			if (su.exists() == false) {
				su = new File("/system/xbin/su");
				if (su.exists() == false) {
					rooted = false;
				}
			}
		} catch (Exception e) {
			rooted = false;
		}
		return rooted;
	}
	
	/**
	 * 获取当前语言
	 * @return
	 */
	public static String getLanguage() {
		return Locale.getDefault().getLanguage();
	}
	
	/**
	 * <br>Description: 获取手机mac地址
	 * <br>Author:caizp
	 * <br>Date:2012-8-23下午05:57:46
	 * @param ctx
	 * @return
	 */
	public static String getLocalMacAddress(Context ctx) { 
        WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE); 
        WifiInfo info = wifi.getConnectionInfo(); 
        return info.getMacAddress(); 
	}  
	
	/**
	 * <br>Description: 获取手机上网类型(cmwap/cmnet/wifi/uniwap/uninet)
	 * <br>Author:caizp
	 * <br>Date:2012-8-23下午05:59:48
	 * @param ctx
	 * @return
	 */
	public static String getNetworkTypeName(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if(null == info || null == info.getTypeName()){
			return "unknown";
		}
		return info.getTypeName();
	}

	/**
	 * 获取网络类型值，获取服务器端数据URL中需要用到
	 * @param ctx
	 * @return
	 */
	public static String getNT(Context ctx) {
		/**
		 * 0 未知
		 * 
		 * 10 WIFI网络
		 * 
		 * 20 USB网络
		 * 
		 * 31 联通
		 * 
		 * 32 电信
		 * 
		 * 53 移动
		 * 
		 * IMSI是国际移动用户识别码的简称(International Mobile Subscriber Identity)
		 * 
		 * IMSI共有15位，其结构如下： MCC+MNC+MIN MNC:Mobile NetworkCode，移动网络码，共2位
		 * 在中国，移动的代码为电00和02，联通的代码为01，电信的代码为03
		 */
		String imsi=getIMSI(ctx);
		String nt = "0";
		if (TelephoneUtil.isWifiEnable(ctx)) {
			nt = "10";
		} else if (imsi == null) {
			nt = "0" ;
		} else if (imsi.length() > 5) {
			String mnc = imsi.substring(3,5);
			if (mnc.equals("00") || mnc.equals("02")) {
				nt = "53";
			} else if (mnc.equals("01")) {
				nt = "31";
			} else if (mnc.equals("03")) {
				nt = "32";
			}
		}
		return nt;
	}
	
	/**
	 * 判断机型是否为三星S3
	 */
	public static boolean isGALAXYS3(){
		String phone = getMachineName();
		if(phone == null) 
			return false;
		
		String regEx = "I930|I939"; 
		Pattern p = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(phone);
		return m.find();
	}
}
