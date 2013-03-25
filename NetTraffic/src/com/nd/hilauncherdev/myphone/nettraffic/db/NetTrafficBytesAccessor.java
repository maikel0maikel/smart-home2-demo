package com.nd.hilauncherdev.myphone.nettraffic.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.TrafficStats;
import android.util.Log;

import com.felix.demo.activity.NetTrafficBytesResult;
import com.nd.hilauncherdev.myphone.nettraffic.util.NetTrafficSettingTool;

/**
 * 流量监控数据存储
 * @author cfb
 */
public class NetTrafficBytesAccessor {

    private static final String TAG = "NetTrafficBytesAccessor";
	
	private static final String T_NETTRAFFIC_BYTES = "NetTrafficBytes";
	
	//最新的实时天流量和月流量
	public static NetTrafficBytesResult netTrafficBytesResult;	
	public static NetTrafficBytesResult netTrafficWifiResult;
	
	private static float last_gprs_Bytes = -1f;			//最后一次读取的gprs流量值
	private static float last_wifi_Bytes = -1f;			//最后一次读取的wifi总流量值	
	
	private static int GPRS_DATA_ID = -1;
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
		//values.put("id", item.id); //系统自增
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
	 * 清空流量监控表
	 * @return
	 */
	public boolean clearNetTrafficBytes() {
		
		boolean bResult = true; 
		NetTrafficByteDB db = null;
		try {
			db = new NetTrafficByteDB(ctx);
			bResult = db.delete(T_NETTRAFFIC_BYTES, null, null);
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

		// rx、tx在数据库中已 KB单位存放	2G/3G流量按KB形式显示所有不处理	
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
	 * 同一批次的除今天外的要扣掉,如果一样需要扣除以前的流量
	 * @param dev
	 * @param date
	 * @param data_id
	 * @return
	 */
	public NetTrafficBytesItem getBytesSumForOtherDayAndSameDataID(int dev,String date,int data_id){
		
		NetTrafficBytesItem ret = new NetTrafficBytesItem();

		NetTrafficByteDB db = null;
		db = new NetTrafficByteDB(ctx);
		// rx、tx在数据库中已 KB单位存放	2G/3G流量按KB形式显示所有不处理	
		String sql = ""
				+ "SELECT Sum(rx)               rx_tal, "
				+ "       Sum(tx)               tx_tal, "
				+ "       ( Sum(rx) + Sum(tx) ) all_tal "
				+ "FROM   "+T_NETTRAFFIC_BYTES+" "
				+ "WHERE  dev = ? "
				+ "       AND date <> ? "
				+ "       AND data_id = ? ";
		
		Cursor c = db.query(sql, new String[] {dev+"", date+"", data_id+""});
		c.moveToFirst();
        
        if (c.moveToFirst()) {    
        	ret.rx = c.getFloat(0);
            ret.tx = c.getFloat(1);
            ret.tal = c.getFloat(2);
        }

		c.close();
		db.close();
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
		
		NetTrafficBytesResult result = new NetTrafficBytesResult();
		
		NetTrafficByteDB db = null;
		db = new NetTrafficByteDB(ctx);
		//获取某天的流量
		String sqlDate = ""
				+ "SELECT ( Sum(rx) + Sum(tx) ) all_tal "
				+ "FROM   "+T_NETTRAFFIC_BYTES+" "
				+ "WHERE  dev = ? "
				+ "       AND date = ? ";
        Cursor cDate = db.query(sqlDate, new String[] {dev+"", date+""});         
        if (cDate.moveToFirst()) {    
        	result.date 		= date;
        	result.dateBytesAll = cDate.getFloat(0);
        }
        cDate.close();
                
        //获取某月的流量  
		String sqlMonth = ""
				+ "SELECT ( Sum(rx) + Sum(tx) ) all_tal "
				+ "FROM   "+T_NETTRAFFIC_BYTES+" "
				+ "WHERE  dev = ? "
				+ "       AND date like ? ";
        Cursor cMonth = db.query(sqlMonth, new String[] {dev+"", month+"%"});        
        if (cMonth.moveToFirst()) {    
        	result.month 		= month;
        	result.monthBytesAll = cMonth.getFloat(0);
        }
        cMonth.close();
        
        db.close();
		
		return result;
	}
	
	//记录当前的流量
	//返回值: 流量无变化返回false 流量有变化返回true	
	public boolean insertNetTrafficBytesToDB(String strDate){
		
		boolean netTrafficChange = false;
		
		float currentTotalRx  = TrafficStats.getTotalRxBytes()/1024f;
		float currentTotalTx  = TrafficStats.getTotalTxBytes()/1024f;
		
		Log.d("NetTrafficConnectivityChangeBroadcast TrafficStats.getTotalRxBytes()", currentTotalRx+"");
		Log.d("NetTrafficConnectivityChangeBroadcast TrafficStats.getTotalTxBytes()", currentTotalTx+"");
		
		//GPRS流量
		float currentMobileRx = TrafficStats.getMobileRxBytes()/1024f;
		float currentMobileTx = TrafficStats.getMobileTxBytes()/1024f;
		float currentMobileAll= currentMobileRx+currentMobileTx;		
		
		Log.d("NetTrafficConnectivityChangeBroadcast TrafficStats.getMobileRxBytes()", currentMobileRx+"");
		Log.d("NetTrafficConnectivityChangeBroadcast TrafficStats.getMobileTxBytes()", currentMobileTx+"");
		
		//Wifi流量
		float currentWifiRx   = currentTotalRx - currentMobileRx;
		float currentWifiTx   = currentTotalTx - currentMobileTx;
		float currentWifiAll  = currentWifiRx+currentWifiTx;
		
		//GPRS流量发生变化   登记GPRS流量		
		if ( currentMobileAll != last_gprs_Bytes && currentMobileRx<0.01 ) {
			
			NetTrafficBytesItem itemGprs = new NetTrafficBytesItem();		
			itemGprs.dev 	 = NetTrafficBytesItem.DEV_GPRS;
			itemGprs.rx  	 = currentMobileRx;
			itemGprs.tx  	 = currentMobileTx;
		    itemGprs.date	 = strDate;
		    itemGprs.data_id = getDataID(itemGprs.dev, strDate); //数据流量批次标示
		    
		    try {		    	
		    	//判断同一批次的除今天外的要扣掉,如果一样需要扣除以前的流量
		    	NetTrafficBytesItem otherDayAndSameDataIDItem = getBytesSumForOtherDayAndSameDataID(itemGprs.dev,itemGprs.date,itemGprs.data_id);
		    	float tmpItemValue = itemGprs.rx - otherDayAndSameDataIDItem.rx;
		    	if ( tmpItemValue>=0 )  itemGprs.rx = tmpItemValue;
		    	
		    	tmpItemValue = itemGprs.tx - otherDayAndSameDataIDItem.tx;
		    	if ( tmpItemValue>=0 )  itemGprs.tx = tmpItemValue;
		    	
		    	updateNetTrafficBytesItem(itemGprs);
		    } catch (Exception e) {
				e.printStackTrace();
			}
		    
		    Log.d(TAG, "登记GPRS流量"); 
		    
		    netTrafficChange = true;
		    
		    last_gprs_Bytes  = currentMobileAll;
		}
		
		//Wifi流量发生变化   登记Wifi流量    		
		if ( currentWifiAll != last_wifi_Bytes && currentWifiRx<0.01 ) {
			
			NetTrafficBytesItem itemWifi = new NetTrafficBytesItem();		
			itemWifi.dev 	 = NetTrafficBytesItem.DEV_WIFI;
			itemWifi.rx  	 = currentWifiRx;
			itemWifi.tx  	 = currentWifiTx;
		    itemWifi.date	 = strDate;
		    itemWifi.data_id = getDataID(itemWifi.dev, strDate); //数据流量批次标示
		    
		    try {
		    	//判断同一批次的除今天外的要扣掉,如果一样需要扣除以前的流量
		    	NetTrafficBytesItem otherDayAndSameDataIDItem = getBytesSumForOtherDayAndSameDataID(itemWifi.dev,itemWifi.date,itemWifi.data_id);
		    	float tmpItemValue = itemWifi.rx - otherDayAndSameDataIDItem.rx;
		    	if ( tmpItemValue>=0 )  itemWifi.rx = tmpItemValue;
		    			
		    	tmpItemValue = itemWifi.tx - otherDayAndSameDataIDItem.tx;	
		    	if ( tmpItemValue>=0 )  itemWifi.tx = tmpItemValue;
		    	
		    	updateNetTrafficBytesItem(itemWifi);
		    } catch (Exception e) {
				e.printStackTrace();
			}
			
		    Log.d(TAG, "登记Wifi流量");
		    
		    netTrafficChange = true;
		    
			last_wifi_Bytes  = currentWifiAll;
		}
		
		return netTrafficChange;
	}

}