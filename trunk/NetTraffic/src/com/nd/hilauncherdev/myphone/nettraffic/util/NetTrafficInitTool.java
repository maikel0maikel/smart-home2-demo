package com.nd.hilauncherdev.myphone.nettraffic.util;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;

import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficBytesAccessor;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficBytesItem;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficRankingItem;

public class NetTrafficInitTool {
	
	private static boolean hasInitFlag = false; 
	
	private static final ConcurrentHashMap<Integer, NetTrafficRankingItem> allAppMap = new ConcurrentHashMap<Integer, NetTrafficRankingItem>();
	
	/**最后一次读取的gprs流量值*/
	public static float last_gprs_rx_Bytes = -1f;
	public static float last_gprs_tx_Bytes = -1f;
	
	/**最后一次读取的wifi总流量值*/
	public static float last_wifi_rx_Bytes = -1f;
	public static float last_wifi_tx_Bytes = -1f;
	
	public static synchronized ConcurrentHashMap<Integer, NetTrafficRankingItem> getCacheAppMap(Context ctx){
		
		if ( !hasInitFlag ){

			//从数据库读取今日和本月流量
			NetTrafficBytesAccessor.netTrafficGprsResult = NetTrafficBytesAccessor.getInstance(ctx)
				.getDayAndMonth(NetTrafficBytesItem.DEV_GPRS, CrashTool.getStringDate(), CrashTool.getStringMonth());
			NetTrafficBytesAccessor.netTrafficWifiResult = NetTrafficBytesAccessor.getInstance(ctx)
				.getDayAndMonth(NetTrafficBytesItem.DEV_WIFI, CrashTool.getStringDate(), CrashTool.getStringMonth());
			
			//初始化系统GPRS及WIFI开始流量
			float currentTotalRx  = TrafficStats.getTotalRxBytes()/1024f;
			float currentTotalTx  = TrafficStats.getTotalTxBytes()/1024f;
			//GPRS流量
			float currentMobileRx = TrafficStats.getMobileRxBytes()/1024f;
			float currentMobileTx = TrafficStats.getMobileTxBytes()/1024f;
			
			last_gprs_rx_Bytes = currentMobileRx;
			last_gprs_tx_Bytes = currentMobileTx;
			last_wifi_rx_Bytes = currentTotalRx-currentMobileRx;
			last_wifi_tx_Bytes = currentTotalTx-currentMobileTx;
			
			
			//初始化每个应用的流量
			final PackageManager pkgmanager = ctx.getPackageManager();
			final List<ApplicationInfo> installed = pkgmanager.getInstalledApplications(0);
			NetTrafficRankingItem app = null;
			long tx = 0;
			long rx = 0;
			for (final ApplicationInfo apinfo : installed) {
				
				//过滤只处理有Internet连接权限的app
				if (PackageManager.PERMISSION_GRANTED != pkgmanager.checkPermission(Manifest.permission.INTERNET, apinfo.packageName)) {
					continue;
				}
				
				tx = NetTrafficStatsProxy.getUidTxBytes(apinfo.uid);
				rx = NetTrafficStatsProxy.getUidRxBytes(apinfo.uid);
				
				if ( tx<0 ) {
					tx = 0;
				}
				if ( rx<0 ){
					rx = 0;
				}
				
				app = allAppMap.get(apinfo.uid);
				if (app == null) {
					app = new NetTrafficRankingItem();
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

					allAppMap.put(apinfo.uid, app);
				}else{
					app.names = app.names+","+pkgmanager.getApplicationLabel(apinfo).toString();
				}
			}
			
			hasInitFlag = true;
		}
		return allAppMap;
	}
}
