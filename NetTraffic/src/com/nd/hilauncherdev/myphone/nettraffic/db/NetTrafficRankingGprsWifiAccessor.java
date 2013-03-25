 package com.nd.hilauncherdev.myphone.nettraffic.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;

import com.nd.hilauncherdev.myphone.nettraffic.receiver.NetTrafficConnectivityChangeBroadcast;
import com.nd.hilauncherdev.myphone.nettraffic.util.CrashTool;
import com.nd.hilauncherdev.myphone.nettraffic.util.NetTrafficInitTool;
import com.nd.hilauncherdev.myphone.nettraffic.util.NetTrafficSettingTool;
import com.nd.hilauncherdev.myphone.nettraffic.util.NetTrafficStatsProxy;

/**
 * GPRS及WIFI流量排行数据存储
 * @author cfb
 */
public class NetTrafficRankingGprsWifiAccessor {

    public static final String TAG = "NetTrafficRankingGprsWifiAccessor";
	
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
	 * @param uid
	 * @param data_id
	 * @param dev
	 * @return
	 */
	public NetTrafficRankingItem getNetTrafficRankingItem(int uid, int data_id, int dev){
		
		NetTrafficRankingItem ret = null;
		NetTrafficDB db = null;
		Cursor c = null;
		try {
			db = new NetTrafficDB(ctx);
	        c = db.query("select * from "+T_NETTRAFFIC_RANKING_DETAIL+" where uid=? and data_id=? and dev=?", new String[] {uid+"", data_id+"", dev+""});        
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
			if(this.getNetTrafficRankingItem(item.uid, item.data_id, item.dev)== null){			
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
	 * @param uid
	 * @return
	 */
	public boolean deleteNetTrafficRankingItem(int uid){
		
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
	 * 获取所有的流量行记录
	 * @param dev
	 * @return
	 */
	public ArrayList<NetTrafficRankingItem> getAllNetTrafficRanking(int dev, String beginTime, String endTime) {

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
					+ "AND date>=? AND date<=? "
					+ "GROUP  BY pkg,names "
					+ "HAVING all_tal > 0.1 "
					+ "ORDER  BY all_tal DESC ";
			db = new NetTrafficDB(ctx);
			c = db.query(sql, new String[]{dev+"", beginTime, endTime});
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
	@SuppressLint("UseSparseArrays")
	public void insertALLAppNetTrafficToDB(int iDev, String strDate){
				
		NetTrafficDB db = null;
		try {
			
			db = new NetTrafficDB(ctx);
			
			final ConcurrentHashMap<Integer, NetTrafficRankingItem> allAppMap = NetTrafficInitTool.getCacheAppMap(ctx);
			
			final PackageManager pkgmanager = ctx.getPackageManager();
			final List<ApplicationInfo> installed = pkgmanager.getInstalledApplications(0);
			final HashMap<Integer, NetTrafficRankingItem> appMap = new HashMap<Integer, NetTrafficRankingItem>();
			NetTrafficRankingItem app = null;
			NetTrafficRankingItem globalApp = null;
			NetTrafficRankingItem ret = null;
			long tx = 0;
			long rx = 0;
			for (final ApplicationInfo apinfo : installed) {
				
				//过滤只处理有Internet连接权限的app
				if (PackageManager.PERMISSION_GRANTED != 
						pkgmanager.checkPermission(Manifest.permission.INTERNET, apinfo.packageName)) {
					continue;
				}
				
				/*
				if ((apinfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) { // 系统程序
					continue;
				} 
				*/ 
				//过滤不统计流量的软件包名
				/*
				if ( isIgnoreProcess(apinfo.packageName) ) {
					continue;
				}
				*/
				
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
					app.tx = NetTrafficStatsProxy.getUidTxBytes(apinfo.uid)/1024f;
					
					globalApp = allAppMap.get(app.uid);
					if ( iDev==NetTrafficBytesItem.DEV_GPRS ){
						if ( globalApp!=null ){
							if ( globalApp.rx<=app.rx ){
								float appAddRx = app.rx-globalApp.rx; 
								globalApp.rx = app.rx;
								app.rx = appAddRx;
								globalApp.sumGprsRx += appAddRx;
							}else{
								app.rx = globalApp.sumGprsRx;
							}
							if ( globalApp.tx<=app.tx ){
								float appAddTx = app.tx-globalApp.tx; 
								globalApp.tx = app.tx;
								app.tx = appAddTx;
								globalApp.sumGprsTx += appAddTx;
							}else{
								app.tx = globalApp.sumGprsTx;
							}
						}else{
							globalApp = new NetTrafficRankingItem();
							globalApp.rx = app.rx;
							globalApp.tx = app.tx;
							globalApp.sumGprsRx = app.rx;
							globalApp.sumGprsTx = app.tx;
							allAppMap.put(app.uid, globalApp);
						}
					}else{
						if ( globalApp!=null ){
							if ( globalApp.rx<=app.rx ){
								float appAddRx = app.rx-globalApp.rx; 
								globalApp.rx = app.rx;
								app.rx = appAddRx;
								globalApp.sumWifiRx += appAddRx;
							}else{
								app.rx = globalApp.sumWifiRx;
							}
							if ( globalApp.tx<=app.tx ){
								float appAddTx = app.tx-globalApp.tx; 
								globalApp.tx = app.tx;
								app.tx = appAddTx;
								globalApp.sumWifiTx += appAddTx;
							}else{
								app.tx = globalApp.sumWifiTx;
							}
						}else{
							globalApp = new NetTrafficRankingItem();
							globalApp.rx = app.rx;
							globalApp.tx = app.tx;
							globalApp.sumWifiRx = app.rx;
							globalApp.sumWifiTx = app.tx;
							allAppMap.put(app.uid, globalApp);
						}
					}
					
					if (app.rx<0){
						app.rx = 0;
					}
					if (app.tx<0){
						app.tx = 0;
					}
					if (app.rx+app.tx==0){
						continue;
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
			        c = db.query("select * from "+T_NETTRAFFIC_RANKING_DETAIL+" where uid=? and data_id=? and dev=? and date=?", new String[] {app.uid+"", app.data_id+"", app.dev+"", app.date});   
			        if(c.moveToFirst()) {            
			            ret = buildNetTrafficRankingItem(c);
			        }
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(c!=null){
						c.close();
					}
				}
				
				boolean bResult = false;
				if(ret==null){			
					bResult = db.insertOrThrow(T_NETTRAFFIC_RANKING_DETAIL, null, values)>0;			
				}else{
					app.rx += ret.rx;
					values.put("rx", app.rx);
					
					app.tx += ret.tx;
					values.put("tx", app.tx);
					
					bResult = db.update(T_NETTRAFFIC_RANKING_DETAIL, values, "uid=? and data_id=? and dev=? and date=?", new String[] {app.uid+"", app.data_id+"", app.dev+"", app.date})>0;
					NetTrafficConnectivityChangeBroadcast.logToFile(TAG, "更新"+app.names +" 结果"+bResult);
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
    	
    	if (RANKING_DATE_ID==-1){
    		
    		boolean bBoot = NetTrafficSettingTool.getPrefsBoolean(ctx, NetTrafficSettingTool.bootCompletedRankingKey, false);
    		int maxID = 0;
    		if ( NetTrafficSettingTool.SHUTDOWN_FLAG || !bBoot ) {
				RANKING_DATE_ID = (int)NetTrafficSettingTool.getPrefsLong(ctx, NetTrafficSettingTool.iRankingMaxIdKey, -1);
				if (RANKING_DATE_ID==-1){
					RANKING_DATE_ID = getMaxDataIDFormDB();
				}
				NetTrafficConnectivityChangeBroadcast.logToFile(TAG, "SHUTDOWN_FLAG 更新  maxID = "+RANKING_DATE_ID+";bBoot="+bBoot);
    		}else{
				maxID = getMaxDataIDFormDB();
				RANKING_DATE_ID = maxID+1;				
				NetTrafficConnectivityChangeBroadcast.logToFile(TAG, "更新 maxID = maxID+ 1= "+maxID+";bBoot="+bBoot);
				NetTrafficSettingTool.setPrefsBoolean(ctx, NetTrafficSettingTool.bootCompletedRankingKey, false);
				NetTrafficSettingTool.setPrefsLong(ctx, NetTrafficSettingTool.iRankingMaxIdKey, RANKING_DATE_ID);
    		}
    	}
    	
    	return RANKING_DATE_ID;
    }

	/**
	 * 获取最大的数据批次数据表示
	 * @return
	 */
	private int getMaxDataIDFormDB(){
		
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
	
	public String getMaxMinStringDate(boolean isMax){
		
		String resultDate = CrashTool.getStringDate();
		NetTrafficDB db = null;
		Cursor c = null;
		try {
			db = new NetTrafficDB(ctx);
			if (isMax){
				c = db.query("select max(date) from "+T_NETTRAFFIC_RANKING_DETAIL, null);
			}else{
				c = db.query("select min(date) from "+T_NETTRAFFIC_RANKING_DETAIL, null);
			}
	        if (c.moveToFirst()) {            
	        	resultDate= c.getString(0);
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
		return resultDate;
	}
	
    public void applicationRemoved(String pkgName,int uid){
    	deleteNetTrafficRankingItem(uid);
    }
    
}
