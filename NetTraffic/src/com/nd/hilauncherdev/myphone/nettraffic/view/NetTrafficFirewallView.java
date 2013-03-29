package com.nd.hilauncherdev.myphone.nettraffic.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import com.felix.demo.R;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficRankingGprsWifiAccessor;
import com.nd.hilauncherdev.myphone.nettraffic.firewall.FireWallMainActivity;
import com.nd.hilauncherdev.myphone.nettraffic.util.CrashTool;
import com.nd.hilauncherdev.myphone.nettraffic.util.ThreadUtil;
import com.nd.hilauncherdev.myphone.nettraffic.view.base.NetTrafficTabBaseView;

public class NetTrafficFirewallView extends NetTrafficTabBaseView{

	private boolean bStartLoad = false;

	private Context ctx;
	
	private LayoutInflater mInflater;
	
	private View wait_layout;
	
	private boolean isRefreshView = true;
	
	private int sleepTime = 3 * 1000;
	
	private Handler handler = new Handler();
	
	private Runnable task = new Runnable() {

		@Override
		public void run() {
			
			ThreadUtil.executeNetTraffic(new Runnable() {
				@Override
				public void run() {
					try { 
						/*
						if (isRefreshView) {
							handler.postDelayed(task, sleepTime);
						}
						*/
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	};
	
	public NetTrafficFirewallView(Context context) {
		super(context);
		
		ctx = context;
		mInflater = LayoutInflater.from( ctx );
		LayoutInflater.from( ctx ).inflate(R.layout.net_traffic_ranking_list, this);
		setupViews();
	}
	
	private void setupViews() {
		
		wait_layout = this.findViewById( R.id.wait_layout );
		wait_layout.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 判断是否加载过
	 * @return
	 */
	public boolean isStartLoad() {
		return bStartLoad;
	}
	
	/**
	 * 开始加载数据
	 */
	public void startLoadData(){
		bStartLoad = true;
		
		ThreadUtil.executeNetTraffic( new Runnable(){
			public void run() {
				try {
					NetTrafficRankingGprsWifiAccessor.getInstance(ctx).
    					insertALLAppNetTrafficToDB(CrashTool.getNetType(ctx), CrashTool.getStringDate());
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						wait_layout.setVisibility(View.GONE);

						Intent intent = new Intent(ctx, FireWallMainActivity.class);
						ctx.startActivity(intent);
					}
				});
			}
		});
	}
	
	@Override
	public void onResume() {
		isRefreshView = true;
		handler.postDelayed(task, sleepTime);
	}
	
	@Override
	public void onPause() {
		isRefreshView = false;
		handler.removeCallbacks(task);
	}
	
	@Override
	public void onDestroy() {
		isRefreshView = false;
		handler.removeCallbacks(task);
	}
}
