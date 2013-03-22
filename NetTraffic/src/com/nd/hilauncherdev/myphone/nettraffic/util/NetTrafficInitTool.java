package com.nd.hilauncherdev.myphone.nettraffic.util;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficRankingItem;

public class NetTrafficInitTool {
	
	private static boolean hasInitFlag = false; 
	
	private static final ConcurrentHashMap<Integer, NetTrafficRankingItem> allAppMap = new ConcurrentHashMap<Integer, NetTrafficRankingItem>();
	
	public static synchronized ConcurrentHashMap<Integer, NetTrafficRankingItem> getCacheAppMap(Context ctx){
		
		if ( !hasInitFlag ){

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
