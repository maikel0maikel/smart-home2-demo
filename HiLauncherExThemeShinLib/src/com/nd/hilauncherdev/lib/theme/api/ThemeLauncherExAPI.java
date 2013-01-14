package com.nd.hilauncherdev.lib.theme.api;

import com.nd.android.lib.theme.R;
import com.nd.hilauncherdev.lib.theme.HiLauncherExApplyThemeDialog;
import com.nd.hilauncherdev.lib.theme.NdLauncherExThemeApi;
import com.nd.hilauncherdev.lib.theme.db.DowningTaskItem;
import com.nd.hilauncherdev.lib.theme.down.DownloadNotification;
import com.nd.hilauncherdev.lib.theme.down.ThemeItem;
import com.nd.hilauncherdev.lib.theme.util.HiLauncherThemeGlobal;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

/**
 * 91桌面主题交互接口
 * @author cfb
 */
public class ThemeLauncherExAPI {

	/**91桌面包名*/
	public static final String THEME_MANAGE_PACKAGE_NAME = "com.nd.android.pandahome2";
	
	/**91桌面主入口类名*/
	public static final String THEME_MANAGE_CLASS_NAME = "com.nd.hilauncherdev.launcher.Launcher";
	
	/**安装成功后发送广播*/
	public static final String INSTALL_RESULT_ACTION_RECEIVER = "nd.pandahome.external.response.theme.apt.install";
	
	/**广播参数主题操作来源字段*/
	private static final String THEME_PARAMETER_THEME_FROM = "from";
	
	/**广播参数服务端主题ID*/
	private static final String THEME_PARAMETER_SERVER_THEME_ID = "serverThemeID";
	
	/**主题应用广播*/
	public static final String ND_HILAUNCHER_THEME_APPLY_ACTION = "nd.pandahome.external.response.themelib.apt.apply";

	/**
	 * 91桌面主题应用接口
	 * @param context
	 * @param newThemeID 安装后的主题ID
	 */
	public static void sendApplyAPT(Context context,String newThemeID){
		
		sendLauncherThemeApplyBsd(context);
		
		Intent it = new Intent();
		it.setClassName(THEME_MANAGE_PACKAGE_NAME, THEME_MANAGE_CLASS_NAME);
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		it.putExtra(THEME_PARAMETER_THEME_FROM, "apttheme:" + newThemeID);
		it.addFlags(32);
		context.startActivity(it);
	}
	
	/**
	 * 91桌面主题安装及应用接口
	 * @param context
	 * @param aptPath  主题包路径
	 * @param serverThemeID  主题包的服务端资源ID
	 */
	public static void installAndApplyAPT(Context context, String aptPath, String serverThemeID, int notifyPosition){
		
		sendLauncherThemeApplyBsd(context);
		
		//清除之前的通知栏
		NotificationManager nManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
		nManager.cancel(notifyPosition);
		
		Intent it = new Intent();
		it.setClassName(THEME_MANAGE_PACKAGE_NAME, THEME_MANAGE_CLASS_NAME);
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		it.putExtra(THEME_PARAMETER_THEME_FROM, "aptpath:" + aptPath);
		it.putExtra(THEME_PARAMETER_SERVER_THEME_ID, serverThemeID);
		it.addFlags(32);
		context.startActivity(it);
	}
	
	/**
	 * 发送主题皮肤应用广播,用于通知关闭下载任务窗口
	 * @param context
	 */
	private static void sendLauncherThemeApplyBsd(Context context){
		//发送广播关闭
		Intent intent = new Intent(ND_HILAUNCHER_THEME_APPLY_ACTION);
		intent.addFlags(32);
		context.sendBroadcast(intent);
	}
	
	
	/**
	 * 通过服务端资源ID判断是否是皮肤插件
	 * (id末尾为1的为皮肤插件)
	 * @param serverResID
	 * @param iItemType  插件类型
	 * @return
	 */
	public static boolean checkItemType(String serverResID, int iItemType){
		
        boolean bResult = false;//3为91桌面,2为主题,1为皮肤插件 
        if ( serverResID!=null && serverResID.length()>0 ) {
			String itemType = serverResID.substring(serverResID.length()-1);
			if ( (iItemType+"").equals(itemType)){
				bResult = true;
			}
		}
		return bResult;
	}
	
	
	/**
	 * 显示主题及皮肤应用窗口
	 * @param context
	 * @param dTaskItem
	 */
	public static void showThemeApplyActivity(Context context, DowningTaskItem dTaskItem){
		//修改为发送到通知栏顶端
		if (dTaskItem==null)
			return ;
		String filePath = dTaskItem.tmpFilePath;
		String serverThemeID = dTaskItem.themeID;
		String newThemeID = dTaskItem.newThemeID;
		int notifyPosition = dTaskItem.startID; 
		if (filePath == null) {
			return ;
		}
		
		if ( ThemeLauncherExAPI.checkItemType(serverThemeID, ThemeItem.ITEM_TYPE_SKIN) ){
			
			Intent intent = new Intent(NdLauncherExThemeApi.ND_HILAUNCHER_THEME_SKIN_APPLY_ACTION);
			intent.putExtra(NdLauncherExThemeApi.ND_HILAUNCHER_THEME_APP_ID_KEY, NdLauncherExThemeApi.ND_HILAUNCHER_THEME_APP_ID_VALUE);
			intent.putExtra(NdLauncherExThemeApi.ND_HILAUNCHER_THEME_APP_SKIN_PATH_KEY, dTaskItem.newThemeID);
			intent.addFlags(32);
			PendingIntent pIntent = PendingIntent.getActivity( context, dTaskItem.startID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			DownloadNotification.downloadCompletedNotification(context, dTaskItem.startID, dTaskItem.themeName+"下载完成", "点击应用皮肤", pIntent);
			
			//ThemeLauncherExAPI.sendApplySkin(context, dTaskItem);
		}else{
			//如果未安装桌面的情况下
			if ( newThemeID==null || "".equals(newThemeID) ) {
				Intent it = new Intent();
				it.setClassName(THEME_MANAGE_PACKAGE_NAME, THEME_MANAGE_CLASS_NAME);
				it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				it.putExtra(THEME_PARAMETER_THEME_FROM, "aptpath:" + filePath);
				it.putExtra(THEME_PARAMETER_SERVER_THEME_ID, serverThemeID);
				it.addFlags(32);
				PendingIntent pIntent = PendingIntent.getActivity( context, dTaskItem.startID, it, PendingIntent.FLAG_UPDATE_CURRENT);
				DownloadNotification.downloadCompletedNotification(context, dTaskItem.startID, dTaskItem.themeName+"下载完成", "点击应用主题", pIntent);
				
				//ThemeLauncherExAPI.installAndApplyAPT(context, filePath, serverThemeID, notifyPosition);
			}else{
				Intent it = new Intent();
				it.setClassName(THEME_MANAGE_PACKAGE_NAME, THEME_MANAGE_CLASS_NAME);
				it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				it.putExtra(THEME_PARAMETER_THEME_FROM, "apttheme:" + newThemeID);
				it.addFlags(32);
				PendingIntent pIntent = PendingIntent.getActivity( context, dTaskItem.startID, it, PendingIntent.FLAG_UPDATE_CURRENT);
				DownloadNotification.downloadCompletedNotification(context, dTaskItem.startID, dTaskItem.themeName+"下载完成", "点击应用主题", pIntent);
				
				//ThemeLauncherExAPI.sendApplyAPT(context, newThemeID);
			}
		}
		 
		/*
		Intent intent = new Intent(context, HiLauncherExApplyThemeDialog.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("dTaskItem", dTaskItem);
		context.startActivity(intent);
		*/
	}
	
	/**
	 * 发送皮肤应用广播给第三方插件
	 * @param context
	 * @param dTaskItem
	 */
	public static void sendApplySkin(Context context, DowningTaskItem dTaskItem){
		
		Intent intent = new Intent(NdLauncherExThemeApi.ND_HILAUNCHER_THEME_SKIN_APPLY_ACTION);
		intent.putExtra(NdLauncherExThemeApi.ND_HILAUNCHER_THEME_APP_ID_KEY, NdLauncherExThemeApi.ND_HILAUNCHER_THEME_APP_ID_VALUE);
		intent.putExtra(NdLauncherExThemeApi.ND_HILAUNCHER_THEME_APP_SKIN_PATH_KEY, dTaskItem.newThemeID);
		intent.addFlags(32);
		context.sendBroadcast(intent);
	}
}
