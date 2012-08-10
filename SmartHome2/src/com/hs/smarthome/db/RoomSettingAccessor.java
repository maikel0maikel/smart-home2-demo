package com.hs.smarthome.db;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class RoomSettingAccessor {
	
	private static final String DATABASE_NAME = "roomsetting.db";	
	
	public static final String TBL_ROOM = "tbl_room";	//房间表名
	private static final int INIT_ROOM_ITEM = 6;			//默认初始化6个房间
	
	//SQL格式化网站  http://www.dpriver.com/pp/sqlformat.htm
	
	//创建 房间 数据表
	public static final String SQL_CREATE_TABLE_ROOM = ""
			+ "CREATE TABLE IF NOT EXISTS tbl_room "
			+ "  ( "
			+ "     itemId INTEGER PRIMARY KEY AUTOINCREMENT, " //主键,自增ID
			+ "     itemTitleName TEXT "						//房间名称		
			+ "  )";
	
	
	private Context ctx;	

	private RoomSettingAccessor(Context ctx){
		this.ctx = ctx;
		
		SQLiteDatabase db = openDB();
		db.execSQL(SQL_CREATE_TABLE_ROOM);
		db.close();
	}	

	static private RoomSettingAccessor accessor; 
	
	public static RoomSettingAccessor getInstance(Context context){

		if(accessor == null){
			accessor = new RoomSettingAccessor(context);
		}
		return accessor;
	}
	
	private SQLiteDatabase openDB(){		
		return ctx.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
	}
	
	/**
	 * 构建房间对象
	 * @param c
	 * @return
	 */
	private RoomItem buildRoomItem(Cursor c) {
		
		RoomItem ret = new RoomItem();  
		ret.itemId = c.getInt(0);
	    ret.itemTitleName = c.getString(1);
		return ret; 
	}
	
	/**
	 * 获取所有的房间对象
	 * @return
	 * @throws Exception
	 */
	public ArrayList<RoomItem> getRoomItemList()
			throws Exception {

		ArrayList<RoomItem> ret = new ArrayList<RoomItem>();

		SQLiteDatabase db = openDB();
		String sql = "select * from "+TBL_ROOM+" order by itemId ";
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();

		while (!c.isAfterLast()) {
			RoomItem item = buildRoomItem(c);
			ret.add(item);
			c.moveToNext();
		}

		c.close();
		db.close();
		return ret;
	}
	
	/**
	 * 通过ID获取单个房间对象
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	public RoomItem getRoomItem(int itemId) throws Exception{
		RoomItem ret = null;
		SQLiteDatabase db = openDB();
		Cursor c = db.rawQuery("select * from "+TBL_ROOM+" where itemId=? ", new String[] {itemId+""});           
        if (c.moveToFirst()) {            
            ret = buildRoomItem(c);
        }
        c.close();
        db.close();
        return ret;	
	}
	
	/**
	 * 增加或者修改房间对象
	 * @param item
	 * @return
	 * @throws Exception
	 */
	public boolean updateRoomItem(RoomItem item) throws Exception{
		
		ContentValues values = new ContentValues();		
		//values.put("itemId", item.itemId); //系统自增
		values.put("itemTitleName", item.itemTitleName);
						
		SQLiteDatabase db = openDB();
		if(this.getRoomItem(item.itemId)== null){			
			db.insertOrThrow(TBL_ROOM, null, values);			
		}else{
		    db.update(TBL_ROOM, values, "itemId=?", new String[] {item.itemId+""});		
		}		
		db.close();
		return true;
	}

	/**
	 * 创建房间初始化数据
	 */
	public void initRoomTable(){
		//DEBUG 使用 清空表
		//clearShortCutSlotPanelTable();
		//dropShortCutSlotPanelTable();
		if ( !isRoomTableEmpty() )
			return;
		
		RoomItem item = new RoomItem();
		ContentValues values = new ContentValues();		
		SQLiteDatabase db = openDB();
		//初始化6个房间信息
		values.put("itemTitleName", "客厅");
		db.insertOrThrow(TBL_ROOM, null, values);
		
		values.put("itemTitleName", "卧室");
		db.insertOrThrow(TBL_ROOM, null, values);	
		
		values.put("itemTitleName", "书房");
		db.insertOrThrow(TBL_ROOM, null, values);	

		values.put("itemTitleName", "厨房");
		db.insertOrThrow(TBL_ROOM, null, values);	
		
		values.put("itemTitleName", "其他");
		db.insertOrThrow(TBL_ROOM, null, values);
		
		values.put("itemTitleName", "其他2");
		db.insertOrThrow(TBL_ROOM, null, values);	
		
		db.close();
	}
	
	public void clearRoomTable() {
		SQLiteDatabase db = openDB();
		db.execSQL("DELETE FROM "+TBL_ROOM);
		db.close();
	}

	public void dropRoomTable() {
		SQLiteDatabase db = openDB();
		db.execSQL("DROP TABLE IF EXISTS " + TBL_ROOM);
		db.close();
	}

	public boolean isRoomTableEmpty() {
		SQLiteDatabase db = openDB();
		Cursor c = db.query(TBL_ROOM, null, null, null, null, null, null);
		boolean ret = c.getCount() == 0 ? true : false;
		c.close();
		db.close();
		return ret;
	}

}
