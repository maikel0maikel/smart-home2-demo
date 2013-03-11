package com.felix.demo.phonehelper.receiver.activity;

import android.content.Context;
import android.media.AudioManager;

public class MyAudioManager {

	private AudioManager audioManager;
	private int currVolume = 0;
	private Context context;

	public MyAudioManager(Object object, Context mc) {
		// 音频管理对象由外部调用是传入
		this.audioManager = (AudioManager) object;
		this.context = mc;
		// 设置音讯模式为对外输出
		this.audioManager.setMode(AudioManager.ROUTE_SPEAKER);
		// 取得当前的音量
		currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
	}

	// 打开扬声器
	public void OpenSpeaker() {
		// 设置为true，打开扬声器
		audioManager.setSpeakerphoneOn(true);
		// 设置打开扬声器的音量为最大
		audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.STREAM_VOICE_CALL);
		// Toast.makeText(context,"揚聲器已經打開",Toast.LENGTH_SHORT).show();
	}

	// 关闭扬声器
	public void CloseSpeaker() {
		// 设置为false，关闭已经打开的扬声器
		audioManager.setSpeakerphoneOn(false);
		// 恢复为正常音量
		audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume, AudioManager.STREAM_VOICE_CALL);
		// Toast.makeText(context,"揚聲器已經關閉",Toast.LENGTH_SHORT).show();
	}
}
