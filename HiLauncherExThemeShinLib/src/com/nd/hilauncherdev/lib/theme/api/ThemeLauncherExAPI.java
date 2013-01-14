package com.nd.hilauncherdev.lib.theme.api;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.nd.hilauncherdev.lib.theme.NdLauncherExThemeApi;
import com.nd.hilauncherdev.lib.theme.db.DowningTaskItem;
import com.nd.hilauncherdev.lib.theme.down.DownloadNotification;
import com.nd.hilauncherdev.lib.theme.down.ThemeItem;

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
		context.startActivity(getIntentForApplyAPT(newThemeID));
	}
	
	/**
	 * 获取主题应用Intent
	 * @param newThemeID
	 * @return
	 */
	public static Intent getIntentForApplyAPT(String newThemeID){
		Intent it = new Intent();
		it.setClassName(THEME_MANAGE_PACKAGE_NAME, THEME_MANAGE_CLASS_NAME);
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		it.putExtra(THEME_PARAMETER_THEME_FROM, "apttheme:" + newThemeID);
		it.addFlags(32);
		return it;
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
		
		context.startActivity(getIntentForInstallAndApplyAPT(aptPath,serverThemeID));
	}
	
	/**
	 * 获取主题安装及应用的Intent
	 * @param aptPath
	 * @param serverThemeID
	 * @return
	 */
	public static Intent getIntentForInstallAndApplyAPT(String aptPath, String serverThemeID){
		Intent it = new Intent();
		it.setClassName(THEME_MANAGE_PACKAGE_NAME, THEME_MANAGE_CLASS_NAME);
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		it.putExtra(THEME_PARAMETER_THEME_FROM, "aptpath:" + aptPath);
		it.putExtra(THEME_PARAMETER_SERVER_THEME_ID, serverThemeID);
		it.addFlags(32);
		return it;
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
		String themeName = dTaskItem.themeName;
		int notifyPosition = dTaskItem.startID; 
		if (filePath == null) {
			return ;
		}
		
		//通知栏模式应用 皮肤及主题
		if ( ThemeLauncherExAPI.checkItemType(serverThemeID, ThemeItem.ITEM_TYPE_SKIN) ){
			Intent intent = getIntentForApplySkin(newThemeID);
			PendingIntent pIntent = PendingIntent.getActivity( context, notifyPosition, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			DownloadNotification.downloadCompletedNotification(context, notifyPosition, themeName+"下载完成", "点击应用皮肤", pIntent);
		}else{
			if ( newThemeID==null || "".equals(newThemeID) ) {
				Intent it = getIntentForInstallAndApplyAPT(filePath, serverThemeID);
				PendingIntent pIntent = PendingIntent.getActivity( context, notifyPosition, it, PendingIntent.FLAG_UPDATE_CURRENT);
				DownloadNotification.downloadCompletedNotification(context, notifyPosition, themeName+"下载完成", "点击应用主题", pIntent);
			}else{
				Intent it = getIntentForApplyAPT(newThemeID);
				PendingIntent pIntent = PendingIntent.getActivity( context, notifyPosition, it, PendingIntent.FLAG_UPDATE_CURRENT);
				DownloadNotification.downloadCompletedNotification(context, notifyPosition, themeName+"下载完成", "点击应用主题", pIntent);
			}
		}
		 
		/* 对话框模式应用  皮肤及主题
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
		
		context.sendBroadcast(getIntentForApplySkin(dTaskItem.newThemeID));
	}
	
	/**
	 * 获取第三方插件皮肤应用Intent
	 * @param filePath
	 * @return
	 */
	public static Intent getIntentForApplySkin(String filePath){
		Intent intent = new Intent(NdLauncherExThemeApi.ND_HILAUNCHER_THEME_SKIN_APPLY_ACTION);
		intent.putExtra(NdLauncherExThemeApi.ND_HILAUNCHER_THEME_APP_ID_KEY, NdLauncherExThemeApi.ND_HILAUNCHER_THEME_APP_ID_VALUE);
		intent.putExtra(NdLauncherExThemeApi.ND_HILAUNCHER_THEME_APP_SKIN_PATH_KEY, filePath);
		intent.addFlags(32);
		return intent;
	}
}
