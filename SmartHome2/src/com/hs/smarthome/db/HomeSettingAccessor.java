package com.hs.smarthome.db;


import java.util.ArrayList;

import com.hs.smarthome.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class HomeSettingAccessor {
	
	private static final String DATABASE_NAME = "homesetting.db";	
	
	public static final String TBL_HOME = "tbl_home";	//设备表名
	private static final int INIT_HOME_ITEM = 5;			//默认初始化100个报警
	//SQL格式化网站  http://www.dpriver.com/pp/sqlformat.htm
	
	//创建 设备 数据表
	public static final String SQL_CREATE_TABLE_HOME = ""
			+ "CREATE TABLE IF NOT EXISTS tbl_home "
			+ "  ( "
			+ "     itemId INTEGER PRIMARY KEY AUTOINCREMENT, " //主键,自增ID
			+ "     itemTitleName TEXT, "						//设备名称	
			+ "     itemControlPanelID INTEGER , " 				//控制面板
			+ "     itemRoomID INTEGER "						//设备分类				
			+ "  )";
	
	
	private Context ctx;	

	private HomeSettingAccessor(Context ctx){
		this.ctx = ctx;
		
		SQLiteDatabase db = openDB();
		db.execSQL(SQL_CREATE_TABLE_HOME);
		db.close();
	}	

	static private HomeSettingAccessor accessor; 
	
	public static HomeSettingAccessor getInstance(Context context){

		if(accessor == null){
			accessor = new HomeSettingAccessor(context);
		}
		return accessor;
	}
	
	private SQLiteDatabase openDB(){		
		return ctx.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
	}
	
	/**
	 * 构建设备对象
	 * @param c
	 * @return
	 */
	private HomeItem buildHomeItem(Cursor c) {
		
		HomeItem ret = new HomeItem();  
		ret.itemId = c.getInt(0);
	    ret.itemTitleName = c.getString(1);
	    ret.itemControlPanelID = c.getInt(2);
	    ret.itemRoomID = c.getInt(3);
		return ret; 
	}
	
	/**
	 * 获取所有的设备对象
	 * @return
	 * @throws Exception
	 */
	public ArrayList<HomeItem> getHomeItemList(int roomID)
			throws Exception {

		ArrayList<HomeItem> ret = new ArrayList<HomeItem>();

		SQLiteDatabase db = openDB();
		String sql = "select * from "+TBL_HOME+" where itemRoomID="+roomID+" order by itemId ";
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();

		while (!c.isAfterLast()) {
			HomeItem item = buildHomeItem(c);
			ret.add(item);
			c.moveToNext();
		}

		c.close();
		db.close();
		return ret;
	}
	
	/**
	 * 通过ID获取单个设备对象
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	public HomeItem getHomeItem(int itemId) throws Exception{
		HomeItem ret = null;
		SQLiteDatabase db = openDB();
		Cursor c = db.rawQuery("select * from "+TBL_HOME+" where itemId=? ", new String[] {itemId+""});           
        if (c.moveToFirst()) {            
            ret = buildHomeItem(c);
        }
        c.close();
        db.close();
        return ret;	
	}
	
	/**
	 * 增加或者修改设备对象
	 * @param item
	 * @return
	 * @throws Exception
	 */
	public boolean updateHomeItem(HomeItem item) throws Exception{
		
		ContentValues values = new ContentValues();		
		//values.put("itemId", item.itemId); //系统自增
		values.put("itemTitleName", item.itemTitleName);
		values.put("itemControlPanelID", item.itemControlPanelID);
		values.put("itemRoomID", item.itemRoomID);
						
		SQLiteDatabase db = openDB();
		if(this.getHomeItem(item.itemId)== null){			
			db.insertOrThrow(TBL_HOME, null, values);			
		}else{
		    db.update(TBL_HOME, values, "itemId=?", new String[] {item.itemId+""});		
		}	
		db.close();
		return true;
	}

	/**
	 * 创建设备初始化数据
	 */
	public void initHomeTable(){		
		if ( !isHomeTableEmpty() )
			return;
		
		ContentValues values = new ContentValues();		
		SQLiteDatabase db = openDB();
		//初始化100个设备信息
		for (int i = 0; i < INIT_HOME_ITEM; i++) {
			
			if (i>=0&&i<5){
				values.put("itemRoomID", 1);
			}
			
			if (i==0){
				values.put("itemTitleName", "电视机");
				values.put("itemControlPanelID", ControlPanel.PANEL3);
			}
			if (i==1){
				values.put("itemTitleName", "饮水机");
				values.put("itemControlPanelID", ControlPanel.PANEL1);
			}
			if (i==2){
				values.put("itemTitleName", "顶灯");
				values.put("itemControlPanelID", ControlPanel.PANEL1);
			}
			if (i==3){
				values.put("itemTitleName", "空调");
				values.put("itemControlPanelID", ControlPanel.PANEL2);
			}
			if (i==4){
				values.put("itemTitleName", "窗帘");
				values.put("itemControlPanelID", ControlPanel.PANEL1);
			}
			
			db.insertOrThrow(TBL_HOME, null, values);	
		}
	for (int i = 0; i < INIT_HOME_ITEM-2; i++) {
			
			if (i>=0&&i<3){
				values.put("itemRoomID", 2);
			}
			
			if (i==0){
				values.put("itemTitleName", "顶灯");
				values.put("itemControlPanelID", ControlPanel.PANEL1);
			}
			if (i==1){
				values.put("itemTitleName", "台灯");
				values.put("itemControlPanelID", ControlPanel.PANEL1);
			}
			if (i==2){
				values.put("itemTitleName", "窗帘");
				values.put("itemControlPanelID", ControlPanel.PANEL1);
			}
				
			db.insertOrThrow(TBL_HOME, null, values);	
		}
	for (int i = 0; i < INIT_HOME_ITEM-2; i++) {
		
		if (i>=0&&i<5){
			values.put("itemRoomID", 3);
		}
		
		if (i==0){
			values.put("itemTitleName", "顶灯");
			values.put("itemControlPanelID", ControlPanel.PANEL1);
		}
		if (i==1){
			values.put("itemTitleName", "台灯");
			values.put("itemControlPanelID", ControlPanel.PANEL1);
		}
		if (i==2){
			values.put("itemTitleName", "窗帘");
			values.put("itemControlPanelID", ControlPanel.PANEL1);
		}
		
		db.insertOrThrow(TBL_HOME, null, values);	
	}
for (int i = 0; i < INIT_HOME_ITEM-4; i++) {
		
		if (i>=0&&i<5){
			values.put("itemRoomID", 4);
		}
		
		if (i==0){
			values.put("itemTitleName", "顶灯");
			values.put("itemControlPanelID", ControlPanel.PANEL1);
		}
		db.insertOrThrow(TBL_HOME, null, values);	
	}
for (int i = 0; i < INIT_HOME_ITEM-3; i++) {
	
	if (i>=0&&i<5){
		values.put("itemRoomID", 5);
	}
	
	if (i==0){
		values.put("itemTitleName", "卫生间顶灯");
		values.put("itemControlPanelID", ControlPanel.PANEL1);
	}
	if (i==1){
		values.put("itemTitleName", "卫生间热水器");
		values.put("itemControlPanelID", ControlPanel.PANEL1);
	}

	
	db.insertOrThrow(TBL_HOME, null, values);	
}
for (int i = 0; i < INIT_HOME_ITEM-4; i++) {
	
	if (i>=0&&i<5){
		values.put("itemRoomID", 6);
	}
	
	if (i==0){
		values.put("itemTitleName", "卫生间洗衣机");
		values.put("itemControlPanelID", ControlPanel.PANEL1);
	}
	db.insertOrThrow(TBL_HOME, null, values);	
}
		
		db.close();
	}
	
	public void deleteHomeSetting(int itemID) {
		SQLiteDatabase db = openDB();
		db.execSQL("DELETE FROM "+TBL_HOME+" WHERE itemId="+itemID);
		db.close();
	}
	
	public void clearHomeTable() {
		SQLiteDatabase db = openDB();
		db.execSQL("DELETE FROM "+TBL_HOME);
		db.close();
	}

	public void dropHomeTable() {
		SQLiteDatabase db = openDB();
		db.execSQL("DROP TABLE IF EXISTS " + TBL_HOME);
		db.close();
	}

	public boolean isHomeTableEmpty() {
		SQLiteDatabase db = openDB();
		Cursor c = db.query(TBL_HOME, null, null, null, null, null, null);
		boolean ret = c.getCount() == 0 ? true : false;
		c.close();
		db.close();
		return ret;
	}
	

}