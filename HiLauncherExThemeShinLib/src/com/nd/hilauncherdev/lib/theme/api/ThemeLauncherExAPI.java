package com.nd.hilauncherdev.lib.theme.api;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.WindowManager;

import com.nd.hilauncherdev.kitset.analytics.OtherAnalytics;
import com.nd.hilauncherdev.kitset.util.ApkTools;
import com.nd.hilauncherdev.lib.theme.NdLauncherExDialogDefaultImp;
import com.nd.hilauncherdev.lib.theme.NdLauncherExThemeApi;
import com.nd.hilauncherdev.lib.theme.db.DowningTaskItem;
import com.nd.hilauncherdev.lib.theme.db.ThemeLibLocalAccessor;
import com.nd.hilauncherdev.lib.theme.down.DownloadNotification;
import com.nd.hilauncherdev.lib.theme.down.DownloadTask;
import com.nd.hilauncherdev.lib.theme.down.ThemeItem;
import com.nd.hilauncherdev.lib.theme.service.CreateDialogService;
import com.nd.hilauncherdev.lib.theme.util.HiLauncherThemeGlobal;
import com.nd.hilauncherdev.lib.theme.util.SUtil;

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
	 * 启动91桌面
	 * @param context
	 */
	public static void startHiLauncher(Context context){
		Intent it = new Intent();
		it.setClassName(THEME_MANAGE_PACKAGE_NAME, THEME_MANAGE_CLASS_NAME);
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		it.addFlags(32);
		context.startActivity(it);
	}
	
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
	 * @param bNotifyMode 是否是通知栏应用模式(false 表示是直接应用模式,true 表示是通知栏消息触发模式)
	 */
	public static void showThemeApplyActivity(Context context, DowningTaskItem dTaskItem, boolean bNotifyMode){
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
		Intent it = null;
		String notifyContent = null;
		if ( ThemeLauncherExAPI.checkItemType(serverThemeID, ThemeItem.ITEM_TYPE_SKIN) ){
			notifyContent = "点击应用皮肤";
			it = getIntentForApplySkin(newThemeID);
			if (bNotifyMode){
				it = new Intent(context, CreateDialogService.class);
				it.putExtra("dTaskItem", dTaskItem);
				it.addFlags(32);
				PendingIntent pIntent = PendingIntent.getService(context, notifyPosition, it, PendingIntent.FLAG_UPDATE_CURRENT);
				DownloadNotification.downloadCompletedNotification(context, notifyPosition, themeName+"下载完成", notifyContent, pIntent);
			}else{
				context.sendBroadcast(it);
			}
		}else{
			notifyContent = "点击应用主题";
			if ( newThemeID==null || "".equals(newThemeID) ) {
				it = getIntentForInstallAndApplyAPT(filePath, serverThemeID);
			}else{
				it = getIntentForApplyAPT(newThemeID);
			}
			
			if (bNotifyMode){ 
				it = new Intent(context, CreateDialogService.class);
				it.putExtra("dTaskItem", dTaskItem);
				it.addFlags(32);
				PendingIntent pIntent = PendingIntent.getService( context, notifyPosition, it, PendingIntent.FLAG_UPDATE_CURRENT);
				DownloadNotification.downloadCompletedNotification(context, notifyPosition, themeName+"下载完成", notifyContent, pIntent);
			}else{
				context.startActivity(it);
			}
		}
		 
		//TODO 下载完成是否发送响应的广播通知第三方
		
		/* 对话框模式应用  皮肤及主题
		Intent intent = new Intent(context, HiLauncherExApplyThemeDialog.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("dTaskItem", dTaskItem);
		context.startActivity(intent);
		*/
	}
	
	public static void showThemeApplyDialog(final Context context, DowningTaskItem dTaskItem){
		
		if (dTaskItem==null)
			return ;
		final String filePath = dTaskItem.tmpFilePath;
		final String serverThemeID = dTaskItem.themeID;
		final String newThemeID = dTaskItem.newThemeID;
		final String themeName = dTaskItem.themeName;
		if (filePath == null) {
			return ;
		}
		final DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				
				//判断桌面是否安装
				if ( !ApkTools.isInstallAPK(context, HiLauncherThemeGlobal.THEME_MANAGE_PACKAGE_NAME) ){
					ThemeLauncherExAPI.showHiLauncherDownDialog(context);
					return;
				}
				
				Intent it = null;
				if ( ThemeLauncherExAPI.checkItemType(serverThemeID, ThemeItem.ITEM_TYPE_SKIN) ){
					it = getIntentForApplySkin(newThemeID);
					context.sendBroadcast(it);
				}else{
					if ( newThemeID==null || "".equals(newThemeID) ) {
						it = getIntentForInstallAndApplyAPT(filePath, serverThemeID);
					}else{
						it = getIntentForApplyAPT(newThemeID);
					}
					try {
						context.startActivity(it);	
					} catch (Exception e) {
						HiLauncherThemeGlobal.ddpost("主题应用失败");
						e.printStackTrace();
					}					
				}
			}
		};
		
		final DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		};
		if (NdLauncherExThemeApi.themeExDialog==null){
			Dialog dialog = (new NdLauncherExDialogDefaultImp()).createThemeDialog(context, -1, "提示", "应用 "+themeName, "确定", "取消", positive, negative);
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			dialog.show();
		}else{
			Dialog dialog = NdLauncherExThemeApi.themeExDialog.createThemeDialog(context, -1, "提示", "应用 "+themeName, "确定", "取消", positive, negative);
			if (dialog!=null){
				dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				dialog.show();
			}
		}
	}
	
	/**
	 * 弹出提示下载91桌面的对话框
	 * @param ctx
	 */
	public static void showHiLauncherDownDialog(final Context ctx){
		
		final DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				try{
					DowningTaskItem hiDowningTaskItem = ThemeLibLocalAccessor.getInstance(ctx).getDowningTaskItem(HiLauncherThemeGlobal.HiLauncherTaskItemID);
					if (hiDowningTaskItem==null || hiDowningTaskItem.state!=DowningTaskItem.DownState_Finish){
						//下载桌面
						final ThemeItem hiThemeDetail = new ThemeItem();
						hiThemeDetail.setItemType(ThemeItem.ITEM_TYPE_LAUNCHER);
						hiThemeDetail.setName("91桌面");
						hiThemeDetail.setId("91" + ThemeItem.ITEM_TYPE_LAUNCHER);
						
						if (hiDowningTaskItem!=null){
							hiThemeDetail.setDownloadUrl(hiDowningTaskItem.downUrl);
							hiThemeDetail.setLargePostersUrl(hiDowningTaskItem.picUrl);
							DownloadTask manager = new DownloadTask();
							manager.downloadTheme( ctx, hiThemeDetail );
						}else{
							final Handler mHandler=new Handler();
							Thread t = new Thread() {
					            @Override
					            public void run() {
					            	//网络获取下载地址 带统计功能
					            	String downloadUrl = OtherAnalytics.get91LauncherAppDownloadUrl(ctx);
					            	//未获取到采用默认地址
									if(SUtil.isEmpty(downloadUrl))
										downloadUrl=HiLauncherThemeGlobal.getHiLauncherDefaultDownUrl(ctx);
									hiThemeDetail.setDownloadUrl(downloadUrl);
									hiThemeDetail.setLargePostersUrl("");
									try{
										/*
										Looper.prepare();
										DownloadTask manager = new DownloadTask();
										manager.downloadTheme( ctx, hiThemeDetail );
										Looper.loop();
										*/
										mHandler.post(new Runnable() {
											@Override
											public void run() {
												DownloadTask manager = new DownloadTask();
												manager.downloadTheme( ctx, hiThemeDetail );
											}
										});
									}catch (Exception e) {
										e.printStackTrace();
									}
					            }
					        };
					        t.start();	
						}
					}else{
						//安装桌面
						ApkTools.installApplication(ctx, hiDowningTaskItem.tmpFilePath);   
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		final DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		};
		
		if (NdLauncherExThemeApi.themeExDialog==null){
			Dialog dialog = (new NdLauncherExDialogDefaultImp()).createThemeDialog(ctx, -1, "提示", "应用全套主题需要下载安装91桌面,确定开始下载.", "确定", "取消", positive, negative);
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			dialog.show();
		}else{
			Dialog dialog = NdLauncherExThemeApi.themeExDialog.createThemeDialog(ctx, -1, "提示", "应用全套主题需要下载安装91桌面,确定开始下载.", "确定", "取消", positive, negative);
			if (dialog!=null){
				dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				dialog.show();
			}
		}
	}
	
}
