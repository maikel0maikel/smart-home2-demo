package com.nd.hilauncherdev.lib.theme.api;


import com.nd.hilauncherdev.lib.theme.db.LocalAccessor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 91桌面主题安装结果接收器
 * @author cfb
 */
public class ThemeInstallResultReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {

		if (intent==null)
			return ;
		if (intent.getAction().equals(ThemeLauncherExAPI.INSTALL_RESULT_ACTION_RECEIVER)) {
			//newId为安装后生成的主题ID
			String newId = intent.getStringExtra("themeid");
			String serverThemeID = intent.getStringExtra( "serverThemeID" );
			
			if (newId!=null && !"".equals(newId) ){
				try {
					LocalAccessor.getInstance(context).updateDownTaskItemForNewThemeID(serverThemeID, newId);
					//LocalAccessor.getInstance(context).updateDownTaskItemForNewThemeIDByAptPath(aptPath+"", themeid);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}