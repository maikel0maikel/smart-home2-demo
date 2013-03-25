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
	
	/**流量排行  是否重启如果是重启则需要增加1 */
	public static final String bootCompletedRankingKey = "isBootCompletedRanking";
	public static final String bootCompletedBytesGprsKey = "isBootCompletedGprsBytes"; //流量监控 是否重启如果是重启则需要增加1  
	public static final String bootCompletedBytesWifiKey = "isBootCompletedWifiBytes"; //流量监控 是否重启如果是重启则需要增加1
	public static final String iRankingMaxIdKey = "iRankingMaxId";						//最大批次
	
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
	
	//清空统计数据	
	
    public static boolean getSetting(Context context){
    	
    	final SharedPreferences prefs = context.getSharedPreferences("float_flag", Activity.MODE_PRIVATE);
    	return prefs.getBoolean("float", false);
    }
    
    public static void setSetting(Context context, boolean floatFlag){
    	
    	final SharedPreferences prefs = context.getSharedPreferences("float_flag", Activity.MODE_PRIVATE);
    	Editor editor = prefs.edit();
    	editor.putBoolean("float", floatFlag);
    	editor.commit();
    }
    
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
}