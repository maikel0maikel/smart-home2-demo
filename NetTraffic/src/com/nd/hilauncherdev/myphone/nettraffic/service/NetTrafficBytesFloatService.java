package com.nd.hilauncherdev.myphone.nettraffic.service;


import java.text.DecimalFormat;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.felix.demo.R;
import com.felix.demo.activity.NetTrafficBytesAccessor;
import com.felix.demo.activity.NetTrafficBytesMain;
import com.nd.hilauncherdev.myphone.nettraffic.activity.NetTrafficRankingMain;
import com.nd.hilauncherdev.myphone.nettraffic.receiver.NetTrafficConnectivityChangeBroadcast;

public class NetTrafficBytesFloatService extends Service {

	WindowManager wm = null;
	WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
	View view;
	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;
	int state;
	TextView tvWifiUser;
	TextView tvGprsUse;
	ImageView iv;
	private float StartX;
	private float StartY;
	int delaytime=5000;
	
	int ONGOING_NOTIFICATION = 9100;
	
	AlarmManager alarmManager;
	PendingIntent pendingIntent;
	private static final long DAY_MIN = 24 * 60 * 60 * 1000; // 一天时间
	
	@Override
	public void onCreate() {
		Log.d("NetTrafficBytesFloatService", "onCreate");
		super.onCreate();
		view = LayoutInflater.from(this).inflate(R.layout.net_traffic_floating, null);
		tvGprsUse = (TextView) view.findViewById(R.id.gprs_use);
		tvWifiUser = (TextView) view.findViewById(R.id.wifi_use);

		dataRefresh();
		
		iv = (ImageView) view.findViewById(R.id.img2);
		iv.setVisibility(View.GONE);
		createView();
		handler.postDelayed(task, delaytime);
		
		//启动每天12点定时器
		Intent intent = new Intent(this,NetTrafficConnectivityChangeBroadcast.class);  
		intent.setAction("netTrafficAlarm");
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);  
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);  
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getAlarmStartTime(), DAY_MIN, pendingIntent);    
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.d("NetTrafficBytesFloatService", "onStartCommand");
		
		//使用这招可以达到显示在安全中心，但是不在通知栏显示
		//Notification notification = new Notification(0, "小黑流量监控", System.currentTimeMillis());
		Notification notification = new Notification(R.drawable.ic_launcher, "小黑流量监控", System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, NetTrafficRankingMain.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, "流量标题", "流量内容文本", pendingIntent);
		startForeground(ONGOING_NOTIFICATION, notification);
		//startForeground(0, notification);  使用id 0 可以达到隐藏的效果，但是安全中心没有
		//startForeground(ONGOING_NOTIFICATION, new Notification()); 使用id 0 可以达到隐藏的效果，但是安全中心没有
		
		return super.onStartCommand(intent, flags, startId);
	}

	private void createView() {
		
		wm = (WindowManager) getApplicationContext().getSystemService("window");
		
		//wmParams.type = 2002;
		//wmParams.flags |= 8;
		wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;  
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; 
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		wmParams.x = 0;
		wmParams.y = 0;
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.format = 1;
		
		wm.addView(view, wmParams);

		view.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				x = event.getRawX();
				y = event.getRawY() - 25; 
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					state = MotionEvent.ACTION_DOWN;
					StartX = x;
					StartY = y;
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					
					break;
				case MotionEvent.ACTION_MOVE:
					state = MotionEvent.ACTION_MOVE;
					updateViewPosition();
					break;

				case MotionEvent.ACTION_UP:
					state = MotionEvent.ACTION_UP;

					updateViewPosition();
					showImg();
					mTouchStartX = mTouchStartY = 0;
					break;
				}
				return true;
			}
		});

		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				NetTrafficBytesMain.setFloatFlag(NetTrafficBytesFloatService.this, false);
				
				Intent serviceStop = new Intent();
				serviceStop.setClass(NetTrafficBytesFloatService.this, NetTrafficBytesFloatService.class);
				stopService(serviceStop);
			}
		});

	}

	public void showImg() {
		if (Math.abs(x - StartX) < 1.5 && Math.abs(y - StartY) < 1.5 && !iv.isShown()) {
			iv.setVisibility(View.VISIBLE);
		} else if (iv.isShown()) {
			iv.setVisibility(View.GONE);
		}
	}

	private Handler handler = new Handler();
	private Runnable task = new Runnable() {
		public void run() {
			dataRefresh();
			handler.postDelayed(this, delaytime);
			wm.updateViewLayout(view, wmParams);
		}
	};

	public void dataRefresh() {
		
		if (NetTrafficBytesAccessor.netTrafficBytesResult==null || NetTrafficBytesAccessor.netTrafficWifiResult==null){
			
			NetTrafficBytesAccessor.initTrafficBytes(this);			
		}
		
		// 数据显示到布局上
		if (NetTrafficBytesAccessor.netTrafficBytesResult != null
				&& NetTrafficBytesAccessor.netTrafficWifiResult != null) {

			
			
			tvGprsUse.setText( unitHandler(NetTrafficBytesAccessor.netTrafficBytesResult.dateBytesAll)+" M/"+
					unitHandler(NetTrafficBytesAccessor.netTrafficBytesResult.monthBytesAll)+"M" );
	
			tvWifiUser.setText( unitHandler(NetTrafficBytesAccessor.netTrafficWifiResult.dateBytesAll)+"M/"+
					unitHandler(NetTrafficBytesAccessor.netTrafficWifiResult.monthBytesAll)+"M" );			
		}		
	}

	private void updateViewPosition() {
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY);
		wm.updateViewLayout(view, wmParams);
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacks(task);
		Log.d("NetTrafficBytesFloatService", "onDestroy");
		wm.removeView(view);
		
		stopForeground(true);
		
		if (alarmManager!=null && pendingIntent!=null){
			alarmManager.cancel(pendingIntent);
		}
		
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private String unitHandler(float count) {
		String value = null;
		float floatnum = count/1024f; 
		DecimalFormat format = new DecimalFormat("0.00");
		value = format.format(floatnum);
		return value;
	}
	
	/**
	 * 获取定时器启动时间(每天的23点58分记录记录一下当天的流量)
	 * @return
	 */
	private long getAlarmStartTime(){
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 55);
		
		return calendar.getTimeInMillis();
	}
}
