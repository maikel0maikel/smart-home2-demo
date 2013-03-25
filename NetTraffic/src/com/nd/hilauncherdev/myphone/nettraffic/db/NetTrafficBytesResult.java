package com.nd.hilauncherdev.myphone.nettraffic.db;

public class NetTrafficBytesResult {
	
	/**网络类型: 0:GPRS 1:Wifi*/
	public int    dev;
	
	/**当天*/
	public String date;
	
	/**本月*/
	public String month;
	
	/**今天的流量*/
	public float  dateBytesAll = 0f;
	
	/**本月的流量*/
	public float  monthBytesAll = 0f;
}
