package com.nd.hilauncherdev.myphone.nettraffic.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


/**
 * 流量监控 设置工具
 * @author cfb
 *
 */
public class NetTrafficSettingTool {
	
	private static final String PREFS_NAME_NETTRAFFIC = "NetTrafficPrefsV3.5.5";	//网络流量监控
	
	/**是否正在关机*/
	public static boolean SHUTDOWN_FLAG = false;
	
	/**流量排行  是否重启如果是重启则需要增加1*/
	public static final String bootCompletedRankingKey = "isBootCompletedRanking";
	/**流量监控 是否重启如果是重启则需要增加1*/
	public static final String bootCompletedBytesGprsKey = "isBootCompletedGprsBytes";
	/**流量监控 是否重启如果是重启则需要增加1*/
	public static final String bootCompletedBytesWifiKey = "isBootCompletedWifiBytes";
	/**最大批次*/
	public static final String iRankingMaxIdKey = "iRankingMaxId";
	/**是否显示流量浮动框*/
	public static final String isVisualFloatKey = "isVisualFloatKey";
	public static final String TOUCH_LAST_X = "touchLastX";
	public static final String TOUCH_LAST_Y = "touchLastY";
	/**流量超标是否提醒过*/
	public static final String isWarningDayKey = "isWarningDayKey";
	public static final String isWarningMonthKey = "isWarningMonthKey";
	
	public static final String TrafficOpen = "bTrafficOpen";				//是否开启流量监控
	public static final String TrafficMaxMonth = "iTrafficMaxMonth"; 		//每月流量套餐
	public static final String TrafficDayMonth = "iTrafficDayMonth";		//每月结算日
	
	//public static final String TrafficAutoFixBytes = "iTrafficAutoFixBytes";//自动检验流量
	public static final String TrafficAutoFixBytes_Sim = "sTrafficAutoFixBytesSim";				//sim卡归属地:  格式"福州-中国移动"
	public static final String TrafficAutoFixBytes_mailtext = "sTrafficAutoFixBytesMailText";	//短信内容:	 18
	public static final String TrafficAutoFixBytes_mailto = "sTrafficAutoFixBytesMailTo";		//发送对象: 10086 
	public static final String TrafficFloatWindow = "bTrafficFloatWindow";						//是否开启流量悬浮窗
	
	//--- 流量统计提示---
	public static final String TrafficOutOfMaxAlert = "bTrafficOutOfMaxAlert";	//超额提醒 true,只有开启了这个选项,下面的提示功能才能生效
	public static final String TrafficLowAlert = "iTrafficLowAlert";			//月剩余流量预警  90%
	public static final String TrafficDayAlert = "iTrafficDayAlert";			//每日流量提醒
	public static final String TrafficNotifyAlert = "bTrafficNotifyAlert";		//在状态栏显示提醒信息(如果不设置只弹出对话框提示)
	public static final String TrafficAutoOffLine = "bTrafficAutoOffLine";		//超过套餐限额时自动断网
	
    
	public static void setPrefsBoolean(Context ctx, String sKeyName, boolean bKeyValue) {
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME_NETTRAFFIC, Activity.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putBoolean(sKeyName, bKeyValue);
		editor.commit();
	}

	public static boolean getPrefsBoolean(Context ctx, String sKeyName, boolean defValue) {
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME_NETTRAFFIC, Activity.MODE_PRIVATE);
		return prefs.getBoolean(sKeyName, defValue);
	}
	
	public static void setPrefsLong(Context ctx, String sKeyName, long iKeyValue) {
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME_NETTRAFFIC, Activity.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putLong(sKeyName, iKeyValue);
		editor.commit();
	}

	public static long getPrefsLong(Context ctx, String sKeyName, long defValue) {
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME_NETTRAFFIC, Activity.MODE_PRIVATE);
		return prefs.getLong(sKeyName, defValue);
	}
	
	public static void setPrefsFloat(Context ctx, String sKeyName, float iKeyValue) {
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME_NETTRAFFIC, Activity.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putFloat(sKeyName, iKeyValue);
		editor.commit();
	}
	
	public static float getPrefsFloat(Context ctx, String sKeyName, float defValue) {
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME_NETTRAFFIC, Activity.MODE_PRIVATE);
		return prefs.getFloat(sKeyName, defValue);
	}
}
