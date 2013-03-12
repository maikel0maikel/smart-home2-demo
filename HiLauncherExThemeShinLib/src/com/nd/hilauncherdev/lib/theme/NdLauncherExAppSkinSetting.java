package com.nd.hilauncherdev.lib.theme;

import com.nd.hilauncherdev.lib.theme.util.HiLauncherThemeGlobal;

/**
 * 应用皮肤设置信息
 * @author cfb
 */
public class NdLauncherExAppSkinSetting {

	/**黄历天气默认值*/
	private static final String appDefaultID = "1000";
	private static final String appDefaultKey = "3";
	
	/**接入应用ID 皮肤列表展示参数*/
	private String appId;
	
	/**接入应用Func_ID 下载统计使用*/
	private String appKey;
	
	/**下载后皮肤路径存放  value 末尾需要目录符号*/
	private String appSkinPath = HiLauncherThemeGlobal.PACKAPGES_HOME;
	
	/**对话框创建实例*/
	private NdLauncherExDialogCallback themeExDialog = null;
	
	/**下载统计实例*/
	private NdLauncherExDownActionCallback themeExDownAction = null;

	public String getAppId() {
		return appId==null?appDefaultID:appId;
	}

	/**
	 * 设置AppID
	 * @param appId
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppKey() {
		return appKey==null?appDefaultKey:appKey;
	}

	/**
	 * 设置AppKey
	 * @param appKey
	 */
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSkinPath() {
		return appSkinPath==null?HiLauncherThemeGlobal.PACKAPGES_HOME:appSkinPath;
	}

	/**
	 * 设置皮肤下载后的解压路径
	 * @param appSkinPath
	 */
	public void setAppSkinPath(String appSkinPath) {
		this.appSkinPath = appSkinPath;
	}

	public NdLauncherExDialogCallback getThemeExDialog() {
		return themeExDialog;
	}

	/**
	 * 设置自定义对话框接口
	 * @param themeExDialog
	 */
	public void setThemeExDialog(NdLauncherExDialogCallback themeExDialog) {
		this.themeExDialog = themeExDialog;
	}

	public NdLauncherExDownActionCallback getThemeExDownAction() {
		return themeExDownAction;
	}

	/**
	 * 设置下载回调接口
	 * @param themeExDownAction
	 */
	public void setThemeExDownAction(NdLauncherExDownActionCallback themeExDownAction) {
		this.themeExDownAction = themeExDownAction;
	}

}
