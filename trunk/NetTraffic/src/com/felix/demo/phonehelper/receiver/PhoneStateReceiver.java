package com.felix.demo.phonehelper.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.felix.demo.phonehelper.receiver.activity.MainActivity;

public class PhoneStateReceiver extends BroadcastReceiver {

	private static final String TAG = "PhoneStateReceiver";

	/*
	 * @Override public void onReceive(Context context, Intent intent) {
	 * 
	 * String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
	 * 
	 * 
	 * 
	 * if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) { String
	 * incomingNumber =
	 * intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
	 * 
	 * Log.d(TAG, "new call arrived with number:" + incomingNumber);
	 * 
	 * Intent i = new Intent(context, Object.class);
	 * i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	 * i.putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER, incomingNumber);
	 * 
	 * try { Thread.sleep(1000); } catch (InterruptedException e) {
	 * e.printStackTrace(); } context.startActivity(i); } else
	 * if(phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){ Log.d(TAG,
	 * "makeing phone call!" ); } }
	 */
//	private static String mOutGoingNumber;

	@Override
	public void onReceive(Context context, Intent intent) {
			
		String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		Log.d(TAG, "incall " + phoneState);
		do {
//			// show call hold screen, and  save the number.
			if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
				String mOutGoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
				Log.d(TAG, "ACTION_NEW_OUTGOING_CALL " + mOutGoingNumber);
				
				//new Thread(new CallOutThread()).start();
				AskCallStateThread callStateThread = new AskCallStateThread(context);
	            callStateThread.start();
				break;
			}
//			
//			// show call hold screen
//			if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
//				Log.d(TAG, "in hold");
//				Intent i = new Intent(CallScreenHoldActivity.ACTION);
//				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//				Bundle b = new Bundle();
//				b.putString("phoneNumber", mOutGoingNumber);
//				i.putExtras(b);
//
//				context.startActivity(i);
//				break;
//			}
			// show in call  screen
			if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
				String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
				
				if (incomingNumber == null || incomingNumber.length()==0) {
					return;
				}
				
				Log.d(TAG, "incall " + incomingNumber);
				try {
					Thread.sleep(500);
				} catch (Exception e) {
					Log.i(TAG, e.toString());
				}
				
				
				Intent i = new Intent(context, MainActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER, incomingNumber);
				context.startActivity(i);
				break;
			}
		}while(false);
		
//		Log.d(TAG, "receive " + intent.getAction()
//				+ intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
//				+ "phoneState:" + phoneState+"phone number" + mOutGoingNumber);
	}
}
