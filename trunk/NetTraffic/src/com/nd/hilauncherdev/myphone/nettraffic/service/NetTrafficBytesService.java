package com.nd.hilauncherdev.myphone.nettraffic.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.felix.demo.R;
import com.nd.hilauncherdev.myphone.nettraffic.activity.NetTrafficRankingMain;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficRankingGprsWifiAccessor;

public class NetTrafficBytesService extends Service {
	private Timer timer;
	
	int ONGOING_NOTIFICATION = 9100;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				
				//NetTrafficRankingGprsWifiAccessor.getInstance(getBaseContext()).insertALLAppNetTrafficToDB();
				//NetTrafficBytesAccessor.logRealTimeTrafficBytes(getBaseContext());
			}
		}, 0, 1000 * 60);

		Log.d("NetTrafficBytesService", "NetTrafficBytesService created.");
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		 
		//使用这招可以达到显示在安全中心，但是不在通知栏显示
		//Notification notification = new Notification(0, "小黑流量监控", System.currentTimeMillis());
		Notification notification = new Notification(R.drawable.ic_launcher, "小黑流量监控GPRS", System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, NetTrafficRankingMain.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, "流量标题", "流量内容文本GPRS", pendingIntent);
		startForeground(ONGOING_NOTIFICATION, notification);
		//startForeground(0, notification);  使用id 0 可以达到隐藏的效果，但是安全中心没有
		//startForeground(ONGOING_NOTIFICATION, new Notification()); 使用id 0 可以达到隐藏的效果，但是安全中心没有
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
		
		stopForeground(true);
		
		Log.d("NetTrafficBytesService", "NetTrafficBytesService shutdown.");
	}
}
