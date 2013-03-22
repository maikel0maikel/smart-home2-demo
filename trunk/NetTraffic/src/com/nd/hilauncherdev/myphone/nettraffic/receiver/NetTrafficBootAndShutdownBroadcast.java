package com.nd.hilauncherdev.myphone.nettraffic.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficRankingGprsWifiAccessor;
import com.nd.hilauncherdev.myphone.nettraffic.util.NetTrafficSettingTool;

public class NetTrafficBootAndShutdownBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			
			NetTrafficConnectivityChangeBroadcast.logToFile(NetTrafficRankingGprsWifiAccessor.TAG, "ACTION_BOOT_COMPLETED 开机启动了");
			
			// 重启,流量监控批次需要增加1
			// 开启流量监控服务
			//读取配置文件 分析是否 (1)开启流量监控 和  (2)悬浮窗口
			/*
			Intent intentService = new Intent(context, NetTrafficBytesService.class);
			intentService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意:要加不然无法开启Service
			context.startService(intentService);
			 */
		} else if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
			
			NetTrafficSettingTool.SHUTDOWN_FLAG = true;
			
			NetTrafficConnectivityChangeBroadcast.logToFile(NetTrafficRankingGprsWifiAccessor.TAG, "ACTION_BOOT_COMPLETED 关机了");
			//关机时再次保存最新的流量排行
			//NetTrafficRankingAccessor.getInstance(context).insertALLAppNetTrafficToDB();
			//NetTrafficBytesAccessor.logRealTimeTrafficBytes(context);
		}
		
		NetTrafficSettingTool.setPrefsBoolean(context, NetTrafficSettingTool.bootCompletedRankingKey, true);
		NetTrafficSettingTool.setPrefsBoolean(context, NetTrafficSettingTool.bootCompletedBytesGprsKey, true);
		NetTrafficSettingTool.setPrefsBoolean(context, NetTrafficSettingTool.bootCompletedBytesWifiKey, true);
	}
}
