package com.nd.hilauncherdev.lib.theme.down;


import java.io.File;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.nd.hilauncherdev.lib.theme.db.DowningTaskItem;


/**
 * 主题下载管理类
 */
public final class ThemeDownloadStateManager {
	
	/**下载Intent*/
	public static final String INTENT_THEME_DOWNLOAD_STATE = "com.nd.android.pandahome2.themelib.download";
	
	/**下载进度广播*/	
	public static final int CODE_DOWNLOA_PROGRESS = 100;
	
	/**发送下载暂停的广播*/
	public static void sendDownloadPauseMessage( Context context, String themeId ) {
		Intent intent = new Intent( INTENT_THEME_DOWNLOAD_STATE );
		intent.putExtra( "themeId", themeId );
		intent.putExtra( "state", DowningTaskItem.DownState_Pause );
		context.sendBroadcast( intent );		
	}
	
	/**发送下载失败的广播*/
	public static void sendDownloadFailMessage( Context context, String themeId ) {
		Intent intent = new Intent( INTENT_THEME_DOWNLOAD_STATE );
		intent.putExtra( "themeId", themeId );
		intent.putExtra( "state", DowningTaskItem.DownState_Fail );
		context.sendBroadcast( intent );		
	}
	
	/**发送下载进度的广播*/
	public static void sendDownloadProgressMessage( Context context, String themeId, int progress, String tempFilePath) {
		Intent intent = new Intent( INTENT_THEME_DOWNLOAD_STATE );
		intent.putExtra( "themeId", themeId );
		intent.putExtra( "state", CODE_DOWNLOA_PROGRESS );
		intent.putExtra( "progress", progress );
		intent.putExtra( "tempFilePath", tempFilePath );
		context.sendBroadcast( intent );		
	}	
	
	/**发送下载中的广播*/
	public static void sendDownloadingMessage( Context context, String themeId ) {
		Intent intent = new Intent( INTENT_THEME_DOWNLOAD_STATE );
		intent.putExtra( "themeId", themeId );
		intent.putExtra( "state", DowningTaskItem.DownState_Downing );
		context.sendBroadcast( intent );		
	}
	
	/**
	 * 发送下载完成的广播
	 */
	public static  void sendDownloadFinishMessage( Context context, String oldId, String newThemeId,String filePath ) {
		Intent intent = new Intent( INTENT_THEME_DOWNLOAD_STATE );
		intent.putExtra( "themeId", oldId );
		intent.putExtra( "id", newThemeId );
		intent.putExtra( "state", DowningTaskItem.DownState_Finish );
		intent.putExtra( "filePath", filePath );
		context.sendBroadcast( intent );		
	}
}
