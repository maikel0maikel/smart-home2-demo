package com.hs.smarthome.db;

import java.io.Serializable;

public class HomeItem  implements Serializable{
		
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
	
	/**无线信号*/
	public String itemWireless;
	
	/**红外信号*/
	public String itemInfrared;
}
