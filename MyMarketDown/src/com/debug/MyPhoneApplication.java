package com.debug;


import android.app.Application;

import com.nd.hilauncherdev.datamodel.CommonGlobal;

public class MyPhoneApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		CommonGlobal.setApplicationContext(this.getBaseContext());
	}
}
