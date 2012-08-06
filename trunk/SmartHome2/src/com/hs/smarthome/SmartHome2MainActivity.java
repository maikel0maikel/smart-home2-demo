package com.hs.smarthome;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

public class SmartHome2MainActivity extends ActivityGroup implements View.OnClickListener{
    
	private FrameLayout tabContainer;
	
	private FrameLayout tabHomeButton;
	private FrameLayout tabSwitchButton;
	private FrameLayout tabSceneButton;
	private FrameLayout tabAlarmLogButton;
	private FrameLayout tabMoreButton;
	
	private View lastActionButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smarthome_main);

		this.tabHomeButton = (FrameLayout)findViewById(R.id.tabHome);
		this.tabSwitchButton = (FrameLayout)findViewById(R.id.tabSwitch);
		this.tabSceneButton = (FrameLayout)findViewById(R.id.tabScene);
		this.tabAlarmLogButton = (FrameLayout)findViewById(R.id.tabAlarmLog);
		this.tabMoreButton = (FrameLayout)findViewById(R.id.tabMore);
		
		this.tabHomeButton.setOnClickListener(this);
		this.tabSwitchButton.setOnClickListener(this);
		this.tabSceneButton.setOnClickListener(this);
		this.tabAlarmLogButton.setOnClickListener(this);
		this.tabMoreButton.setOnClickListener(this);
		
		tabContainer = (FrameLayout) findViewById(R.id.tabs);
		
		showView(tabHomeButton);
    }
    
	public void setButtonTextColor(FrameLayout actionView, boolean paramBoolean){
		
		if ( actionView!=null ) {
			
			for (int i = 0; i < actionView.getChildCount(); i++) {
				View child  = actionView.getChildAt(i);
				if (child instanceof TextView) {
					TextView actionChild = (TextView)child;
					if ( paramBoolean ) {
						actionChild.setTextColor(Color.WHITE);
					}else{
						actionChild.setTextColor(Color.BLACK);
					}
				}
			}  
		}		
	}
	
	public void showView(View paramView){
		
		if (lastActionButton==paramView) 
			return ; 
		
		Intent reqIntent = null;
		
		if (lastActionButton!=null) 
			lastActionButton.setSelected(false);
		
		switch (paramView.getId()) {
			case R.id.tabHome:
				lastActionButton = paramView;
				reqIntent =new Intent(this, FuncHomeActivity.class);
				break;
			case R.id.tabSwitch:
				lastActionButton = paramView;
				reqIntent =new Intent(this, FuncSwitchActivity.class);
				break;
			case R.id.tabScene:
				lastActionButton = paramView;
				reqIntent =new Intent(this, FuncSceneActivity.class);
				break;
			case R.id.tabAlarmLog:
				lastActionButton = paramView;
				reqIntent =new Intent(this, FuncAlarmLogActivity.class);
				break;
			case R.id.tabMore:
				lastActionButton = paramView;
				reqIntent =new Intent(this, FuncMoreActivity.class);
				break;				
		}
		
		if (lastActionButton!=null) 
			lastActionButton.setSelected(true);
		
		if (reqIntent==null)
			return ;
		
		LocalActivityManager laMgr = getLocalActivityManager();
		View mainView = laMgr.startActivity(paramView.getId()+"", reqIntent).getDecorView();
		LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mainView.setLayoutParams(param);
		tabContainer.removeAllViews();
		tabContainer.addView(mainView);
	}

	@Override
	public void onClick(View v) {
		
		showView(v);
	}
}