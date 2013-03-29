package com.nd.hilauncherdev.myphone.nettraffic.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.nd.hilauncherdev.framework.view.LazyViewPager;


public class NetTrafficViewPager  extends LazyViewPager{

	public NetTrafficViewPager(Context context) {
		super(context);
	}

	public NetTrafficViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NetTrafficViewPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected boolean loadContentData(int screen) {

		View view = this.getChildAt(screen);
		switch (screen) {
			case 0:
				if ( view instanceof NetTrafficRankingView ) {
					NetTrafficRankingView netTrafficRankingView = (NetTrafficRankingView)view; 
					if ( !netTrafficRankingView.isStartLoad() ){
						netTrafficRankingView.startLoadData();
					}
				}
				break;
			case 1:
				
				break;
			case 2:
				if ( view instanceof NetTrafficFirewallView ) {
					NetTrafficFirewallView netTrafficFirewallView = (NetTrafficFirewallView)view; 
					if ( !netTrafficFirewallView.isStartLoad() ){
						netTrafficFirewallView.startLoadData();
					}
				}
				break;
		}
		
		return false;
	}

	@Override
	public void refreshView(int screen, String tab) {
		
	}

}
