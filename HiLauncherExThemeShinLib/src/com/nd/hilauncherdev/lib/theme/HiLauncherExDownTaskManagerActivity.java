package com.nd.hilauncherdev.lib.theme;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.nd.hilauncherdev.lib.theme.api.ThemeLauncherExAPI;
import com.nd.hilauncherdev.lib.theme.view.DownTaskManageView;


/**
 * 
 * 下载任务管理
 *
 */
public class HiLauncherExDownTaskManagerActivity extends Activity {

	private DownTaskManageView downTaskManageView;
	
	private ApiMessageReceiver mApiMessageReceiver = new ApiMessageReceiver();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		downTaskManageView = new DownTaskManageView(this);		
		downTaskManageView.initView();
		setContentView(downTaskManageView);
		
		IntentFilter filter = new IntentFilter();	
		filter.addAction(ThemeLauncherExAPI.ND_HILAUNCHER_THEME_APPLY_ACTION);
		filter.addAction(NdLauncherExThemeApi.ND_HILAUNCHER_THEME_SKIN_APPLY_ACTION);
		registerReceiver(mApiMessageReceiver, filter);	
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if (downTaskManageView!=null){
			downTaskManageView.destroyView();
		}
		
		unregisterReceiver(mApiMessageReceiver);
	}
	
	/**
	 * 工具包的消息广播接收
	 * @author cfb
	 */
	private class ApiMessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
				finish();
		}
	}
}