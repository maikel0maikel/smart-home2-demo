package com.hs.smarthome.db;

import java.io.Serializable;

public class AlarmItem implements Serializable{
		
	/** 记录ID 有唯一性约束*/
	public int itemId; 	
	
	/**资源图片*/
	public int itemImgResID; 		 
	
	/**无线名称*/
	public String itemTitleName;
	
	/**报警震动提示开关      0:为Off 1:表示On*/
	public int itemShock;
	
	/**报警声音提示开关*/
	public int itemSound;
	
	/**为内部报警默认音*/
	public int itemDefaultSound;
	
	/**外部报警音路径*/
	public String itemOtherSoundPath;	
	
	public static final int ITEM_FLAG_OFF = 0; 
	public static final int ITEM_FLAG_ON = 1;
}
