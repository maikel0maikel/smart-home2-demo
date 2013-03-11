package com.felix.demo.phonehelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.felix.demo.phonehelper.receiver.activity.MainActivity;

public class MyReceiver extends BroadcastReceiver {

	private static final String TAG = "MyReceiver";

	// 这里intent是来自于拨打电话，而intentTemp是用来跳转到MainActivity界面里
	@Override
	public void onReceive(final Context context, Intent intent) {

		// http://www.eoeandroid.com/forum.php?mod=viewthread&tid=200069&reltid=201849&pre_thread_id=0&pre_pos=2&ext=
		/*
		 * Runnable runnable = new Runnable() {
		 * 
		 * @Override public void run() {
		 * 
		 * Process process; InputStream inputstream; BufferedReader
		 * bufferedreader; try { process =
		 * Runtime.getRuntime().exec("logcat -v time -b radio"); inputstream =
		 * process.getInputStream(); InputStreamReader inputstreamreader = new
		 * InputStreamReader(inputstream); bufferedreader = new
		 * BufferedReader(inputstreamreader); String str = ""; while ((str =
		 * bufferedreader.readLine()) != null) { Log.d(tag, str); boolean
		 * isReadLine =
		 * str.contains("========Report Message incoming ^ONLINE=============");
		 * if (isReadLine) { Log.d(tag, tag + str); int connectTime =
		 * Integer.parseInt(str.substring(12, 14)); Log.d(tag, tag +
		 * "connectTime :" + connectTime); long milliseconds =
		 * System.currentTimeMillis(); Calendar calendar =
		 * Calendar.getInstance(); calendar.setTimeInMillis(milliseconds); int
		 * curTime = calendar.get(Calendar.SECOND); Log.d(tag, tag +
		 * "curTime 1 :" + curTime); // 上次电话接通时间差 if (Math.abs(curTime -
		 * connectTime) < 3) { Log.d(tag, "lcc 接通电话"); //isAcitive = true;
		 * Intent intentTemp = new Intent(context, MainActivity.class); //
		 * 必须为这个intent设置一个Flag
		 * intentTemp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * context.startActivity(intentTemp); } } // Log.d(tag, "lcc str"+str);
		 * } } catch (Exception e) { } } };
		 * 
		 * new Thread(runnable).start();
		 */
		/*
		 * TelephonyManager mTelephony = (TelephonyManager)
		 * context.getSystemService(Context.TELEPHONY_SERVICE); new Thread(new
		 * TestThread(null, mTelephony, context)).start();
		 */

		String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		Log.d(TAG, "incall " + phoneState);
		do {
			// show in call screen
			if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
				String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

				if (incomingNumber == null || incomingNumber.length() == 0) {
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
		} while (false);
	}
}
