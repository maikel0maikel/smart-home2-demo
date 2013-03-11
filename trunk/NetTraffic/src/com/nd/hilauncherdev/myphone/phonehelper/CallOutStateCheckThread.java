package com.nd.hilauncherdev.myphone.phonehelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.Intent;

public class CallOutStateCheckThread implements Runnable {

	private Context ctx;
	
	public CallOutStateCheckThread(Context ctx) {
		this.ctx = ctx;
	}

	@Override
	public void run() {
		Process process = null;
		InputStream inputstream = null;
		InputStreamReader inputstreamreader = null;
		BufferedReader bufferedreader = null;
		try {
			process = Runtime.getRuntime().exec("logcat -v time -b radio");
			inputstream = process.getInputStream();
			inputstreamreader = new InputStreamReader(inputstream);
			bufferedreader = new BufferedReader(inputstreamreader);
			String str = "";
			long dialingStart = 0;
			boolean enableVibrator = false;
			boolean isAlert = false;
			while ((str = bufferedreader.readLine()) != null) {
				
				//如果拨号服务已经停止自动退出
				if ( CallHelperListener.getStopCallState() ){
					break;
				}
				/*Debug
				if (str.contains("GET_CURRENT_CALLS")){
					Log.i("CallOutStateCheckThread_contains", Thread.currentThread().getName() + ":" + str);
				}else{
					Log.i("CallOutStateCheckThread_Not", Thread.currentThread().getName() + ":" + str);
				}
				*/
				
				// 记录GSM状态DIALING
				if (str.contains("GET_CURRENT_CALLS") && str.contains("DIALING")) {
					// 当DIALING开始并且已经经过ALERTING或者首次DIALING
					if (!isAlert || dialingStart == 0) {
						// 记录DIALING状态产生时间
						dialingStart = System.currentTimeMillis();
						isAlert = false;
					}
					continue;
				}
				
				if (str.contains("GET_CURRENT_CALLS") && str.contains("ALERTING") && !enableVibrator) {

					long temp = System.currentTimeMillis() - dialingStart;
					isAlert = true;
					// 这个是关键,当第一次DIALING状态的时间,与当前的ALERTING间隔时间在1.5秒以上并且在20秒以内的话
					// 那么认为下次的ACTIVE状态为通话接通.
					if (temp > 1500 && temp < 20000) {
						enableVibrator = true;
					}
					continue;
				}
				
				if (str.contains("GET_CURRENT_CALLS") && str.contains("ACTIVE") && enableVibrator) {
					CallHelperTool.openSpeaker(ctx);
					enableVibrator = false;
					
					//如果接通后关闭拨号服务
					Intent intent = new Intent(ctx, CallHelperService.class);
					ctx.stopService(intent);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(bufferedreader!=null){
					bufferedreader.close();
				}
				if(inputstreamreader!=null){
					inputstreamreader.close();
				}
				if(inputstream!=null){
					inputstream.close();
				}
				if(process!=null){
					process.destroy();
				}
	        } catch (Exception ex) {
	        	ex.printStackTrace();
	        }
		}
	}
}
