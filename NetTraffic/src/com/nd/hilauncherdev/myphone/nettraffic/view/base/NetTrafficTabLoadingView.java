package com.nd.hilauncherdev.myphone.nettraffic.view.base;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.felix.demo.R;


public class NetTrafficTabLoadingView extends LinearLayout {

	public NetTrafficTabLoadingView(Context context) {
		super(context);
	}

	public NetTrafficTabLoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(context).inflate(R.layout.net_traffic_loading, this,true); 
		initView();
	}
	
	private void initView() {
		
	}
}
