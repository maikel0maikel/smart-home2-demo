package com.nd.hilauncherdev.myphone.phonehelper;

import android.content.Context;
import android.media.AudioManager;
import android.os.ServiceManager;

import com.android.internal.telephony.ITelephony;

/**
 * 拨号助手工具类
 * @author Cfb
 *
 */
public class CallHelperTool {

	/**
	 * 结束通话
	 */
	public static synchronized void closePhone() {
		try {
			ITelephony phone = ITelephony.Stub.asInterface(ServiceManager.checkService("phone"));
			phone.endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 打开扬声器
	 */
	public static synchronized void openSpeaker(Context ctx) {
		try {
			AudioManager audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
			audioManager.setMode(AudioManager.MODE_IN_CALL); 
			if (!audioManager.isSpeakerphoneOn()) {
				audioManager.setSpeakerphoneOn(true);

				audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.STREAM_VOICE_CALL);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 关闭扬声器
	 */
	public static synchronized void closeSpeaker(Context ctx) {

		try {
			AudioManager audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager != null) {
				if (audioManager.isSpeakerphoneOn()) {
					audioManager.setSpeakerphoneOn(false);
					int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
					audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume, AudioManager.STREAM_VOICE_CALL);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
