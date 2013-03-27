package com.nd.hilauncherdev.myphone.nettraffic.service;


import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
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
import com.nd.hilauncherdev.framework.ViewFactory;
import com.nd.hilauncherdev.framework.view.dialog.CommonDialog;
import com.nd.hilauncherdev.myphone.nettraffic.activity.NetTrafficBytesMain;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficBytesAccessor;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficBytesItem;
import com.nd.hilauncherdev.myphone.nettraffic.receiver.NetTrafficConnectivityChangeBroadcast;
import com.nd.hilauncherdev.myphone.nettraffic.util.CrashTool;
import com.nd.hilauncherdev.myphone.nettraffic.util.NetTrafficInitTool;
import com.nd.hilauncherdev.myphone.nettraffic.util.NetTrafficSettingTool;
import com.nd.hilauncherdev.myphone.nettraffic.util.NetTrafficUnitTool;
import com.nd.hilauncherdev.myphone.nettraffic.util.ThreadUtil;

/**
 * 流量监控服务 
 * 1、启动通知栏流量框
 * 2、浮动展示流量View
 * 
 * 优化空间
 * 1、无网络 取消Time刷新及浮动块
 * 2、有网络在但是无流量达半分钟，则取消浮动块
 * @author cfb
 */
public class NetTrafficBytesFloatService extends Service {

	private AlarmManager alarmManager;
	private NotificationManager nManager;
	private PendingIntent pendingIntent;
	/**通知栏消息ID*/ 
	private int NOTIFICATION_ID = 9100;
	
	private WindowManager wm = null;
	private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
	private View floatView;
	private TextView tvWifiUse;
	private TextView tvGprsUse;
	/**网络速度*/
	private TextView speedUse;
	/**网络类型图标*/
	private ImageView netTypeImg;
	
	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;
	private float mWinTouchStartX;
	private float mWinTouchStartY;

	/**是否显示浮动框*/
	private boolean isVisualFloatView = false;
	/**是否定时刷新*/
	private boolean isRefreshView = true;
	/**定时刷新的频率*/
	private int DELAY_TIME = 3 * 1000;
	/**软件明细信息的记录频率*/
	//private static final long DAY_MIN = 24 * 60 * 60 * 1000; // 一天时间
	private static final long ONE_HOUR = 60 * 60 * 1000; // 一小时
	/**上次的流量总和*/
	private float lastNetBytesGprs = -1f;
	private float lastNetBytesWifi = -1f;
	/**上次通知栏提醒的Gprs流量*/
	private float lastNotifyBytesGprs = -1f;
	
	/**流量超标是否提醒过*/
	public boolean hasDayWarning = false;
	public boolean hasMonthWarning = false;
	
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);  
		nManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
		wm = (WindowManager) getApplicationContext().getSystemService(Service.WINDOW_SERVICE);
		
		floatView = LayoutInflater.from(this).inflate(R.layout.net_traffic_floating, null);
		tvGprsUse = (TextView) floatView.findViewById(R.id.gprs_use);
		tvWifiUse = (TextView) floatView.findViewById(R.id.wifi_use);
		speedUse =  (TextView) floatView.findViewById(R.id.speed_use);
		netTypeImg = (ImageView) floatView.findViewById(R.id.nettypeimg);
		
		Intent intent = new Intent(this,NetTrafficConnectivityChangeBroadcast.class);  
		intent.setAction(NetTrafficConnectivityChangeBroadcast.ALARM_ACTION);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);  
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getAlarmStartTime(), ONE_HOUR, pendingIntent);    
     
        startForeground(NOTIFICATION_ID, getNetTrafficNotification());
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		boolean tmpTrafficOpen = NetTrafficSettingTool.getPrefsBoolean(getBaseContext(), NetTrafficSettingTool.TrafficOpen, true);
		boolean tmpVisualFloat = NetTrafficSettingTool.getPrefsBoolean(getBaseContext(), NetTrafficSettingTool.isVisualFloatKey, false);
        hasDayWarning = NetTrafficSettingTool.getPrefsBoolean(getBaseContext(), NetTrafficSettingTool.isWarningDayKey, hasDayWarning);
        hasMonthWarning = NetTrafficSettingTool.getPrefsBoolean(getBaseContext(), NetTrafficSettingTool.isWarningMonthKey, hasMonthWarning); 
        
		if (!tmpTrafficOpen){
			tmpVisualFloat = false;
			hasDayWarning = true;
			hasMonthWarning = true;
			nManager.notify(NOTIFICATION_ID, getNetTrafficNotification());
			isRefreshView = false;
			handler.removeCallbacks(task);
		}else{
			nManager.notify(NOTIFICATION_ID, getNetTrafficNotification());
			isRefreshView = true;
			ThreadUtil.executeNetTraffic(new Runnable() {
				@Override
				public void run() {
					NetTrafficInitTool.getCacheAppMap(NetTrafficBytesFloatService.this);
					handler.postDelayed(task, DELAY_TIME);
				}
			});
		}
		
		if ( tmpVisualFloat ) {
			if ( !isVisualFloatView ) {
				createView();
			}
		}else{
			if ( isVisualFloatView ) {
				removeView();
			}
		}
		
		//TODO 判断网络是否可用 
		//     是否关闭定时流量记录 和浮动框
		/*
		if ( CrashTool.isNetworkAvailable(this) ){
			if ( !isRefreshView ){
				isRefreshView = true;
				handler.postDelayed(task, delaytime);
			}
		}else{
			if ( isRefreshView ) {
				isRefreshView = false;
			}
		}
		*/
		return super.onStartCommand(intent, flags, startId);
	}

	private void createView() {
		
//		wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;  
//		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; 
		
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE; // 设置window type
		wmParams.flags |= 8;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
		wmParams.x = (int) NetTrafficSettingTool.getPrefsFloat(getBaseContext(), NetTrafficSettingTool.TOUCH_LAST_X, 0);
		wmParams.y = (int) NetTrafficSettingTool.getPrefsFloat(getBaseContext(), NetTrafficSettingTool.TOUCH_LAST_Y, 0);
		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

		// 显示floatView图像
		wm.addView(floatView, wmParams);
		
		//浮动框已显示
		isVisualFloatView = true;
		
		floatView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				x = event.getRawX();
				y = event.getRawY() - 25;// 25是系统状态栏的高度
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					mWinTouchStartX = x;
					mWinTouchStartY = y;
					break;
				case MotionEvent.ACTION_MOVE:
					updateViewPosition();
					break;
				case MotionEvent.ACTION_UP:
					if (Math.abs(mWinTouchStartX - x) > 5 || Math.abs(mWinTouchStartY - y) > 5) {
						updateViewPosition();
					} else {
						if (Math.abs(x - mWinTouchStartX) < 2 && Math.abs(y - mWinTouchStartY) < 2 && !netTypeImg.isShown()) {
							netTypeImg.setVisibility(View.VISIBLE);
						} else if (netTypeImg.isShown()) {
							netTypeImg.setVisibility(View.INVISIBLE);
						}
					}
					mTouchStartX = mTouchStartY = 0;
					mWinTouchStartX = mWinTouchStartY = 0;
					break;
				}
				return true;
			}
		});

		netTypeImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (netTypeImg.isShown()) {
					netTypeImg.setVisibility(View.INVISIBLE);
				}
				
				NetTrafficSettingTool.setPrefsBoolean(getBaseContext(), NetTrafficSettingTool.isVisualFloatKey, false);
				Intent serviceStop = new Intent();
				serviceStop.setClass(NetTrafficBytesFloatService.this, NetTrafficBytesFloatService.class);
				startService(serviceStop);
			}
		});
	}

	private void removeView(){
		try {
			isVisualFloatView = false;
			NetTrafficSettingTool.setPrefsFloat(getBaseContext(), NetTrafficSettingTool.TOUCH_LAST_X, x);
			NetTrafficSettingTool.setPrefsFloat(getBaseContext(), NetTrafficSettingTool.TOUCH_LAST_Y, y);
			wm.removeView(floatView);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Handler handler = new Handler();
	private Runnable task = new Runnable() {
		public void run() {
			try {
				Log.d("NetTrafficBytesFloatService", " task 123");
				//TODO 判断无网络状态,如果无网络状态持续1分钟则停止刷新
				ThreadUtil.executeNetTraffic(new Runnable() {
					@Override
					public void run() {
						NetTrafficBytesAccessor.getInstance(getBaseContext()).insertNetTrafficBytesToDB(CrashTool.getStringDate());
						
						//TODO 判断流量是否隔天了
						
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								//判断网络类型再更新
								if (isVisualFloatView) {
									wm.updateViewLayout(floatView, wmParams);
								}
								if ( isRefreshView ) {
									if ( lastNotifyBytesGprs!=NetTrafficBytesAccessor.netTrafficGprsResult.dateBytesAll ) {
										lastNotifyBytesGprs = NetTrafficBytesAccessor.netTrafficGprsResult.dateBytesAll;
										nManager.notify(NOTIFICATION_ID, getNetTrafficNotification());
									}
									handler.postDelayed(task, DELAY_TIME);
								}
								dataRefresh();
							}
						});
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 读取流量并设置显示
	 */
	public void dataRefresh() {
		
		speedUse.setText("0B/s");
		if ( CrashTool.isNetworkAvailable(getBaseContext()) ){
			if ( CrashTool.isWifiNetwork(getBaseContext()) ){
				if ( lastNetBytesWifi>0 && lastNetBytesWifi<NetTrafficBytesAccessor.netTrafficWifiResult.dateBytesAll){
					float speed = NetTrafficBytesAccessor.netTrafficWifiResult.dateBytesAll-lastNetBytesWifi;
					speedUse.setText( NetTrafficUnitTool.netTrafficUnitHandler(speed/3)+"/s" );
				}
				NetTrafficUnitTool.setNetTypeTextViewDrawable(speedUse, true, NetTrafficBytesItem.DEV_WIFI);
				lastNetBytesWifi = NetTrafficBytesAccessor.netTrafficWifiResult.dateBytesAll;
			}else{
				if ( lastNetBytesGprs>0 && lastNetBytesGprs<NetTrafficBytesAccessor.netTrafficGprsResult.dateBytesAll){
					float speed = NetTrafficBytesAccessor.netTrafficGprsResult.dateBytesAll-lastNetBytesGprs;
					speedUse.setText( NetTrafficUnitTool.netTrafficUnitHandler(speed/3)+"/s" );
				}
				NetTrafficUnitTool.setNetTypeTextViewDrawable(speedUse, true, NetTrafficBytesItem.DEV_GPRS);
				lastNetBytesGprs = NetTrafficBytesAccessor.netTrafficGprsResult.dateBytesAll;
			}

			checkNetTrafficWarning();
		}

		tvGprsUse.setText( NetTrafficUnitTool.netTrafficUnitHandler(NetTrafficBytesAccessor.netTrafficGprsResult.dateBytesAll)+" / "+
				NetTrafficUnitTool.netTrafficUnitHandler(NetTrafficBytesAccessor.netTrafficGprsResult.monthBytesAll) );
		tvWifiUse.setText( NetTrafficUnitTool.netTrafficUnitHandler(NetTrafficBytesAccessor.netTrafficWifiResult.dateBytesAll)+" / "+
				NetTrafficUnitTool.netTrafficUnitHandler(NetTrafficBytesAccessor.netTrafficWifiResult.monthBytesAll) );			
	}

	private void updateViewPosition() {
		try {
			wmParams.x = (int) (x - mTouchStartX);
			wmParams.y = (int) (y - mTouchStartY);
			wm.updateViewLayout(floatView, wmParams);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		Log.d("NetTrafficBytesFloatService", "onDestroy");
		
		handler.removeCallbacks(task);
		if (alarmManager!=null && pendingIntent!=null){
			alarmManager.cancel(pendingIntent);
		}
		if ( isVisualFloatView ) {
			removeView();		
		}
		nManager.cancel(NOTIFICATION_ID);
		stopForeground(true);
		
		super.onDestroy();
	}
	
	
	/**
	 * 获取定时器启动时间(每天的23点57分记录记录一下当天的流量)
	 * @return
	 */
	private long getAlarmStartTime(){
		
		Calendar calendar = Calendar.getInstance();
		//calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 57);
		return calendar.getTimeInMillis();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Notification getNetTrafficNotification(){
		Notification notification = new Notification(R.drawable.ic_launcher, "91流量监控", System.currentTimeMillis());
		notification.flags |= Notification.FLAG_ONGOING_EVENT;  
		Intent notificationIntent = new Intent(this, NetTrafficBytesMain.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		//TODO 判断是否需要重新获取今天及本月数据流量
		boolean tmpTrafficOpen = NetTrafficSettingTool.getPrefsBoolean(getBaseContext(), NetTrafficSettingTool.TrafficOpen, true);
		if ( tmpTrafficOpen ) {
			notification.setLatestEventInfo(this, "91流量监控", 
					"今日="+NetTrafficUnitTool.netTrafficUnitHandler(NetTrafficBytesAccessor.netTrafficGprsResult.dateBytesAll)
					+";本月"+NetTrafficUnitTool.netTrafficUnitHandler(NetTrafficBytesAccessor.netTrafficGprsResult.monthBytesAll)
					+";剩余", pendingIntent);	
		}else{
			notification.setLatestEventInfo(this, "91流量监控", 
					"未启用流量监控", pendingIntent);	
		}
		
		return notification;
	}
	
	private void checkNetTrafficWarning(){
		
		if ( NetTrafficBytesAccessor.netTrafficWifiResult.dateBytesAll>(25*1024f) && !hasDayWarning) {
			hasDayWarning = true;
			NetTrafficSettingTool.setPrefsBoolean(getBaseContext(), NetTrafficSettingTool.isWarningDayKey, hasDayWarning); 
			
			CommonDialog commonDialog = ViewFactory.getAlertDialog(getBaseContext(), -1, "91桌面流量监控提醒",
					"您今天使用的流量已经超过限定额度啦，建议您关闭2G/3G网络连接,以免流量超支。",
					"继续使用", "关闭网络", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							
							dialog.dismiss();
						}
					}, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							//最好再来个提示吧。
							dialog.dismiss();
						}
					});
			commonDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			commonDialog.show();
		}
		
		if ( NetTrafficBytesAccessor.netTrafficWifiResult.monthBytesAll>(100*1024f) && !hasMonthWarning) {
			hasMonthWarning = true;
			NetTrafficSettingTool.setPrefsBoolean(getBaseContext(), NetTrafficSettingTool.isWarningMonthKey, hasMonthWarning); 
			
			CommonDialog commonDialog = ViewFactory.getAlertDialog(getBaseContext(), -1, "91桌面流量监控提醒",
					"您本月使用的流量已经超过限定额度啦，建议您关闭2G/3G网络连接,以免流量超支。",
					"继续使用", "关闭网络", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							
							dialog.dismiss();
						}
					}, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							//最好再来个提示吧。
							dialog.dismiss();
						}
					});
			commonDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			commonDialog.show();
		}
	}
}
