package com.nd.hilauncherdev.myphone.nettraffic.db;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

/**
 * 流量排行Item
 * @author cfb
 */
public class NetTrafficRankingItem {

	/**记录ID 自动增加*/
	public int id;
	
	/**类型标识  0：2G、3G流量   1：Wifi流量*/
	public int dev;
	
	/**软件包名*/
	public String pkg;
	
	/**接收字节数 rx、tx在数据库中已 KB单位存放*/
	public float rx;
	
	/**发送字节数*/
	public float tx;
	
	/**日期*/
	public String date;
	
	/**数据批次标示   每次手机重启后重新启用一个记录编号(递增)*/
	public int data_id;
	
	/**系统分配给App的用户ID*/
	public int uid;
	
	/**软件名称*/
	public String names;
	
	//用于独立统计Wifi及Gprs累计信息
	/**累计接收Wifi*/
	public float sumWifiRx = 0;
	
	/**累计发送Wifi*/
	public float sumWifiTx = 0;
	
	/**累计接收Gprs*/
	public float sumGprsRx = 0;
	
	/**累计发送Gprs*/
	public float sumGprsTx = 0;
	
	//排行列表展示使用
	/**流量总字节数 */
	public float tal;
	public ApplicationInfo appinfo;	
	public Drawable cached_icon;	
	public boolean icon_loaded = false;
}
