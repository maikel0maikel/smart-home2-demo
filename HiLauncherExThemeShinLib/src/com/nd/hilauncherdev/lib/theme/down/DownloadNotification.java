package com.nd.hilauncherdev.lib.theme.down;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import com.nd.android.lib.theme.R;

/**
 * <br>Description: widget下载通知管理类
 * <br>Author:caizp
 * <br>Date:2010-11-4上午10:33:42
 */
public class DownloadNotification {

	/**
	 * <br>Description: 下载成功通知
	 * <br>Author:caizp
	 * <br>Date:2010-11-4上午10:34:09
	 * @param context
	 * @param position
	 * @param title
	 * @param pIntentApply 对应应用按钮行文 
	 */
	public static void downloadCompletedNotification(Context context, int position, String title, String content, PendingIntent pIntent ){
		
		NotificationManager nManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
		
		//清除之前的通知栏
		nManager.cancel(position);

		Notification notification=new Notification();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.icon = android.R.drawable.stat_sys_download_done;
		notification.setLatestEventInfo(context, title, content, pIntent);
		notification.defaults = Notification.DEFAULT_SOUND;
		nManager.notify(position, notification);
	}
	
	public static void sendHiLauncerExFinishMessage(Context context, int position, String title, String content, String filePath){
		
		NotificationManager nManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
		//清除之前的通知栏
		nManager.cancel(position);
		
		File file=new File(filePath);
		Uri uri = Uri.fromFile(file); 
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, "application/vnd.android.package-archive" ); 
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pIntent = PendingIntent.getActivity( context, position, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		pIntent=PendingIntent.getActivity(context, 0, intent, 0);

		Notification notification=new Notification();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.icon = android.R.drawable.stat_sys_download_done;
		notification.setLatestEventInfo(context, title, content, pIntent);
		nManager.notify(position, notification);
	}
	
	/**
	 * <br>Description: 下载失败通知
	 * <br>Author:caizp
	 * <br>Date:2010-11-4上午10:34:29
	 * @param context
	 * @param position
	 * @param widgetItem
	 */
	public static void downloadFailedNotification(Context context, int position, String title, PendingIntent pIntent ){
		NotificationManager nManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
		
		Notification notif = new Notification(android.R.drawable.stat_notify_error, context.getString(R.string.ndtheme_text_unsuccessfully_downloaded), System.currentTimeMillis());
		
		notif.flags = Notification.FLAG_AUTO_CANCEL;
        notif.setLatestEventInfo(context, title, context.getString(R.string.ndtheme_theme_download_fail_tip), pIntent);
        nManager.notify(position, notif);

	}
	
	/**
	 * <br>Description: 取消下载通知
	 * <br>Author:caizp
	 * <br>Date:2010-11-4上午10:34:57
	 * @param context
	 * @param position
	 * @param widgetItem
	 */
	public static void downloadCancelledNotification(Context context, int position){
		
		NotificationManager nManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
		//清除通知栏
		nManager.cancel(position);
	}
	
	/**
	 * <br>Description: 下载过程中进度条更新
	 * <br>Author:caizp
	 * <br>Date:2010-11-4上午10:35:14
	 * @param context
	 * @param position
	 * @param title
	 * @param pIntent
	 */
	public static void downloadRunningNotification(Context context,  int position, String title, String content, PendingIntent pIntent, int progress ){
		try{
			NotificationManager nManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
			
			RemoteViews view = new RemoteViews(context.getPackageName(),R.layout.nd_hilauncher_theme_widget_download_notify);
			view.setImageViewResource(R.id.widget_image, android.R.drawable.stat_sys_download);
			view.setTextViewText(R.id.percent, progress+"%");
			view.setTextViewText(R.id.widget_name, title);
			view.setProgressBar(R.id.progress, 100, progress, false);
			
			if (Build.VERSION.SDK_INT > 10) {
				view.setTextColor(R.id.percent, context.getResources().getColor(R.color.ndtheme_white));
				view.setTextColor(R.id.widget_name, context.getResources().getColor(R.color.ndtheme_white));
			}
			
			Notification notification=new Notification();
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			notification.icon = android.R.drawable.stat_sys_download;
			notification.contentView = view;
			//未下载完成所有不做动作响应
			//notification.contentIntent = pIntent;
			nManager.notify(position, notification);
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

}
