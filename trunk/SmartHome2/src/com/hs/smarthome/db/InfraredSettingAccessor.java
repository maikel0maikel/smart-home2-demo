package com.hs.smarthome.db;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class InfraredSettingAccessor {
	
	private static final String DATABASE_NAME = "infraredsetting.db";	
	
	public static final String TBL_INFRARED = "tbl_Infrared";	//红外表名
	private static final int INIT_INFRARED_ITEM = 50;			//默认初始化50个红外
	
	//SQL格式化网站  http://www.dpriver.com/pp/sqlformat.htm
	
	//创建 红外 数据表
	public static final String SQL_CREATE_TABLE_INFRARED = ""
			+ "CREATE TABLE IF NOT EXISTS tbl_infrared "
			+ "  ( "
			+ "     itemId INTEGER PRIMARY KEY AUTOINCREMENT, " //主键,自增ID
			+ "     itemTitleName TEXT "						//红外名称		
			+ "  )";
	
	
	private Context ctx;	

	private InfraredSettingAccessor(Context ctx){
		this.ctx = ctx;
		
		SQLiteDatabase db = openDB();
		db.execSQL(SQL_CREATE_TABLE_INFRARED);
		db.close();
	}	

	static private InfraredSettingAccessor accessor; 
	
	public static InfraredSettingAccessor getInstance(Context context){

		if(accessor == null){
			accessor = new InfraredSettingAccessor(context);
		}
		return accessor;
	}
	
	private SQLiteDatabase openDB(){		
		return ctx.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
	}
	
	/**
	 * 构建红外对象
	 * @param c
	 * @return
	 */
	private InfraredItem buildInfraredItem(Cursor c) {
		
		InfraredItem ret = new InfraredItem();  
		ret.itemId = c.getInt(0);
	    ret.itemTitleName = c.getString(1);
		return ret; 
	}
	
	/**
	 * 获取所有的红外对象
	 * @return
	 * @throws Exception
	 */
	public ArrayList<InfraredItem> getInfraredItemList()
			throws Exception {

		ArrayList<InfraredItem> ret = new ArrayList<InfraredItem>();

		SQLiteDatabase db = openDB();
		String sql = "select * from "+TBL_INFRARED+" order by itemId ";
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();

		while (!c.isAfterLast()) {
			InfraredItem item = buildInfraredItem(c);
			ret.add(item);
			c.moveToNext();
		}

		c.close();
		db.close();
		return ret;
	}
	
	/**
	 * 通过ID获取单个红外对象
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	public InfraredItem getInfraredItem(int itemId) throws Exception{
		InfraredItem ret = null;
		SQLiteDatabase db = openDB();
		Cursor c = db.rawQuery("select * from "+TBL_INFRARED+" where itemId=? ", new String[] {itemId+""});           
        if (c.moveToFirst()) {            
            ret = buildInfraredItem(c);
        }
        c.close();
        db.close();
        return ret;	
	}
	
	/**
	 * 增加或者修改红外对象
	 * @param item
	 * @return
	 * @throws Exception
	 */
	public boolean updateInfraredItem(InfraredItem item) throws Exception{
		
		ContentValues values = new ContentValues();		
		//values.put("itemId", item.itemId); //系统自增
		values.put("itemTitleName", item.itemTitleName);
						
		SQLiteDatabase db = openDB();
		if(this.getInfraredItem(item.itemId)== null){			
			db.insertOrThrow(TBL_INFRARED, null, values);			
		}else{
		    db.update(TBL_INFRARED, values, "itemId=?", new String[] {item.itemId+""});		
		}		
		db.close();
		return true;
	}

	/**
	 * 创建红外初始化数据
	 */
	public void initInfraredTable(){
		//DEBUG 使用 清空表
		//clearShortCutSlotPanelTable();
		//dropShortCutSlotPanelTable();
		if ( !isInfraredTableEmpty() )
			return;
		
		InfraredItem item = new InfraredItem();
		ContentValues values = new ContentValues();		
		SQLiteDatabase db = openDB();
		//初始化8个继电器信息
		for (int i = 0; i < INIT_INFRARED_ITEM; i++) {
			//values.put("itemId", item.itemId); //系统自增
			values.put("itemTitleName", "红外"+(i+1));
			
			db.insertOrThrow(TBL_INFRARED, null, values);				
		}
	
		db.close();
	}
	
	public void clearInfraredTable() {
		SQLiteDatabase db = openDB();
		db.execSQL("DELETE FROM "+TBL_INFRARED);
		db.close();
	}

	public void dropInfraredTable() {
		SQLiteDatabase db = openDB();
		db.execSQL("DROP TABLE IF EXISTS " + TBL_INFRARED);
		db.close();
	}

	public boolean isInfraredTableEmpty() {
		SQLiteDatabase db = openDB();
		Cursor c = db.query(TBL_INFRARED, null, null, null, null, null, null);
		boolean ret = c.getCount() == 0 ? true : false;
		c.close();
		db.close();
		return ret;
	}

}
