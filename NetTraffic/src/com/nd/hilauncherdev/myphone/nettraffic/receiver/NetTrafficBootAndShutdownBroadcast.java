package com.nd.hilauncherdev.myphone.nettraffic.receiver;


import com.felix.demo.activity.NetTrafficBytesAccessor;
import com.felix.demo.activity.NetTrafficRankingAccessor;
import com.nd.hilauncherdev.myphone.nettraffic.service.NetTrafficBytesService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class NetTrafficBootAndShutdownBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			
			// 重启,流量排行批次需要增加1
			NetTrafficRankingAccessor.getInstance(context).setBootCompletedRanking(true);
			
			// 重启,流量监控批次需要增加1
			NetTrafficBytesAccessor.getInstance(context).
				setPrefsKey(NetTrafficBytesAccessor.bootCompletedBytesGprsKey, true);
			NetTrafficBytesAccessor.getInstance(context).
				setPrefsKey(NetTrafficBytesAccessor.bootCompletedBytesWifiKey, true);
			
			// 开启流量监控服务
			/*
			 * 
			 * Intent intentService= new Intent(Intent.ACTION_RUN);
			 * intentService.setClass(context, NetTrafficBytesService.class);
			 * context.startService(intentService);
			 */

			//读取配置文件 分析是否 (1)开启流量监控 和  (2)悬浮窗口
			
			Intent intentService = new Intent(context, NetTrafficBytesService.class);
			intentService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意:要加不然无法开启Service
			context.startService(intentService);
			 
		} else if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
			
			// 重启,流量排行批次需要增加1
						NetTrafficRankingAccessor.getInstance(context).setBootCompletedRanking(true);
			// 重启,流量监控批次需要增加1
			NetTrafficBytesAccessor.getInstance(context).
				setPrefsKey(NetTrafficBytesAccessor.bootCompletedBytesGprsKey, true);
			NetTrafficBytesAccessor.getInstance(context).
				setPrefsKey(NetTrafficBytesAccessor.bootCompletedBytesWifiKey, true);
			
			
			//关机时再次保存最新的流量排行
			NetTrafficRankingAccessor.getInstance(context).insertALLAppNetTrafficToDB();
			
			NetTrafficBytesAccessor.logRealTimeTrafficBytes(context);
		}
	}
}
