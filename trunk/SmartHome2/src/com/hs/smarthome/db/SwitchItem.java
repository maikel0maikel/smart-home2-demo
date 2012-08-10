package com.hs.smarthome.db;

public class SwitchItem {
	
	/** 记录ID 有唯一性约束*/
	public int itemId; 		
	
	/**记录是否为空  0:为Off 1:表示On*/
	public int itemFlag;	 
	
	/**继电器名称*/
	public String itemTitleName;
	
	/**资源图片*/
	public int itemImgResID; 	
	
	public static final int ITEM_FLAG_OFF = 0; 
	public static final int ITEM_FLAG_ON = 1;
}
