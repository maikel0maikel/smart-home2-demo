package com.nd.hilauncherdev.kitset.util;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * APK工具
 * <br>Author:ryan
 * <br>Date:2012-7-19下午06:03:39
 */
public class ApkTools {
	
	private final static String TAG = "com.nd.hilauncherdev.kitset.util.ApkTools";

	/**
	 * 
	 * 安装应用程序
	 */
	public static boolean installApplication(Context ctx, File mainFile) {
		try {
			Uri data = Uri.fromFile(mainFile);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(data, "application/vnd.android.package-archive");
			ctx.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
