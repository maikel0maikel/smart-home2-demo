package com.nd.hilauncherdev.lib.theme;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
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
    
    
    public interface NdLauncherExDownActionCallback{
    	
    	/**
    	 * 资源首次下载回调(断点续传不调用)
    	 * ----实现此方法时,为防止ANR请在内部开启Thread处理统计。
    	 * @param ctx
    	 * @param itemType  
    	 * 			ThemeItem.ITEM_TYPE_SKIN 表示皮肤
    	 * 			ThemeItem.ITEM_TYPE_THEME 表示主题
    	 * 			ThemeItem.ITEM_TYPE_LAUNCHER 表示91桌面
    	 * @param resID 服务端资源id统一为主题id+itemType
    	 */
    	public void firstDown(Context ctx, int itemType, String resID);
    }
}
