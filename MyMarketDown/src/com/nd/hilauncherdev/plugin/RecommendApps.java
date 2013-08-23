package com.nd.hilauncherdev.plugin;

import android.content.Context;
import android.content.DialogInterface;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.appmarket.AppMarketUtil;
import com.nd.hilauncherdev.framework.ViewFactory;
import com.nd.hilauncherdev.framework.view.dialog.CommonDialog;
import com.nd.hilauncherdev.kitset.Analytics.AnalyticsConstant;
import com.nd.hilauncherdev.kitset.Analytics.HiAnalytics;
import com.nd.hilauncherdev.kitset.Analytics.OtherAnalytics;
import com.nd.hilauncherdev.kitset.util.StringUtil;
import com.nd.hilauncherdev.kitset.util.ThreadUtil;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.ApkDownloadInfo;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.DownloadServerServiceConnection;

/**
 * 推荐应用集合 <br>
 * Author:ryan <br>
 * Date:2012-11-23下午03:53:19
 */
public class RecommendApps {
	public static final String PANDASPACE_CLS = "com.dragon.android.pandaspace.main.LoadingActivity";
	public static final String PANDASPACE_PCK = "com.dragon.android.pandaspace";

	/**
	 * 手机助手下载地址
	 */
	public static final String ASSIT_APP_DOWNLOAD_URL = "http://dl.sj.91.com/business/91soft/91assistant_Andphone167.apk";

	/**
	 * 机锋市场
	 */
	public static final String GFAN_CLS = "com.mappn.gfan.ui.SplashActivity";
	public static final String GFAN_PCK = "com.mappn.gfan";
	
	/**
	 * 机锋市场下载地址
	 */
	private static final String GFAN_APP_DOWNLOAD_URL = "http://da.91rb.com/android/soft/2012/tmp/GfanMobile_web783.apk";
	
	/**
	 * 显示 下载助手的对话框
	 */
	public static void showInstallAssitAppDialog(final Context mContext) {
		final StringBuffer title = new StringBuffer(mContext.getString(R.string.common_button_download)).append(mContext.getString(R.string.app_market_app_assit));
		CommonDialog alertd = ViewFactory.getAlertDialog(mContext, title, mContext.getString(R.string.app_market_app_no_assit_tip), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				downloadAssitApp(mContext);
				dialog.dismiss();
			}
		}, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		});
		alertd.show();
	}

	private static void downloadAssitApp(final Context mContext) {		
		ThreadUtil.executeMore(new Runnable() {

			@Override
			public void run() {
				HiAnalytics.submitEvent(mContext, AnalyticsConstant.RECOMMEND_APP_ND_ASSISTANCE);
				
				// 在线获取91助手的下载地址
				String downloadUrl = OtherAnalytics.get91AssistAppDownloadUrl(mContext);
				// 未获取到采用默认地址
				if (StringUtil.isEmpty(downloadUrl))
					downloadUrl = ASSIT_APP_DOWNLOAD_URL;

				ApkDownloadInfo dlInfo = new ApkDownloadInfo(downloadUrl, downloadUrl);
				dlInfo.apkFile = PANDASPACE_PCK + ".apk";
				dlInfo.downloadDir = AppMarketUtil.PACKAGE_DOWNLOAD_DIR;
				dlInfo.appName = mContext.getString(R.string.app_market_app_assit);
				dlInfo.iconPath = "drawable:logo_91assist";
				DownloadServerServiceConnection mDownloadService = new DownloadServerServiceConnection(mContext);
				mDownloadService.addDownloadTask(dlInfo);
			}
		});
	}
	
	/**
	 * 
	* @Author 								C.xt
	* @Title: 								showInstallGfanAppDialog
	* @Description:							显示 下载机锋市场的对话框
	* @param mContext:						void				 
	* @throws							
	* @date 								2013-5-15 下午8:21:31
	 */
	public static void showInstallGfanAppDialog(final Context mContext) {
		final StringBuffer title = new StringBuffer(mContext.getString(R.string.common_button_download)).append(mContext.getString(R.string.app_market_app_gfan));
		CommonDialog alertd = ViewFactory.getAlertDialog(mContext, title, mContext.getString(R.string.app_market_app_no_gfan_tip), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				downloadGfanApp(mContext);
				dialog.dismiss();
			}
		}, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		});
		alertd.show();
	}
	
	/**
	 * 
	* @Author 								C.xt
	* @Title: 								downloadGfanApp
	* @Description:							下载机锋市场apk 行为函数
	* @param mContext:						void				 
	* @throws							
	* @date 								2013-5-15 下午8:22:24
	 */
	private static void downloadGfanApp(final Context mContext) {
		ThreadUtil.executeMore(new Runnable() {
			@Override
			public void run() {
				HiAnalytics.submitEvent(mContext, AnalyticsConstant.RECOMMEND_APP_GFAN);
				
				ApkDownloadInfo dlInfo = new ApkDownloadInfo(GFAN_APP_DOWNLOAD_URL, GFAN_APP_DOWNLOAD_URL);
				dlInfo.apkFile = GFAN_PCK + ".apk";
				dlInfo.downloadDir = AppMarketUtil.PACKAGE_DOWNLOAD_DIR;
				dlInfo.appName = mContext.getString(R.string.recommend_gfan);
				dlInfo.iconPath = "drawable:logo_gfan";
				DownloadServerServiceConnection mDownloadService = new DownloadServerServiceConnection(mContext);
				mDownloadService.addDownloadTask(dlInfo);
			}
		});
	}
}
