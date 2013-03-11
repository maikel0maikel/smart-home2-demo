package com.felix.demo.phonehelper.receiver;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class AskCallStateThread extends Thread

{

	private static final String TAG = "PhoneStateReceiver";

	public AskCallStateThread(Context aContext) {

		mContext = aContext;

	}

	Context mContext = null;

	private Timer mTimer = new Timer(true);

	private TimerTask mTimerTask = new TimerTask() {

		@Override
		public void run() {

			TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Service.TELEPHONY_SERVICE);
			Log.i(TAG, "mTelManager.getDataState = " + telephonyManager.getDataState());
			
			int nState = telephonyManager.getCallState();
			Log.i(TAG, "mTelManager.getCallState = " + nState);
			
			int dataActivty = telephonyManager.getDataActivity();
			Log.i(TAG, "mTelManager.getDataActivity = " + dataActivty);
			
			int netWorkType = telephonyManager.getNetworkType();
			Log.i(TAG, "mTelManager.getNetworkType = " + netWorkType);
			
			if (nState == TelephonyManager.CALL_STATE_IDLE){
				while (!mTimerTask.cancel())
					mTimer.cancel();
			}
		}
	};

	@Override
	public void run() {

		mTimer.schedule(mTimerTask, 1000, 1000);

	}

}