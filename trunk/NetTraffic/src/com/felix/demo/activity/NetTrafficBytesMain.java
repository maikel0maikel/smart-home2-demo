package com.felix.demo.activity;

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
import com.nd.hilauncherdev.kitset.util.ThreadUtil;
import com.nd.hilauncherdev.myphone.nettraffic.activity.NetTrafficRankingGprsWifiMain;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficBytesAccessor;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficBytesItem;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficRankingGprsWifiAccessor;
import com.nd.hilauncherdev.myphone.nettraffic.service.NetTrafficBytesFloatService;
import com.nd.hilauncherdev.myphone.nettraffic.util.CrashTool;
import com.nd.hilauncherdev.myphone.nettraffic.util.NetTrafficInitTool;

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
		if ( getFloatFlag(this) ){
			Intent service = new Intent();
    		service.setClass(NetTrafficBytesMain.this, NetTrafficBytesFloatService.class);		
    		startService(service);
		}
		
		updateViewTimer = new Timer();
		updateViewTimer.schedule(new TimerTask() {

			@Override
			public void run() {				

				Message message = handler.obtainMessage(0, null);
	            handler.sendMessage(message);
			}
		}, 0, 1000 * 3);
		
		refrashView();	
		
		//实际应用放到service中去.
		ThreadUtil.executeNetTraffic(new Runnable() {
			@Override
			public void run() {
				NetTrafficInitTool.getCacheAppMap(NetTrafficBytesMain.this);
			}
		});
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
            	
            	setFloatFlag(NetTrafficBytesMain.this, true);
            	
            	Intent service = new Intent();
        		service.setClass(NetTrafficBytesMain.this, NetTrafficBytesFloatService.class);		
        		startService(service);
            }
        });
        
        btnstop = (Button) findViewById(R.id.btnstop);
        btnstop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	setFloatFlag(NetTrafficBytesMain.this, false);
            	
            	Intent serviceStop = new Intent();
        		serviceStop.setClass(NetTrafficBytesMain.this, NetTrafficBytesFloatService.class);
        		startService(serviceStop);
            }
        });
        
        btncheck_notify = (Button) findViewById(R.id.btncheck_notify);
        btncheck_notify.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
            
            }
        });
        
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
		if (NetTrafficBytesAccessor.netTrafficBytesResult != null
				&& NetTrafficBytesAccessor.netTrafficWifiResult != null) {

			netTrafficBytesGprsDay
					.setText(getString(
							R.string.net_traffic_bytes_gprs_day,
							unitHandler(NetTrafficBytesAccessor.netTrafficBytesResult.dateBytesAll)));

			netTrafficBytesGprsMonth
					.setText(getString(
							R.string.net_traffic_bytes_gprs_month,
							unitHandler(NetTrafficBytesAccessor.netTrafficBytesResult.monthBytesAll)));

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


	
    public static boolean getFloatFlag(Context context){
    	
    	final SharedPreferences prefs = context.getSharedPreferences("float_flag", Activity.MODE_PRIVATE);
    	return prefs.getBoolean("float", false);
    }
    
    public static void setFloatFlag(Context context, boolean floatFlag){
    	
    	final SharedPreferences prefs = context.getSharedPreferences("float_flag", Activity.MODE_PRIVATE);
    	Editor editor = prefs.edit();
    	editor.putBoolean("float", floatFlag);
    	editor.commit();
    }
		    
}
