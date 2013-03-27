package com.nd.hilauncherdev.myphone.nettraffic.activity;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.felix.demo.R;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficBytesAccessor;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficBytesItem;
import com.nd.hilauncherdev.myphone.nettraffic.service.NetTrafficBytesFloatService;
import com.nd.hilauncherdev.myphone.nettraffic.util.NetTrafficSettingTool;

public class NetTrafficBytesMain  extends Activity { 
	
	private Timer updateViewTimer;
	
	private TextView netTrafficBytesGprsDay;
	
	private TextView netTrafficBytesGprsMonth;
	
	private TextView netTrafficBytesWifiDay;
	
	private TextView netTrafficBytesWifiMonth;
	
	private Button startRankingWifiToday;
	private Button startRankingWifiAll;
	private Button startRankingGprsToday;
	private Button startRankingGprsAll;
	
	private Button btnstart;
	private Button btnstop;	
	private Button btncheck_notify;
	private Button btnstart_service;
	private Button btnclose_service;
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			refrashView();	
		}
	};
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.net_traffic_bytes_main);
		
		initView();
		
		//根据配置文件看是否启动悬浮窗口
		Intent service = new Intent();
		service.setClass(NetTrafficBytesMain.this, NetTrafficBytesFloatService.class);		
		startService(service);
		
		updateViewTimer = new Timer();
		updateViewTimer.schedule(new TimerTask() {

			@Override
			public void run() {				

				Message message = handler.obtainMessage(0, null);
	            handler.sendMessage(message);
			}
		}, 0, 1000 * 5);
		
		refrashView();	
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		
		if (updateViewTimer != null) {
			updateViewTimer.cancel();
		}
	}
	
	public void initView(){
		
		netTrafficBytesGprsDay = (TextView)findViewById(R.id.net_traffic_bytes_gprs_day);
		netTrafficBytesGprsMonth = (TextView)findViewById(R.id.net_traffic_bytes_gprs_month);
		netTrafficBytesWifiDay = (TextView)findViewById(R.id.net_traffic_bytes_wifi_day);
		netTrafficBytesWifiMonth = (TextView)findViewById(R.id.net_traffic_bytes_wifi_month);		
		
		startRankingWifiToday = (Button)findViewById(R.id.startRankingWifiToday);
		startRankingWifiAll = (Button)findViewById(R.id.startRankingWifiAll);
		startRankingGprsToday = (Button)findViewById(R.id.startRankingGprsToday);
		startRankingGprsAll = (Button)findViewById(R.id.startRankingGprsAll);
		
		startRankingWifiToday.setOnClickListener(listener);//设置监听
		startRankingWifiAll.setOnClickListener(listener);//设置监听
		startRankingGprsToday.setOnClickListener(listener);//设置监听
		startRankingGprsAll.setOnClickListener(listener);//设置监听
		
		btnstart = (Button) findViewById(R.id.btnstart);
        btnstart.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	NetTrafficSettingTool.setPrefsBoolean(getBaseContext(), NetTrafficSettingTool.isVisualFloatKey, true);
            	
            	Intent service = new Intent();
        		service.setClass(NetTrafficBytesMain.this, NetTrafficBytesFloatService.class);		
        		startService(service);
            }
        });
        
        btnstop = (Button) findViewById(R.id.btnstop);
        btnstop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	NetTrafficSettingTool.setPrefsBoolean(getBaseContext(), NetTrafficSettingTool.isVisualFloatKey, false);
            	
            	Intent serviceStop = new Intent();
        		serviceStop.setClass(NetTrafficBytesMain.this, NetTrafficBytesFloatService.class);
        		startService(serviceStop);
            }
        });
        
        btncheck_notify = (Button) findViewById(R.id.btncheck_notify);
        btncheck_notify.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
            	//ApkTools.installApplication(getBaseContext(), "/sdcard/NetTraffic.apk");
            	boolean trafficOpen = !NetTrafficSettingTool.getPrefsBoolean(getBaseContext(), NetTrafficSettingTool.TrafficOpen, true);
            	NetTrafficSettingTool.setPrefsBoolean(getBaseContext(), NetTrafficSettingTool.TrafficOpen, trafficOpen);
            	if (trafficOpen){
            		btncheck_notify.setText("关闭流量监控");
            	}else{
            		btncheck_notify.setText("开启流量监控");
            	}
            	Intent serviceStop = new Intent();
        		serviceStop.setClass(NetTrafficBytesMain.this, NetTrafficBytesFloatService.class);
        		startService(serviceStop);
            }
        });
        
        btnstart_service = (Button) findViewById(R.id.btnstart_service);
        btnstart_service.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent serviceStop = new Intent();
        		serviceStop.setClass(NetTrafficBytesMain.this, NetTrafficBytesFloatService.class);
        		startService(serviceStop);
            }
        });
        
        btnclose_service = (Button) findViewById(R.id.btnclose_service);
        btnclose_service.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent serviceStop = new Intent();
        		serviceStop.setClass(NetTrafficBytesMain.this, NetTrafficBytesFloatService.class);
        		stopService(serviceStop);
            }
        });
        
        boolean trafficOpen = NetTrafficSettingTool.getPrefsBoolean(getBaseContext(), NetTrafficSettingTool.TrafficOpen, true);
        if (trafficOpen){
    		btncheck_notify.setText("关闭流量监控");
    	}else{
    		btncheck_notify.setText("开启流量监控");
    	}
	}
	
	Button.OnClickListener listener = new Button.OnClickListener() {// 创建监听对象
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(NetTrafficBytesMain.this, NetTrafficRankingGprsWifiMain.class);
			switch (v.getId()) {
				case R.id.startRankingWifiToday:
					intent.putExtra("devTaype", NetTrafficBytesItem.DEV_WIFI);
					intent.putExtra("isAll", false);
					break;
	
				case R.id.startRankingWifiAll:
					intent.putExtra("devTaype", NetTrafficBytesItem.DEV_WIFI);
					intent.putExtra("isAll", true);
					break;
	
				case R.id.startRankingGprsToday:
					intent.putExtra("devTaype", NetTrafficBytesItem.DEV_GPRS);
					intent.putExtra("isAll", false);
					break;
	
				case R.id.startRankingGprsAll:
					intent.putExtra("devTaype", NetTrafficBytesItem.DEV_GPRS);
					intent.putExtra("isAll", true);
					break;				
				default:
					break;
			}
			NetTrafficBytesMain.this.startActivity(intent);				
		}
	};
	
	
	private String unitHandler(float count) {
		String value = null;
		float floatnum = count/1024f; 
		DecimalFormat format = new DecimalFormat("0.00");
		value = format.format(floatnum);
		return value;
	}
	
	private void refrashView(){
		
		// 数据显示到布局上
			netTrafficBytesGprsDay
					.setText(getString(
							R.string.net_traffic_bytes_gprs_day,
							unitHandler(NetTrafficBytesAccessor.netTrafficGprsResult.dateBytesAll)));

			netTrafficBytesGprsMonth
					.setText(getString(
							R.string.net_traffic_bytes_gprs_month,
							unitHandler(NetTrafficBytesAccessor.netTrafficGprsResult.monthBytesAll)));

			netTrafficBytesWifiDay
					.setText(getString(
							R.string.net_traffic_bytes_wifi_day,
							unitHandler(NetTrafficBytesAccessor.netTrafficWifiResult.dateBytesAll)));

			netTrafficBytesWifiMonth
					.setText(getString(
							R.string.net_traffic_bytes_wifi_month,
							unitHandler(NetTrafficBytesAccessor.netTrafficWifiResult.monthBytesAll)));
	}
		    
}
