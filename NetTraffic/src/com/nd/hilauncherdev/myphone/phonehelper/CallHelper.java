package com.nd.hilauncherdev.myphone.phonehelper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * 拨号助手对象
 * @author cfb
 */
public class CallHelper {

	private Context mContext;
	private String phoneNumber;
	private Intent intent;
	
	/**最大拨打的次数*/
	private static int MaxCallTimes = 30;
	/**循环拨打的次数*/
	public int iWantCallTimes = 1;
	/**已经拨打的次数*/
	private int iHadCallNum = 1;
	
	public CallHelper(Context context, String phoneNum, int callTimes){
		mContext = context;
		phoneNumber = phoneNum;
		if (callTimes>0){
			iWantCallTimes = callTimes>MaxCallTimes?MaxCallTimes:callTimes;
		}else{
			iWantCallTimes = MaxCallTimes;
		}
	}

	public void startCall() {
		if (intent == null) {
			intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phoneNumber));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		try {
			mContext.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addHadCallNum(){
		iHadCallNum++;
	}
	
	public int getHadCallNum(){
		return iHadCallNum;
	}
}