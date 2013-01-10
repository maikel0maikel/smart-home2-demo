package com.nd.hilauncherdev.lib.theme;

import android.content.Context;

import com.nd.hilauncherdev.lib.theme.util.HiLauncherThemeGlobal;

public class NdLauncherExThemeApi {

	/**皮肤应用广播*/
	public static final String ND_HILAUNCHER_THEME_SKIN_APPLY_ACTION = "nd.pandahome.external.request.skin.apply";
	
	/**接入应用ID key name*/
	public static final String ND_HILAUNCHER_THEME_APP_ID_KEY = "skinAppID";
	
	/**接入应用ID value*/
	public static String  ND_HILAUNCHER_THEME_APP_ID_VALUE = "1000";
	
	/**皮肤路径  key name*/
	public static final String ND_HILAUNCHER_THEME_APP_SKIN_PATH_KEY = "skinPath";
	
	/**皮肤路径  value 末尾需要目录符号*/
	public static String ND_HILAUNCHER_THEME_APP_SKIN_PATH_VALUE = HiLauncherThemeGlobal.PACKAPGES_HOME;
	
	public static void init(Context mContext){
		HiLauncherThemeGlobal.setContext(mContext.getApplicationContext());
        HiLauncherThemeGlobal.createDefaultDir();
	}
}
