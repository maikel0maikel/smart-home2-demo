package com.nd.hilauncherdev.lib.theme;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

import com.nd.hilauncherdev.lib.theme.util.HiLauncherThemeGlobal;

/**
 * 第三方调用接口
 *
 */
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

    public interface NdLauncherExDialogCallback {
    	/**
    	 * 
    	 * 通用对话框
    	 * @param ctx
    	 * @param icon 图标
    	 * @param title 标题
    	 * @param view 视图
    	 * @param positive 确定按钮文字
    	 * @param negative 取消按钮文字
    	 * @param ok 确定回调
    	 * @param cancle 取消回调
    	 */
	     public Dialog createThemeDialog(Context ctx, int icon, CharSequence title, CharSequence message, CharSequence positive, CharSequence negative, final OnClickListener ok, final OnClickListener cancle);
	}	
}
