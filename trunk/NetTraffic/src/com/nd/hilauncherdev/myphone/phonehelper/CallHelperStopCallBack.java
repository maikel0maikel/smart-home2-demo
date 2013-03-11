package com.nd.hilauncherdev.myphone.phonehelper;

/**
 * 设定的拨号完成时回调接口
 * @author cfb
 */
public interface CallHelperStopCallBack {
	
	/**
	 * 拨完或则停止拨号时回调
	 */
	public void callHelperStopCall();
	
	/**
	 * 判断上一个打出的电话是否拨通
	 */
	public boolean checkCallPhoneStateAlive();
}
