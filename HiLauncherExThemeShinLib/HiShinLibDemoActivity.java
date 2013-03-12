package com.nd.hilauncherdev.lib.theme;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.nd.android.lib.theme.R;
import com.nd.hilauncherdev.lib.theme.view.DownTaskManageView;
import com.nd.hilauncherdev.lib.theme.view.HiLauncherExThemeShinView;

public class HiShinLibDemoActivity extends Activity implements OnClickListener {
	
	private HiLauncherExThemeShinView hiLauncherExThemeShinView;
	
	private DownTaskManageView downTaskManageView;
	
	private LinearLayout mllContainer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.maindemo);
		
		findViewById(R.id.web).setOnClickListener(this);
		findViewById(R.id.down).setOnClickListener(this);
		
		mllContainer = (LinearLayout) findViewById(R.id.llCon);
		
		//1. 设置工作 略
		NdLauncherExAppSkinSetting appSkinSetting = new NdLauncherExAppSkinSetting();
		appSkinSetting.setAppId("2000");
		appSkinSetting.setAppKey("0");
		//appSkinSetting.setAppSkinPath("/sdcard/appSkinTest/");
		//appSkinSetting.setThemeExDialog(themeExDialog) //自定义对话框时设置
		//appSkinSetting.setThemeExDownAction(themeExDownAction) //下载动作回调设置
		
		//2. 执行初始化
		NdLauncherExThemeApi.init(this, appSkinSetting);
		
		showWeb();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if (hiLauncherExThemeShinView!=null){
			hiLauncherExThemeShinView.destroyView();
		}
		if (downTaskManageView!=null){
			downTaskManageView.destroyView();
		}
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.web:
            showWeb();
            break;
        case R.id.down:
            showDown();
            break;
        default:
            break;
        }
    }
    
    void showWeb() {
        mllContainer.removeAllViews();
        if (hiLauncherExThemeShinView == null) {
            // 1. 设置工作 略
            // 3. 静态或者动态注册皮肤应用接收器 略

            // 4. 动态创建View
            //HashMap<String, Object> initParaMap = new HashMap<String, Object>();
            hiLauncherExThemeShinView = new HiLauncherExThemeShinView(this);
            hiLauncherExThemeShinView.initView();
        }
        mllContainer.addView(hiLauncherExThemeShinView);
    }

    void showDown() {
        mllContainer.removeAllViews();
        if (downTaskManageView == null){
            //HashMap<String, Object> initParaMap = new HashMap<String, Object>();
            downTaskManageView = new DownTaskManageView(this);      
            downTaskManageView.initView();
        }
        mllContainer.addView(downTaskManageView);
    }


}