package com.hs.smarthome.db;


import java.util.ArrayList;

import com.hs.smarthome.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class WirelessSettingAccessor {
	
	private static final String DATABASE_NAME = "wirelesssetting.db";	
	
	public static final String TBL_WIRELESS = "tbl_wireless";	//无线表名
	private static final int INIT_WIRELESS_ITEM = 100;			//默认初始化100个无线
	
	//SQL格式化网站  http://www.dpriver.com/pp/sqlformat.htm
	
	//创建 无线 数据表
	public static final String SQL_CREATE_TABLE_WIRELESS = ""
			+ "CREATE TABLE IF NOT EXISTS tbl_wireless "
			+ "  ( "
			+ "     itemId INTEGER PRIMARY KEY AUTOINCREMENT, " //主键,自增ID
			+ "     itemTitleName TEXT "						//无线名称		
			+ "  )";
	
	
	private Context ctx;	

	private WirelessSettingAccessor(Context ctx){
		this.ctx = ctx;
		
		SQLiteDatabase db = openDB();
		db.execSQL(SQL_CREATE_TABLE_WIRELESS);
		db.close();
	}	

	static private WirelessSettingAccessor accessor; 
	
	public static WirelessSettingAccessor getInstance(Context context){

		if(accessor == null){
			accessor = new WirelessSettingAccessor(context);
		}
		return accessor;
	}
	
	private SQLiteDatabase openDB(){		
		return ctx.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
	}
	
	/**
	 * 构建无线对象
	 * @param c
	 * @return
	 */
	private WirelessItem buildWirelessItem(Cursor c) {
		
		WirelessItem ret = new WirelessItem();  
		ret.itemId = c.getInt(0);
	    ret.itemTitleName = c.getString(1);
		return ret; 
	}
	
	/**
	 * 获取所有的无线对象
	 * @return
	 * @throws Exception
	 */
	public ArrayList<WirelessItem> getWirelessItemList()
			throws Exception {

		ArrayList<WirelessItem> ret = new ArrayList<WirelessItem>();

		SQLiteDatabase db = openDB();
		String sql = "select * from "+TBL_WIRELESS+" order by itemId ";
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();

		while (!c.isAfterLast()) {
			WirelessItem item = buildWirelessItem(c);
			ret.add(item);
			c.moveToNext();
		}

		c.close();
		db.close();
		return ret;
	}
	
	/**
	 * 通过ID获取单个无线对象
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	public WirelessItem getWirelessItem(int itemId) throws Exception{
		WirelessItem ret = null;
		SQLiteDatabase db = openDB();
		Cursor c = db.rawQuery("select * from "+TBL_WIRELESS+" where itemId=? ", new String[] {itemId+""});           
        if (c.moveToFirst()) {            
            ret = buildWirelessItem(c);
        }
        c.close();
        db.close();
        return ret;	
	}
	
	/**
	 * 增加或者修改无线对象
	 * @param item
	 * @return
	 * @throws Exception
	 */
	public boolean updateWirelessItem(WirelessItem item) throws Exception{
		
		ContentValues values = new ContentValues();		
		//values.put("itemId", item.itemId); //系统自增
		values.put("itemTitleName", item.itemTitleName);
						
		SQLiteDatabase db = openDB();
		if(this.getWirelessItem(item.itemId)== null){			
			db.insertOrThrow(TBL_WIRELESS, null, values);			
		}else{
		    db.update(TBL_WIRELESS, values, "itemId=?", new String[] {item.itemId+""});		
		}		
		db.close();
		return true;
	}

	/**
	 * 创建无线初始化数据
	 */
	public void initWirelessTable(){
		//DEBUG 使用 清空表
		//clearShortCutSlotPanelTable();
		//dropShortCutSlotPanelTable();
		if ( !isWirelessTableEmpty() )
			return;
		
		WirelessItem item = new WirelessItem();
		ContentValues values = new ContentValues();		
		SQLiteDatabase db = openDB();
		//初始化100个无线信息
		for (int i = 0; i < INIT_WIRELESS_ITEM; i++) {
			//values.put("itemId", item.itemId); //系统自增
			values.put("itemTitleName", "无线"+(i+1));
			
			db.insertOrThrow(TBL_WIRELESS, null, values);				
		}
	
		db.close();
	}
	
	public void clearWirelessTable() {
		SQLiteDatabase db = openDB();
		db.execSQL("DELETE FROM "+TBL_WIRELESS);
		db.close();
	}

	public void dropWirelessTable() {
		SQLiteDatabase db = openDB();
		db.execSQL("DROP TABLE IF EXISTS " + TBL_WIRELESS);
		db.close();
	}

	public boolean isWirelessTableEmpty() {
		SQLiteDatabase db = openDB();
		Cursor c = db.query(TBL_WIRELESS, null, null, null, null, null, null);
		boolean ret = c.getCount() == 0 ? true : false;
		c.close();
		db.close();
		return ret;
	}

}
