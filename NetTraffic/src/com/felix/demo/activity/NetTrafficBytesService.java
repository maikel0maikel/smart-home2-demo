package com.felix.demo.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class NetTrafficBytesService extends Service {
	private Timer timer;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				
				NetTrafficBytesAccessor.logRealTimeTrafficBytes(getBaseContext());
				
			}
		}, 0, 1000 * 5);

		Log.d("NetTrafficBytesService", "NetTrafficBytesService created.");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
		Log.d("NetTrafficBytesService", "NetTrafficBytesService shutdown.");
	}
}
