package com.felix.demo.activity;

/**
 * 流量监控Item
 * Cfb
 * @author cfb
 *
 */
public class NetTrafficBytesItem {

	public int id; 			//记录ID
	public int dev;			//类型标识  0：2G、3G流量   1：Wifi流量
	public float rx;		//接收字节数	
	public float tx;       	//发送字节数 
	public String date;     //日期
	
	public int data_id;	    //数据批次标识   每次手机重启后G2/G3流量统计重新启用一个记录编号(递增)
							//		       每次Wifi关闭开启后流量统计重新启用一个记录编号(递增)	
	
	//排行列表展示使用
	public float tal;        //流量总字节数  	
	
	public static final int DEV_GPRS = 0;//类型标识  2G、3G流量
	public static final int DEV_WIFI = 1;//类型标识  Wifi流量
}
