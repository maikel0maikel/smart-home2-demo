package com.nd.hilauncherdev.lib.theme;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;

import com.nd.hilauncherdev.lib.theme.view.HiLauncherExThemeShinView;

public class HiLauncherExThemeShinLibActivity extends Activity {
	
	private HiLauncherExThemeShinView hiLauncherExThemeShinView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//1. 设置工作 略
		
		//2. 执行初始化
		NdLauncherExThemeApi.init(this);
		
		//3. 静态或者动态注册皮肤应用接收器 略
		
		//4. 动态创建View
		HashMap<String, Object> initParaMap = new HashMap<String, Object>();
		hiLauncherExThemeShinView = new HiLauncherExThemeShinView(this);		
		hiLauncherExThemeShinView.initView(initParaMap);
		setContentView(hiLauncherExThemeShinView);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if (hiLauncherExThemeShinView!=null){
			hiLauncherExThemeShinView.destroyView();
		}
	}
}