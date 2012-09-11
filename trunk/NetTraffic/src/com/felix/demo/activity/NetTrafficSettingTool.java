package com.felix.demo.activity;

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
	
	private static final String PREFS_NAME_NETTRAFFIC = "NetTrafficPrefs";	//网络流量监控
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
}
