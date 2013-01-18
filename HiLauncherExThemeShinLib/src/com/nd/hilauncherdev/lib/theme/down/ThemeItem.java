package com.nd.hilauncherdev.lib.theme.down;

import java.io.Serializable;


/**
 * 主题列表单项主题信息
 */
public class ThemeItem implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**皮肤*/
	public static final int ITEM_TYPE_SKIN = 1;
	
	/**全套主题*/
	public static final int ITEM_TYPE_THEME = 2;
	
	/**91桌面*/
	public static final int ITEM_TYPE_LAUNCHER = 3;	
	
	/**下载类型   1:皮肤,2:全套主题*/
	private int itemType = 2;

	/**主题标识*/
	private String id;
	
	/**主题名称*/
	private String name;

	/**大尺寸预览图*/
	private String largePostersUrl;

	/**下载地址*/
	private String downloadUrl;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getLargePostersUrl() {
		return largePostersUrl;
	}
	public void setLargePostersUrl(String largePostersUrl) {
		this.largePostersUrl = largePostersUrl;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public int getItemType() {
		return itemType;
	}

	public void setItemType(int itemType) {
		this.itemType = itemType;
	}
}
