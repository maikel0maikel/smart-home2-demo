package com.nd.hilauncherdev.myphone.nettraffic.util;

import android.net.TrafficStats;

/**
 * 流量统计读取类
 * 解决SDK2.2以下无法读取流量报错的问题
 * 
 * Cfb
 * 2012-11-6 下午9:14:43
 */
class NetTrafficStats {

	public static long getMobileTxPackets(){
  
    	return TrafficStats.getMobileTxPackets();
    }


    public static long getMobileRxPackets(){
    	
    	return TrafficStats.getMobileRxPackets();
    }

    public static long getMobileTxBytes(){

    	return TrafficStats.getMobileTxBytes();
    }

    public static long getMobileRxBytes(){

    	return TrafficStats.getMobileRxBytes();
    }

    public static long getTotalTxPackets(){

    	return TrafficStats.getTotalTxPackets();
    }

    public static long getTotalRxPackets(){

    	return TrafficStats.getTotalRxPackets();
    }

    public static long getTotalTxBytes(){

    	return TrafficStats.getTotalTxBytes();
    }

    public static long getTotalRxBytes(){

    	return TrafficStats.getTotalRxBytes();
    }

    public static long getUidTxBytes(int uid){

    	return TrafficStats.getUidTxBytes(uid);
    }

    public static long getUidRxBytes(int uid){

    	return TrafficStats.getUidRxBytes(uid);
    }
}
