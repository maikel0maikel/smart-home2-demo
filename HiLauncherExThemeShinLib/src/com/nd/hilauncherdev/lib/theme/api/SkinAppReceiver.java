package com.nd.hilauncherdev.lib.theme.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nd.hilauncherdev.lib.theme.NdLauncherExThemeApi;

public class SkinAppReceiver  extends BroadcastReceiver{
	public void onReceive(Context context, Intent intent) {

		if (intent==null)
			return ;
		if (intent.getAction().equals(NdLauncherExThemeApi.ND_HILAUNCHER_THEME_SKIN_APPLY_ACTION)) {
			
			String appID = intent.getStringExtra( NdLauncherExThemeApi.ND_HILAUNCHER_THEME_APP_ID_KEY );
			
			if (appID!=null && appID.equals(NdLauncherExThemeApi.getAppId()) ){
				//Do Something here
				//获取本次皮肤应用的皮肤路径
				String skinPath = intent.getStringExtra( NdLauncherExThemeApi.ND_HILAUNCHER_THEME_APP_SKIN_PATH_KEY );
			}
		}
	}
}
