package com.nd.hilauncherdev.myphone.nettraffic.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.felix.demo.R;
import com.nd.hilauncherdev.framework.view.LazyViewPager;
import com.nd.hilauncherdev.framework.view.ViewPagerTab;
import com.nd.hilauncherdev.myphone.nettraffic.view.NetTrafficFirewallView;
import com.nd.hilauncherdev.myphone.nettraffic.view.NetTrafficRankingView;
import com.nd.hilauncherdev.myphone.nettraffic.view.base.NetTrafficTabBaseView;

public class NetTrafficMain extends Activity{

	private LazyViewPager viewPager;
	private ViewPagerTab pagerTab;
	
	private NetTrafficTabBaseView rankingView;
	private NetTrafficTabBaseView monitorView;
	private NetTrafficTabBaseView firewallView;
	
	private View backBtn,llBottom;
	
	private final int[] names = { R.string.net_traffic_main_tab_ranking, 
			R.string.net_traffic_main_tab_monitor, R.string.net_traffic_main_tab_firewall};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.net_traffic_main);
		
		viewPager = (LazyViewPager) findViewById(R.id.nettraffic_viewpager);
		viewPager.addView(createRankingView());
		viewPager.addView(createMonitorView());
		viewPager.addView(createFirewallView());
		
        pagerTab = (ViewPagerTab) findViewById(R.id.nettraffic_pagertab);
		pagerTab.addTitle(new String[]{
				getResources().getString(names[0]),
				getResources().getString(names[1]),
				getResources().getString(names[2])});
		
		pagerTab.setViewpager(viewPager);
		viewPager.setTab(pagerTab);
		
		backBtn = findViewById(R.id.back_btn);
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View paramView) {
				finish();
			}
		});
		
		llBottom = findViewById(R.id.ll_bottom);
		llBottom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View paramView) {
			}
		});
	}
	
	private View createRankingView(){
		if (rankingView==null){
			rankingView = new NetTrafficRankingView(this);
		}
		return rankingView;
	}
	
	private View createMonitorView(){
		if (monitorView==null){
			monitorView = new NetTrafficTabBaseView(this);
		}
		return monitorView;
	}	
	
	private View createFirewallView(){
		if (firewallView==null){
			firewallView = new NetTrafficFirewallView(this);
		}
		return firewallView;
	}
}
