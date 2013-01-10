package com.nd.hilauncherdev.lib.theme.db;

import java.io.Serializable;

/**
 * 下载任务对象
 * @author cfb
 */
public class DowningTaskItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4690500640898319422L;

	/**下载中*/
	public static final int DownState_Downing = 1;
	
	/**暂停*/
	public static final int DownState_Pause   = 2;
	
	/**下载完成*/
	public static final int DownState_Finish  = 3;
	
	/**下载失败*/
	public static final int DownState_Fail    = -1;
	
	/**主题名称*/
	public String themeName;
	
	/**服务端主题ID*/
	public String themeID;
	
	/**通知栏消息ID*/
	public int startID;
	
	/**任务状态: 1 正在下载,2 暂停, 3完成 ,-1 下载失败*/
	public int state;				
	
	/**下载地址*/
	public String downUrl;
	
	/**预览图地址*/
	public String picUrl;
	
	/**临时文件路径*/
	public String tmpFilePath;
	
	/**文件大小*/
	public long totalSize;
	
	/**文件进度*/
	public int progress;
	
	/**下载完成本地生成的新主题ID*/
	public String newThemeID;
	
	/**任务创建时间*/
	public String createTime;
}
