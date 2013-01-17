package com.nd.hilauncherdev.lib.theme.service;


import com.nd.hilauncherdev.lib.theme.api.ThemeLauncherExAPI;
import com.nd.hilauncherdev.lib.theme.db.DowningTaskItem;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


/**
 * 负责创建对话框
 * @author cfb
 */
public class CreateDialogService extends Service {
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		DowningTaskItem dTaskItem = (DowningTaskItem)intent.getSerializableExtra("dTaskItem");
		if (dTaskItem!=null){
			ThemeLauncherExAPI.showThemeApplyDialog(getApplicationContext(), dTaskItem);
		}
		
        stopSelf();
	}
}
