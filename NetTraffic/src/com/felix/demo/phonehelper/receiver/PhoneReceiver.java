package com.felix.demo.phonehelper.receiver;



import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneReceiver extends BroadcastReceiver {
	
	private String TAG = "PhoneReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		Log.e(TAG, "action="+intent.getAction());
		
		String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		Log.d(TAG, "incall=" + phoneState);
		
		if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
			//如果是去电（拨出）
			Log.e(TAG, "拨出");
			
			AskCallStateThread callStateThread = new AskCallStateThread(context);
            callStateThread.start();
            
			TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);   
			tm.listen(listenerOut, PhoneStateListener.LISTEN_CALL_STATE);
		}
	}
	PhoneStateListener listenerIn=new PhoneStateListener(){
 
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			//state 当前状态 incomingNumber,貌似没有去电的API
			super.onCallStateChanged(state, incomingNumber);
			
			Log.e(TAG, "state="+state);
			
			switch(state){
			case TelephonyManager.CALL_STATE_IDLE:
				Log.e(TAG, "挂断");
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				Log.e(TAG, "接听");
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				Log.e(TAG, "响铃:来电号码"+incomingNumber);
				//输出来电号码
				break;
			}
		}
 
	};
	
	PhoneStateListener listenerOut=new PhoneStateListener(){
		 
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			//state 当前状态 incomingNumber,貌似没有去电的API
			super.onCallStateChanged(state, incomingNumber);
			
			Log.e(TAG, "state="+state);
			
			switch(state){
			case TelephonyManager.CALL_STATE_IDLE:
				Log.e(TAG, "挂断out");
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				Log.e(TAG, "接听out");
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				Log.e(TAG, "响铃:来电号码out"+incomingNumber);
				//输出来电号码
				break;
			}
		}
 
	};
}
