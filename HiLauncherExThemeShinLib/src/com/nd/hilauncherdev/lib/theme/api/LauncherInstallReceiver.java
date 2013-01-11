package com.nd.hilauncherdev.lib.theme.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nd.hilauncherdev.lib.theme.db.DowningTaskItem;
import com.nd.hilauncherdev.lib.theme.db.ThemeLibLocalAccessor;
import com.nd.hilauncherdev.lib.theme.util.SharedPrefsUtil;

/**
 * 91桌面 安装完成接收器
 * 
 * @author cfb
 */
public class LauncherInstallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (intent==null)
			return;
		
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            String packageName = intent.getDataString(); 
            
            Log.e("LauncherInstallReceiver", packageName);
            if (packageName!=null && packageName.length()>8){
            	packageName = packageName.substring(8);
            	//自动应用下载桌面时下载的主题
            	if ( ThemeLauncherExAPI.THEME_MANAGE_PACKAGE_NAME.equals(packageName) ) {
            		//1.读取下载时设置的主题
            		//2.判断主题是否下载完成
            		//3.完成则直接应用
            		String autoApplyThemeID = SharedPrefsUtil.getInstance(context).getString(SharedPrefsUtil.KEY_AUTO_APPLY_THEMEID,null);
            		if( autoApplyThemeID!=null ){
            			try {
            				DowningTaskItem downingTaskItem = ThemeLibLocalAccessor.getInstance(context).getDowningTaskItem(autoApplyThemeID);
            				if (downingTaskItem!=null){
            					ThemeLauncherExAPI.installAndApplyAPT(context, downingTaskItem.tmpFilePath, downingTaskItem.themeID, downingTaskItem.startID);
            				}
						} catch (Exception e) {
							e.printStackTrace();
						}
            		}
            	}
            }
        }  
	}
}
