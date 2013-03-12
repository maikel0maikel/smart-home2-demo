package com.nd.hilauncherdev.lib.theme;

import android.content.Context;
import android.util.Log;

import com.nd.hilauncherdev.lib.theme.util.HiLauncherThemeGlobal;

/**
 * 第三方调用接口
 *
 */
public class NdLauncherExThemeApi {
	
	public static final String TAG = "NdLauncherExThemeApi";
	
	/**皮肤应用广播*/
	public static final String ND_HILAUNCHER_THEME_SKIN_APPLY_ACTION = "nd.pandahome.external.request.skin.apply";
	
	/**接入应用ID key*/
	public static final String ND_HILAUNCHER_THEME_APP_ID_KEY = "skinAppID";
	
	/**皮肤路径  key*/
	public static final String ND_HILAUNCHER_THEME_APP_SKIN_PATH_KEY = "skinPath";

	/**App信息*/
	private static NdLauncherExAppSkinSetting ndAppSkinSetting = null;
	
	public static void init(Context mContext, NdLauncherExAppSkinSetting mNdAppSkinSetting){
		HiLauncherThemeGlobal.setContext(mContext.getApplicationContext());
        HiLauncherThemeGlobal.createDefaultDir();
        ndAppSkinSetting = mNdAppSkinSetting; 
	}
	
	public static String getAppId(){
		checkSetting();
		return ndAppSkinSetting.getAppId();
	}
	
	public static String getAppKey(){
		checkSetting();
		return ndAppSkinSetting.getAppKey();
	}
	
	public static String getAppSkinPath(){
		checkSetting();
		return ndAppSkinSetting.getAppSkinPath();
	}
	
	public static NdLauncherExDialogCallback getThemeExDialog(){
		checkSetting();
		return ndAppSkinSetting.getThemeExDialog();
	}
	
	public static NdLauncherExDownActionCallback getThemeExDownAction(){
		checkSetting();
		return ndAppSkinSetting.getThemeExDownAction();
	}
	
	private static void checkSetting(){
		if ( ndAppSkinSetting==null  ) {
			ndAppSkinSetting = new NdLauncherExAppSkinSetting();
			Log.e(TAG, "未设置配置信息启用默认调试配置");
		}
	}

}
