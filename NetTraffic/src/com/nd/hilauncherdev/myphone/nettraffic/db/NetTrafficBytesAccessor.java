package com.nd.hilauncherdev.myphone.nettraffic.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.TrafficStats;
import android.util.Log;

import com.nd.hilauncherdev.myphone.nettraffic.util.NetTrafficInitTool;
import com.nd.hilauncherdev.myphone.nettraffic.util.NetTrafficSettingTool;

/**
 * 流量监控数据存储
 * @author cfb
 */
public class NetTrafficBytesAccessor {

    private static final String TAG = "NetTrafficBytesAccessor";
	
	private static final String T_NETTRAFFIC_BYTES = "NetTrafficBytes";
	
	/**实时  今天流量*/
	public static NetTrafficBytesResult netTrafficGprsResult = new NetTrafficBytesResult();
	/**实时 本月流量*/
	public static NetTrafficBytesResult netTrafficWifiResult = new NetTrafficBytesResult();
	
	public static int GPRS_DATA_ID = -1;
	public static int WIFI_DATA_ID = -1;
	
	
	private Context ctx;	

	private NetTrafficBytesAccessor(Context ctx){
		this.ctx = ctx;
	}	

	static private NetTrafficBytesAccessor accessor; 
	
	public static NetTrafficBytesAccessor getInstance(Context context){

		if(accessor == null){
			accessor = new NetTrafficBytesAccessor(context);
		}
		return accessor;
	}
	
	private NetTrafficBytesItem buildNetTrafficBytesItem(Cursor c) {
		
		NetTrafficBytesItem ret = new NetTrafficBytesItem();  
		ret.id = c.getInt(0);
		ret.dev = c.getInt(1); 
		ret.rx = c.getFloat(2);
        ret.tx = c.getFloat(3);
        ret.date = c.getString(4);
        ret.data_id = c.getInt(5);        
		return ret; 
	}
	
	/**
	 * 流量监控统计组装
	 * @param c
	 * @return
	 */
	private NetTrafficBytesItem buildNetTrafficBytesItemForSum(Cursor c) {
		
		NetTrafficBytesItem ret = new NetTrafficBytesItem();  
		ret.rx = c.getFloat(0);
        ret.tx = c.getFloat(1);
        ret.tal = c.getFloat(2);
        ret.date = c.getString(3);
		return ret; 
	}
	
	/**
	 * 查询某种流量的最后一次监控流量
	 * @param dev
	 * @param data_id
	 * @param date
	 * @return
	 */
	public NetTrafficBytesItem getNetTrafficBytesItem(int dev, int data_id, String date){
        
		NetTrafficBytesItem ret = null;
		NetTrafficByteDB db = null;
		Cursor c = null;
		try {
			db = new NetTrafficByteDB(ctx);
	        c = db.query("select * from "+T_NETTRAFFIC_BYTES+" where dev=? and data_id=? and date=?", new String[] {dev+"", data_id+"", date+""});        
	        if(c.moveToFirst()) {            
	            ret = buildNetTrafficBytesItem(c);
	        }
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(c!=null){
				c.close();
			}
			if(db!=null){
				db.close();
			}
		}
        return ret;	
	}

	/**
	 * 增加或修改某种流量的监控流量
	 * @param item
	 * @return
	 */
	public boolean updateNetTrafficBytesItem(NetTrafficBytesItem item){
		
		boolean bResult = true; 
		ContentValues values = new ContentValues();		
		values.put("dev", item.dev);
		values.put("rx", item.rx);
		values.put("tx", item.tx);
		values.put("date", item.date);
		values.put("data_id", item.data_id);
						
		NetTrafficByteDB db = null;
		try {
			db = new NetTrafficByteDB(ctx);
			if(this.getNetTrafficBytesItem(item.dev, item.data_id, item.date)== null){			
				bResult = db.insertOrThrow(T_NETTRAFFIC_BYTES, null, values)>0;			
			}else{
				bResult = db.update(T_NETTRAFFIC_BYTES, values, "dev=? and data_id=? and date=?", new String[] {item.dev+"", item.data_id+"", item.date+""})>0;		
			}	
		} catch (Exception e) {
			bResult = false;
			e.printStackTrace();
		} finally{
			if(db!=null){
				db.close();
			}
		}
			
		return bResult;
	}
	
	/**
	 * 获取某类某天最大的数据批次数据   按此方式获取max_id可以统计出一天开关了多少次Wifi 
	 * @param dev
	 * @param date
	 * @return
	 */
	public int getMaxDataID(int dev, String date) {
		
		int maxID = 0;
		NetTrafficByteDB db = null;
		Cursor c = null;
		try {
			db = new NetTrafficByteDB(ctx);
			c = db.query("select max(data_id) from "+ T_NETTRAFFIC_BYTES+" where dev=? and date=?", new String[] {dev+"", date+""});
	        if (c.moveToFirst()) {            
	        	maxID= c.getInt(0);
	        }
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(c!=null){
				c.close();
			}
			if(db!=null){
				db.close();
			}
		}
		return maxID;
	}
	
	
	public int getDataID(int dev, String date){
    	
    	//GPRS_DATA_ID 初始化后，只有在重启手机或者重启应用是才需要重新初始化
		if ( NetTrafficBytesItem.DEV_GPRS==dev ) {
			
			if (GPRS_DATA_ID==-1){
	    		
				int maxID = getMaxDataID(dev, date);
				boolean bBoot = NetTrafficSettingTool.getPrefsBoolean(ctx, NetTrafficSettingTool.bootCompletedBytesGprsKey, false);
				GPRS_DATA_ID = maxID;
				if ( bBoot ){ 
					GPRS_DATA_ID = maxID+1;
					NetTrafficSettingTool.setPrefsBoolean(ctx, NetTrafficSettingTool.bootCompletedBytesGprsKey, false);
				}
	    	}
			return GPRS_DATA_ID;
		}
		
    	//WIFI_DATA_ID 初始化后，只有在重启手机或者Wifi重启是才需要重新初始化(需要在Wifi切换时将WIFI_DATA_ID设置为-1,并登记PrefsKey)
		if ( NetTrafficBytesItem.DEV_WIFI==dev ) {
			
			if (WIFI_DATA_ID==-1){
    			
    			int maxID = getMaxDataID(dev, date);
				boolean bBoot = NetTrafficSettingTool.getPrefsBoolean(ctx, NetTrafficSettingTool.bootCompletedBytesWifiKey, false);
				WIFI_DATA_ID = maxID;
				if ( bBoot ){ 
					WIFI_DATA_ID = maxID+1;
					NetTrafficSettingTool.setPrefsBoolean(ctx, NetTrafficSettingTool.bootCompletedBytesWifiKey, false);
				}
	    	}
			return WIFI_DATA_ID;
		}
    	
    	return -1;
    }
	
	/**
	 * 根据时间区间  按日期返回每天的流量
	 * 按月        where  date like '201206%'
	 * 按区间    where date>='20120601' and date<='20120631'		
	 * @param dev
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public ArrayList<NetTrafficBytesItem> getAllNetTrafficBytes(int dev, String beginDate, String endDate){

		String sql = ""
				+ "SELECT Sum(rx)               rx_tal, "
				+ "       Sum(tx)               tx_tal, "
				+ "       ( Sum(rx) + Sum(tx) ) all_tal, "
				+ "       date "
				+ "FROM   "+T_NETTRAFFIC_BYTES+" "
				+ "WHERE  dev = ? "
				+ "       AND date >= ? "
				+ "       AND date <= ? "
				+ "GROUP  BY date "
				+ "ORDER  BY date ";
		
		ArrayList<NetTrafficBytesItem> ret = new ArrayList<NetTrafficBytesItem>();
		NetTrafficByteDB db = null;
		Cursor c = null;
		try {
			db = new NetTrafficByteDB(ctx);
			c = db.query(sql, new String[] {dev+"", beginDate+"", endDate+""});
			while (!c.isAfterLast()) {
				NetTrafficBytesItem item = buildNetTrafficBytesItemForSum(c);
				ret.add(item);
				c.moveToNext();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(c!=null){
				c.close();
			}
			if(db!=null){
				db.close();
			}
		}
		return ret;
	}
	

	/**
	 * 当天、当月的流量
	 * month格式为 '201206'  最终执行为: where  date like '201206%'
	 * @param dev
	 * @param date
	 * @param month
	 * @return
	 */
	public NetTrafficBytesResult getDayAndMonth(int dev,String date, String month){
		
		String sqlDate = ""
				+ "SELECT ( Sum(rx) + Sum(tx) ) all_tal "
				+ "FROM   "+T_NETTRAFFIC_BYTES+" "
				+ "WHERE  dev = ? "
				+ "       AND date = ? ";
		String sqlMonth = ""
				+ "SELECT ( Sum(rx) + Sum(tx) ) all_tal "
				+ "FROM   "+T_NETTRAFFIC_BYTES+" "
				+ "WHERE  dev = ? "
				+ "       AND date like ? ";
				
		NetTrafficBytesResult result = new NetTrafficBytesResult();
		result.dev = dev;
		result.date = date;
		result.month = month;
		result.dateBytesAll = 0;
		result.monthBytesAll = 0;
		NetTrafficByteDB db = null;
		Cursor cDate = null;
		Cursor cMonth = null;
		try {
			db = new NetTrafficByteDB(ctx);
			cDate = db.query(sqlDate, new String[] {dev+"", date+""});    
			if (cDate.moveToFirst()) {    
	        	result.dateBytesAll = cDate.getFloat(0);
	        }
			
			cMonth = db.query(sqlMonth, new String[] {dev+"", month+"%"});        
	        if (cMonth.moveToFirst()) {    
	        	result.monthBytesAll = cMonth.getFloat(0);
	        }
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(cDate!=null){
				cDate.close();
			}
			if(cMonth!=null){
				cMonth.close();
			}
			if(db!=null){
				db.close();
			}
		}
		
		return result;
	}
	
	/**
	 * 记录当前的流量
	 * @param strDate
	 * @return 返回值: 流量无变化返回false 流量有变化返回true	
	 */
	public boolean insertNetTrafficBytesToDB(String strDate){

		NetTrafficInitTool.getCacheAppMap(ctx);
		
		boolean netTrafficChange = false;
		
		float currentTotalRx  = TrafficStats.getTotalRxBytes()/1024f;
		float currentTotalTx  = TrafficStats.getTotalTxBytes()/1024f;
		
		float currentMobileRx = TrafficStats.getMobileRxBytes()/1024f;
		float currentMobileTx = TrafficStats.getMobileTxBytes()/1024f;
		
		float currentWifiRx   = currentTotalRx - currentMobileRx;
		float currentWifiTx   = currentTotalTx - currentMobileTx;
		
		if ( currentMobileRx != NetTrafficInitTool.last_gprs_rx_Bytes && currentMobileRx>0.1 ) {
			netTrafficChange = insertNetTrafficGprsBytesToDB(strDate, currentMobileRx, currentMobileTx);
			Log.d(TAG, "登记GPRS流量"+" netTrafficChange="+netTrafficChange); 
		}
		
		if ( currentWifiRx != NetTrafficInitTool.last_wifi_rx_Bytes && currentWifiRx>0.1 ) {
			netTrafficChange = insertNetTrafficWifiBytesToDB(strDate, currentWifiRx, currentWifiTx);
		    Log.d(TAG, "登记Wifi流量"+" netTrafficChange="+netTrafficChange);
		}
		
		return netTrafficChange;
	}

	/**
	 * GPRS流量登记
	 * @param strDate
	 * @param currentMobileRx
	 * @param currentMobileTx
	 * @return
	 */
	private boolean insertNetTrafficGprsBytesToDB(String strDate, float currentMobileRx, float currentMobileTx){

		boolean netTrafficChange = false;
		
		NetTrafficBytesItem itemGprs = new NetTrafficBytesItem();
		itemGprs.dev 	 = NetTrafficBytesItem.DEV_GPRS;
		itemGprs.date	 = strDate;
		itemGprs.data_id = getDataID(itemGprs.dev, strDate);
		
		if ( NetTrafficInitTool.last_gprs_rx_Bytes<=currentMobileRx ){
			float addRx = currentMobileRx-NetTrafficInitTool.last_gprs_rx_Bytes; 
			NetTrafficInitTool.last_gprs_rx_Bytes = currentMobileRx;
			itemGprs.rx = addRx;
		}else{
			itemGprs.rx = 0;
			NetTrafficInitTool.last_gprs_rx_Bytes = currentMobileRx;
		}
		
		if ( NetTrafficInitTool.last_gprs_tx_Bytes<=currentMobileTx ){
			float addTx = currentMobileTx-NetTrafficInitTool.last_gprs_tx_Bytes; 
			NetTrafficInitTool.last_gprs_tx_Bytes = currentMobileTx;
			itemGprs.tx = addTx;
		}else{
			itemGprs.tx = 0;
			NetTrafficInitTool.last_gprs_tx_Bytes = currentMobileTx;
		}
		
		if (itemGprs.rx+itemGprs.tx<0.01){
			return netTrafficChange;
		}
		
		refreshNetTrafficBytesResult(netTrafficGprsResult, itemGprs.rx+itemGprs.tx);
		
		ContentValues values = new ContentValues();		
		//values.put("id", item.id); //系统自增
		values.put("dev", itemGprs.dev);
		values.put("rx", itemGprs.rx);
		values.put("tx", itemGprs.tx);
		values.put("date", itemGprs.date);
		values.put("data_id", itemGprs.data_id);
		
		NetTrafficBytesItem ret = null;
		NetTrafficByteDB db = null;
		Cursor c = null;
		try {
			db = new NetTrafficByteDB(ctx);
	        c = db.query("select * from "+T_NETTRAFFIC_BYTES+" where dev=? and data_id=? and date=?", 
	        		new String[] {itemGprs.dev+"", itemGprs.data_id+"", itemGprs.date+""});        
	        if(c.moveToFirst()) {            
	            ret = buildNetTrafficBytesItem(c);
	        }
	        
		    if (ret==null){
		    	netTrafficChange = db.insertOrThrow(T_NETTRAFFIC_BYTES, null, values)>0;	
		    }else{
		    	itemGprs.rx += ret.rx;
				values.put("rx", itemGprs.rx);
				itemGprs.tx += ret.tx;
				values.put("tx", itemGprs.tx);
				netTrafficChange = db.update(T_NETTRAFFIC_BYTES, values, "dev=? and data_id=? and date=?", new String[] {itemGprs.dev+"", itemGprs.data_id+"", itemGprs.date+""})>0;
		    }
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(c!=null){
				c.close();
			}
			if(db!=null){
				db.close();
			}
		}
		return netTrafficChange;
	}
	
	/**
	 * Wifi流量登记
	 * @param strDate
	 * @param currentWifiRx
	 * @param currentWifiTx
	 * @return
	 */
	private boolean insertNetTrafficWifiBytesToDB(String strDate, float currentWifiRx, float currentWifiTx){

		boolean netTrafficChange = false;
		
		NetTrafficBytesItem itemWifi = new NetTrafficBytesItem();	
		itemWifi.dev 	 = NetTrafficBytesItem.DEV_WIFI;
		itemWifi.date	 = strDate;
	    itemWifi.data_id = getDataID(itemWifi.dev, strDate);
		
		if ( NetTrafficInitTool.last_wifi_rx_Bytes<=currentWifiRx ){
			float addRx = currentWifiRx-NetTrafficInitTool.last_wifi_rx_Bytes; 
			NetTrafficInitTool.last_wifi_rx_Bytes = currentWifiRx;
			itemWifi.rx = addRx;
		}else{
			itemWifi.rx = 0;
			NetTrafficInitTool.last_wifi_rx_Bytes = currentWifiRx;
		}
		
		if ( NetTrafficInitTool.last_wifi_tx_Bytes<=currentWifiTx ){
			float addTx = currentWifiTx-NetTrafficInitTool.last_wifi_tx_Bytes; 
			NetTrafficInitTool.last_wifi_tx_Bytes = currentWifiTx;
			itemWifi.tx = addTx;
		}else{
			itemWifi.tx = 0;
			NetTrafficInitTool.last_wifi_tx_Bytes = currentWifiTx;
		}
		
		if (itemWifi.rx+itemWifi.tx<0.01){
			return netTrafficChange;
		}
		
	    refreshNetTrafficBytesResult(netTrafficWifiResult, itemWifi.rx+itemWifi.tx);
		
		ContentValues values = new ContentValues();		
		//values.put("id", item.id); //系统自增
		values.put("dev", itemWifi.dev);
		values.put("rx", itemWifi.rx);
		values.put("tx", itemWifi.tx);
		values.put("date", itemWifi.date);
		values.put("data_id", itemWifi.data_id);
		
		NetTrafficBytesItem ret = null;
		NetTrafficByteDB db = null;
		Cursor c = null;
		try {
			db = new NetTrafficByteDB(ctx);
	        c = db.query("select * from "+T_NETTRAFFIC_BYTES+" where dev=? and data_id=? and date=?", 
	        		new String[] {itemWifi.dev+"", itemWifi.data_id+"", itemWifi.date+""});        
	        if(c.moveToFirst()) {            
	            ret = buildNetTrafficBytesItem(c);
	        }
		    if (ret==null){
		    	netTrafficChange = db.insertOrThrow(T_NETTRAFFIC_BYTES, null, values)>0;	
		    }else{
		    	itemWifi.rx += ret.rx;
				values.put("rx", itemWifi.rx);
				itemWifi.tx += ret.tx;
				values.put("tx", itemWifi.tx);
				netTrafficChange = db.update(T_NETTRAFFIC_BYTES, values, "dev=? and data_id=? and date=?", new String[] {itemWifi.dev+"", itemWifi.data_id+"", itemWifi.date+""})>0;
		    }
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(c!=null){
				c.close();
			}
			if(db!=null){
				db.close();
			}
		}
		return netTrafficChange;
	}
	
	private void refreshNetTrafficBytesResult(NetTrafficBytesResult netTrafficBytesResult, float addValue){
		if (netTrafficBytesResult!=null){
			Log.d(TAG, "Begin 实时流量 dateBytesAll="+netTrafficBytesResult.dateBytesAll+" addValue="+addValue);			
			netTrafficBytesResult.dateBytesAll += addValue;
			netTrafficBytesResult.monthBytesAll += addValue;
			Log.d(TAG, "End   实时流量 dateBytesAll="+netTrafficBytesResult.dateBytesAll);	
		}
	}
}
