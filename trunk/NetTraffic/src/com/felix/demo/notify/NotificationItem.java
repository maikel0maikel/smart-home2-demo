package com.felix.demo.notify;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

public class NotificationItem {
	
	public long noId; 
	
	/**软件包名*/
	public String pkgName;
	
	/**软件名称*/
	public String appName;
	
	/**进程号*/
	public int pid;
	
	/**通知描述*/
	public String tickerText;
	
	ApplicationInfo appinfo;	
	Drawable cached_icon;	
	boolean icon_loaded = false;
}
