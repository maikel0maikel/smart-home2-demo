package com.nd.hilauncherdev.myphone.phonehelper;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * 拨号状态监听 
 * @author cfb
 */
public class CallHelperListener extends PhoneStateListener {
	
	//private static final String TAG = "CallHelperListener";
	/**是否停止拨打*/
	private static boolean bStopCall = true; 
	/**拨号助手*/
	private CallHelper mCallHelper;
	/**拨号完成时回调接口*/
	private CallHelperStopCallBack callHelperStop;
	/**是否摘机过*/
	private boolean bOffHook = false;

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {

		switch (state) {
		case TelephonyManager.CALL_STATE_IDLE:
			//空闲状态(挂断电话)
			
			//外部是否设置了拨号中断
			if (bStopCall){
				doCallHelperStop();
				return;
			}
			
			if (!bOffHook)
				return;
			bOffHook = false;
			
			//上次拨通则直接退出
			if (callHelperStop!=null){
				if (callHelperStop.checkCallPhoneStateAlive()){
					doCallHelperStop();
					return;
				}
			}
			
			//判断是否拨号完成
			if (mCallHelper != null) {
				if (mCallHelper.getHadCallNum()>=mCallHelper.iWantCallTimes) {
					doCallHelperStop();
					return;
				}
				mCallHelper.startCall();
				mCallHelper.addHadCallNum();
			}
			break;
		case TelephonyManager.CALL_STATE_RINGING:
			//来电状态
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			//摘机状态
			bOffHook = true;
			break;
		default:
			break;
		}
	}
	
	public void setCallHelper(CallHelper callHelper){
		mCallHelper = callHelper;
	}
	
	public static void initAll(){
		bStopCall = false;
	}
	
	public static void clearAll(){
		bStopCall = true;
	}

	public static boolean getStopCallState(){
		return bStopCall;
	}
	
	public void setCallHelperStop(CallHelperStopCallBack callHelperStop) {
		this.callHelperStop = callHelperStop;
	}
	
	private void doCallHelperStop(){
		clearAll();
		if (callHelperStop!=null){
			callHelperStop.callHelperStopCall();
		}
	}
}
