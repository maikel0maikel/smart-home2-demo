package com.nd.hilauncherdev.lib.theme;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;

import com.nd.hilauncherdev.lib.theme.view.DownTaskManageView;

public class HiLauncherExDownTaskManagerActivity extends Activity {

	private DownTaskManageView downTaskManageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		HashMap<String, Object> initParaMap = new HashMap<String, Object>();
				
		downTaskManageView = new DownTaskManageView(this);		
		downTaskManageView.initView(initParaMap);
		
		setContentView(downTaskManageView);
		
		HiLauncherExApplyThemeDialog.parentActivity = this;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if (downTaskManageView!=null){
			downTaskManageView.destroyView();
		}
		
		HiLauncherExApplyThemeDialog.parentActivity = null;
	}
}
