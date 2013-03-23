package com.nd.hilauncherdev.myphone.nettraffic.receiver;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.felix.demo.activity.NetTrafficRankingAccessor;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficRankingGprsWifiAccessor;


public class NetTrafficRankingPackageBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
			// Ignore application updates
			final boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
			if (!replacing) {
				final int uid = intent.getIntExtra(Intent.EXTRA_UID, -123);
				String pkgName = intent.getDataString();
				if ( pkgName!=null ){
					//放到线程执行
					NetTrafficRankingAccessor.getInstance(context).applicationRemoved(pkgName.replaceAll("package:", ""), uid);
					NetTrafficRankingGprsWifiAccessor.getInstance(context).applicationRemoved(pkgName.replaceAll("package:", ""), uid);
				}
			}
		}
	}

}
