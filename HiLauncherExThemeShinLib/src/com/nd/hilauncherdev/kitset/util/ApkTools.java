package com.nd.hilauncherdev.kitset.util;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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
	
	/**
	 * android查询指定的程序是否安装上 查询安装包
	 * @param mContext
	 * @param pkgName
	 * @return
	 */
	public static boolean isInstallAPK(Context mContext, String pkgName){
		
		PackageManager packageManager= mContext.getPackageManager();
		try {
			packageManager.getApplicationInfo(pkgName, PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {			
			e.printStackTrace();
			return false; 
		}
		
		return true;
	}
}
