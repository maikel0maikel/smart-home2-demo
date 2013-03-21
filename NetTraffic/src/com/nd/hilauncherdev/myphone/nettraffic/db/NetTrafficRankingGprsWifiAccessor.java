package com.nd.hilauncherdev.myphone.nettraffic.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;

import com.nd.hilauncherdev.myphone.nettraffic.receiver.NetTrafficConnectivityChangeBroadcast;
import com.nd.hilauncherdev.myphone.nettraffic.util.NetTrafficStatsProxy;

/**
 * GPRS及WIFI流量排行数据存储
 * @author cfb
 */
public class NetTrafficRankingGprsWifiAccessor {

    private static final String TAG = "NetTrafficRankingGprsWifiAccessor";
    
	private static final String PREFS_NAME = "NetTrafficPrefs3";
	
	/**流量排行  是否重启如果是重启则需要增加1 */
	private static final String bootCompletedRankingKey = "isBootCompletedRanking";
	
	private static final String T_NETTRAFFIC_RANKING_DETAIL = "NetTrafficRankingDetail"; 
	
	/**数据批次*/
	private static int RANKING_DATE_ID = -1;
	
	private static HashSet<String> ignorePkgSet;
	
	private Context ctx;	

	private NetTrafficRankingGprsWifiAccessor(Context ctx){
		this.ctx = ctx;
	}	

	static private NetTrafficRankingGprsWifiAccessor accessor; 
	
	public static NetTrafficRankingGprsWifiAccessor getInstance(Context context){

		if(accessor == null){
			accessor = new NetTrafficRankingGprsWifiAccessor(context);
		}
		return accessor;
	}
	
	private NetTrafficRankingItem buildNetTrafficRankingItem(Cursor c) {
		
		NetTrafficRankingItem ret = new NetTrafficRankingItem();  
		ret.id = c.getInt(0);
		ret.dev = c.getInt(1);
		ret.pkg = c.getString(2); 
		ret.rx = c.getFloat(3);
        ret.tx = c.getFloat(4);
        ret.date = c.getString(5);
        ret.data_id = c.getInt(6);
        ret.uid = c.getInt(7);
        ret.names = c.getString(8);
		return ret; 
	}
	
	/**
	 * 流量排行统计组装
	 * @param c
	 * @return
	 */
	private NetTrafficRankingItem buildNetTrafficRankingItemForSum(Cursor c) {
		
		NetTrafficRankingItem ret = new NetTrafficRankingItem();  
		ret.pkg = c.getString(0); 
		ret.names = c.getString(1);
		ret.rx = c.getFloat(2);
        ret.tx = c.getFloat(3);
        ret.tal = c.getFloat(4);
        
		return ret; 
	}
	
	/**
	 * 查询单个软件的最后一次流量排行
	 * @param pkgName
	 * @param data_id
	 * @param dev
	 * @return
	 * @throws Exception
	 */
	public NetTrafficRankingItem getNetTrafficRankingItem(String pkgName, int data_id, int dev){
		
		NetTrafficRankingItem ret = null;
		NetTrafficDB db = null;
		Cursor c = null;
		try {
			db = new NetTrafficDB(ctx);
	        c = db.query("select * from "+T_NETTRAFFIC_RANKING_DETAIL+" where pkg=? and data_id=? and dev=?", new String[] {pkgName, data_id+"", dev+""});        
	        if(c.moveToFirst()) {            
	            ret = buildNetTrafficRankingItem(c);
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
	 * 增加或修改某个软件的排行流量
	 * @param item
	 * @return
	 * @throws Exception
	 */
	private boolean updateNetTrafficRankingItem(NetTrafficRankingItem item){
		
		boolean bResult = true; 
		ContentValues values = new ContentValues();		
		//values.put("id", item.id); //系统自增
		values.put("dev", item.dev);
		values.put("pkg", item.pkg);
		values.put("rx", item.rx);
		values.put("tx", item.tx);
		values.put("date", item.date);
		values.put("data_id", item.data_id);
		values.put("uid", item.uid);
		values.put("names", item.names);
				
		NetTrafficDB db = null;
		try {
			db = new NetTrafficDB(ctx);
			if(this.getNetTrafficRankingItem(item.pkg, item.data_id, item.dev)== null){			
				bResult = db.insertOrThrow(T_NETTRAFFIC_RANKING_DETAIL, null, values)>0;			
			}else{
				bResult = db.update(T_NETTRAFFIC_RANKING_DETAIL, values, "pkg=? and data_id=? and dev=?", new String[] {item.pkg, item.data_id+"", item.dev+""})>0;		
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
	 * 删除某个软件的流量排行
	 * @param pkg
	 * @return
	 */
	public boolean deleteNetTrafficRankingItem(String pkg){
		
		boolean bResult = true; 
		NetTrafficDB db = null;
		try {
			db = new NetTrafficDB(ctx);
			bResult = db.delete(T_NETTRAFFIC_RANKING_DETAIL, "pkg=?", new String[]{pkg});
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
	 * 删除某个软件的流量排行
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public boolean deleteNetTrafficRankingItemByUid(int uid){

		boolean bResult = true; 
		NetTrafficDB db = null;
		try {
			db = new NetTrafficDB(ctx);
			bResult = db.delete(T_NETTRAFFIC_RANKING_DETAIL, "uid=?", new String[]{uid+""});
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
	 * 清空流量排行表
	 * @return
	 */
	public boolean clearNetTrafficRanking() {
		
		boolean bResult = true; 
		NetTrafficDB db = null;
		try {
			db = new NetTrafficDB(ctx);
			bResult = db.delete(T_NETTRAFFIC_RANKING_DETAIL, null, null);
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
	 * 获取最大的数据批次数据表示
	 * @return
	 */
	public int getMaxDataID(){
		
		int maxID = 0;
		NetTrafficDB db = null;
		Cursor c = null;
		try {
			db = new NetTrafficDB(ctx);
			c = db.query("select max(data_id) from "+T_NETTRAFFIC_RANKING_DETAIL, null);        
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
	
	/**
	 * 获取所有的流量行记录
	 * @param dev
	 * @return
	 */
	public ArrayList<NetTrafficRankingItem> getAllNetTrafficRanking(int dev) {

		ArrayList<NetTrafficRankingItem> ret = new ArrayList<NetTrafficRankingItem>();
		
		NetTrafficDB db = null;
		Cursor c = null;

		try {
			//rx、tx在数据库中已 KB单位存放
			String sql = ""
					+ "SELECT pkg,names, "
					+ "       Sum(rx)             rx_tal, "
					+ "       Sum(tx)             tx_tal, "
					+ "       (Sum(rx) + Sum(tx)) all_tal "
					+ "FROM   "+T_NETTRAFFIC_RANKING_DETAIL+" "
					+ "WHERE dev=? "
					+ "GROUP  BY pkg,names "
					+ "HAVING all_tal > 0.1 "
					+ "ORDER  BY all_tal DESC ";
			db = new NetTrafficDB(ctx);
			c = db.query(sql, new String[]{dev+""});
			c.moveToFirst();
			while (!c.isAfterLast()) {
				NetTrafficRankingItem item = buildNetTrafficRankingItemForSum(c);
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
	 * 获取本次网络类型
	 * @param iDev
	 */
	public void insertALLAppNetTrafficToDB(int iDev, String strDate){
				
		NetTrafficDB db = null;
		try {

			db = new NetTrafficDB(ctx);
			
			final PackageManager pkgmanager = ctx.getPackageManager();
			final List<ApplicationInfo> installed = pkgmanager.getInstalledApplications(0);
			final HashMap<Integer, NetTrafficRankingItem> appMap = new HashMap<Integer, NetTrafficRankingItem>();
			NetTrafficRankingItem app = null;
			NetTrafficRankingItem ret = null;
			long tx = 0;
			long rx = 0;
			for (final ApplicationInfo apinfo : installed) {
				
				/*
				if ((apinfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) { // 系统程序
					continue;
				} 
				*/ 
				//过滤只处理有Internet连接权限的app
				if (PackageManager.PERMISSION_GRANTED != 
						pkgmanager.checkPermission(Manifest.permission.INTERNET, apinfo.packageName)) {
					continue;
				}

				//过滤不统计流量的软件包名
				if ( isIgnoreProcess(apinfo.packageName) ) {
					continue;
				}
				
				//总流量为0的不统计，无流量时函数返回-1,所以两个相加为-2
				tx = NetTrafficStatsProxy.getUidTxBytes(apinfo.uid);
				rx = NetTrafficStatsProxy.getUidRxBytes(apinfo.uid);
				
				if ( tx<0 ) {
					tx = 0;
				}
				if ( rx<0 ){
					rx = 0;
				}
				
				if (tx+rx<10){
					continue;
				}
				
				app = appMap.get(apinfo.uid);
				if (app == null) {
					app = new NetTrafficRankingItem();
					app.dev = iDev;
					app.uid = apinfo.uid;
					app.names = pkgmanager.getApplicationLabel(apinfo).toString();
					app.pkg = apinfo.packageName;
					app.rx = NetTrafficStatsProxy.getUidRxBytes(apinfo.uid)/1024f;
					if (app.rx<0){
						app.rx = 0;
					}
					app.tx = NetTrafficStatsProxy.getUidTxBytes(apinfo.uid)/1024f;
					if (app.tx<0){
						app.tx = 0;
					}
					app.date = strDate;
					app.data_id = getDataID();
					
					appMap.put(apinfo.uid, app);
				}else{
					app.names = app.names+","+pkgmanager.getApplicationLabel(apinfo).toString();
				}
			}
			
			Iterator<Integer> appIterator = appMap.keySet().iterator();
			while(appIterator.hasNext()) {
				
				app = appMap.get(appIterator.next());
				if (app==null)
					continue;
				
				ContentValues values = new ContentValues();		
				//values.put("id", item.id); //系统自增
				values.put("dev", app.dev);
				values.put("pkg", app.pkg);
				values.put("rx", app.rx);
				values.put("tx", app.tx);
				values.put("date", app.date);
				values.put("data_id", app.data_id);
				values.put("uid", app.uid);
				values.put("names", app.names);
				
				//查询前初始化
				ret = null;
				Cursor c = null;
				try {
					//NetTrafficConnectivityChangeBroadcast.logToFile(TAG, "开始db.query");
			        c = db.query("select * from "+T_NETTRAFFIC_RANKING_DETAIL+" where pkg=? and data_id=? and dev=?", new String[] {app.pkg, app.data_id+"", app.dev+""});   
			        //NetTrafficConnectivityChangeBroadcast.logToFile(TAG, "结束db.query");
			        if(c.moveToFirst()) {            
			            ret = buildNetTrafficRankingItem(c);
			        }
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(c!=null){
						//NetTrafficConnectivityChangeBroadcast.logToFile(TAG, "开始c.close()");
						c.close();
						//NetTrafficConnectivityChangeBroadcast.logToFile(TAG, "结束c.close()");
					}
				}
				
				boolean updateFlagRx = true;
				boolean updateFlagTx = true;
				boolean bResult = false;
				if(ret==null){			
					//NetTrafficConnectivityChangeBroadcast.logToFile(TAG, "开始db.insertOrThrow");
					bResult = db.insertOrThrow(T_NETTRAFFIC_RANKING_DETAIL, null, values)>0;			
					//NetTrafficConnectivityChangeBroadcast.logToFile(TAG, "结束db.insertOrThrow");
				}else{
					if(ret.rx>app.rx){
						values.put("rx", app.rx+ret.rx);
					}else{
						//小于1k则不更新
						if(app.rx-ret.rx<1){
							updateFlagRx = false;
						}
					}
					if(ret.tx>app.tx){
						values.put("tx", app.tx+ret.tx);
					}else{
						//小于1k则不更新
						if(app.tx-ret.tx<1){
							updateFlagTx = false;
						}
					}
					if (updateFlagRx||updateFlagTx) {
						//NetTrafficConnectivityChangeBroadcast.logToFile(TAG, "开始db.update");
						bResult = db.update(T_NETTRAFFIC_RANKING_DETAIL, values, "pkg=? and data_id=? and dev=? and rx<?", new String[] {app.pkg, app.data_id+"", app.dev+"", app.rx+""})>0;
						//NetTrafficConnectivityChangeBroadcast.logToFile(TAG, "结束db.update");
						
						NetTrafficConnectivityChangeBroadcast.logToFile(TAG, "更新"+app.names +" 结果"+bResult);
					}else{
						//NetTrafficConnectivityChangeBroadcast.logToFile(TAG, "无更新"+app.names);
					}
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(db!=null){
				db.close();
			}
		}
	}
	
	/**
	 * 是否过滤不统计流量的软件包名
	 * @param pkgName
	 * @return
	 */
	private boolean isIgnoreProcess(String pkgName) {
		
		if (ignorePkgSet==null) {
			
			ignorePkgSet = new HashSet<String>();
		
			try {
				InputStream is = ctx.getAssets().open("traffic.nd");
				InputStreamReader isr = new InputStreamReader(is);			
				BufferedReader bufferedReader = new BufferedReader(isr);
				String read = null;
				while ((read = bufferedReader.readLine()) != null) {
					read = read.trim();
					ignorePkgSet.add(read);
				}
				bufferedReader.close();
				isr.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return ignorePkgSet.contains(pkgName);
		}
		
		return ignorePkgSet.contains(pkgName);
	}
	
    private int getDataID(){
    	
    	//RANKING_DATE_ID 初始化后，只有在重启手机或者重启应用是才需要重新初始化
    	if (RANKING_DATE_ID==-1){
    			
			int maxID = getMaxDataID();
			
			boolean bBoot = getBootCompletedRanking();
			RANKING_DATE_ID = maxID;
			if ( bBoot ){ 
				RANKING_DATE_ID = maxID+1;
				setBootCompletedRanking(false);
			}
    	}
    	
    	return RANKING_DATE_ID;
    }
    
    private boolean getBootCompletedRanking(){
    	
    	final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
    	return prefs.getBoolean(bootCompletedRankingKey, false);
    }
    
    private void setBootCompletedRanking(boolean bootComplete){
    	
    	final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
    	Editor editor = prefs.edit();
    	editor.putBoolean(bootCompletedRankingKey, bootComplete);
    	editor.commit();
    }
    
    public void applicationRemoved(String pkgName,int uid){
    	deleteNetTrafficRankingItem(pkgName);
    }
    
}
