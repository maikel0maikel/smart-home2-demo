package com.hs.smarthome.db;

public class HomeItem {
		
	/** 记录ID 有唯一性约束*/
	public int itemId; 	
	
	/**资源图片*/
	public int itemImgResID; 		 
	
	/**设备名称*/
	public String itemTitleName;
	
	/**控制面板*/
	public int itemControlPanelID;
	
	/**设备分类*/
	public int itemRoomID;
}
