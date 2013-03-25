package com.nd.hilauncherdev.myphone.nettraffic.receiver;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nd.hilauncherdev.kitset.util.ThreadUtil;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficRankingGprsWifiAccessor;
import com.nd.hilauncherdev.myphone.nettraffic.util.CrashTool;


public class NetTrafficRankingPackageBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
			// Ignore application updates
			final boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
			if (!replacing) {
				final int uid = intent.getIntExtra(Intent.EXTRA_UID, -123);
				final String pkgName = intent.getDataString();
				final Context ctx = context;
				if ( pkgName!=null ){
					//放到线程执行
					ThreadUtil.executeNetTraffic(new Runnable() {
						@Override
						public void run() {
							NetTrafficRankingGprsWifiAccessor.getInstance(ctx).applicationRemoved(pkgName.replaceAll("package:", ""), uid);
						}
					});
				}
			}
		}
	}

}
