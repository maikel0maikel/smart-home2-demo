package com.nd.hilauncherdev.myphone.nettraffic.firewall;


import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import com.nd.hilauncherdev.myphone.nettraffic.util.NetTrafficUnitTool;

/**
 * 防火墙列表应用项
 * @author cfb
 */

public class FireWallAppItem {

	public ComponentName componentName;
	/**应用用户ID*/
	public int uid;
	/**应用名称*/
	public String title;
	/**WiFi状态下是否禁联网*/
	public boolean bWifiSelect;
	/**3G状态下是否禁联网*/
	public boolean b3GSelect;	
	/**总流量*/
	public float totalTraffic;
	
	ApplicationInfo appinfo;	
	Drawable cached_icon;
	/**应用图标是否已加载*/
	boolean icon_loaded = false;	
	
	/**
	 * 流量总字节数  
	 * @return
	 */	
	public String getTal(){
		
		return NetTrafficUnitTool.netTrafficUnitHandler(totalTraffic);
	}
	
}
