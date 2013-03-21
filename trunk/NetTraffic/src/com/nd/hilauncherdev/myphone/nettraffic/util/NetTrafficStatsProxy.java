package com.nd.hilauncherdev.myphone.nettraffic.util;

/**
 * 流量统计代理类
 * 解决SDK2.2以下无法读取流量报错的问题
 * 
 * Cfb
 * 2012-11-6 下午9:14:43
 */
public class NetTrafficStatsProxy {

 	public static long getMobileTxPackets(){
    	
 		if ( isUp21() ){
 			return NetTrafficStats.getMobileTxPackets();
 		}
 		
    	return 0;
    }


    public static long getMobileRxPackets(){
    	
 		if ( isUp21() ){
 			return NetTrafficStats.getMobileRxPackets();
 		}
 		
    	return 0;
    }

    public static long getMobileTxBytes(){

 		if ( isUp21() ){
 			return NetTrafficStats.getMobileTxBytes();
 		}
 		
    	return 0;
    }

    public static long getMobileRxBytes(){

 		if ( isUp21() ){
 			return NetTrafficStats.getMobileRxBytes();
 		}
 		
    	return 0;
    }

    public static long getTotalTxPackets(){

 		if ( isUp21() ){
 			return NetTrafficStats.getTotalTxPackets();
 		}
 		
    	return 0;
    }

    public static long getTotalRxPackets(){

 		if ( isUp21() ){
 			return NetTrafficStats.getTotalRxPackets();
 		}
 		
    	return 0;
    }

    public static long getTotalTxBytes(){

 		if ( isUp21() ){
 			return NetTrafficStats.getTotalTxBytes();
 		}
 		
    	return 0;
    }

    public static long getTotalRxBytes(){

 		if ( isUp21() ){
 			return NetTrafficStats.getTotalRxBytes();
 		}
 		
    	return 0;
    }

    public static long getUidTxBytes(int uid){

 		if ( isUp21() ){
 			return NetTrafficStats.getUidTxBytes(uid);
 		}
 		
    	return 0;
    }

    public static long getUidRxBytes(int uid){

 		if ( isUp21() ){
 			return NetTrafficStats.getUidRxBytes(uid);
 		}
 		
    	return 0;
    }
    
    /**
     * 判断SDK是否大于2.1
     * @return boolean
     */
    @SuppressWarnings("deprecation")
	public static boolean isUp21(){
    	
    	try{
			// android.os.Build.VERSION.SDK_INT  Since: API Level 4
			// int version = android.os.Build.VERSION.SDK_INT;
			int version = Integer.parseInt(android.os.Build.VERSION.SDK);
			if (version>7) return true; 
    	}
		catch(Exception e){
			return false;
		}
    	return false;
    }
}
