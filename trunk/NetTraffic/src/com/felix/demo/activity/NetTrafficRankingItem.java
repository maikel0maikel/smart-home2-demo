package com.felix.demo.activity;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

/**
 * 流量排行Item
 * Cfb
 * @author cfb
 *
 */
public class NetTrafficRankingItem {

	public int id; 			//记录ID 自动增加
	public String pkg;		//软件包名
	public float rx;		//接收字节数	 rx、tx在数据库中已 KB单位存放
	public float tx;        //发送字节数  
	public String date;     //日期
	public int data_id;	    //数据批次标示   每次手机重启后重新启用一个记录编号(递增)
	public int uid;			//用户进程ID
	public String names;	//软件名称
	
	
	//排行列表展示使用
	public float tal;        //流量总字节数  
	ApplicationInfo appinfo;	
	Drawable cached_icon;	
	boolean icon_loaded = false;
}
