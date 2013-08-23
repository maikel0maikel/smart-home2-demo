package com.nd.hilauncherdev.framework;

import android.view.KeyEvent;

/***
 * 需要响应按键的View实现
 * 
 */
public interface OnKeyDownListenner {
	 /**
	  * 响应按键
	  */
	 public boolean onKeyDownProcess(int keyCode, KeyEvent event);
}