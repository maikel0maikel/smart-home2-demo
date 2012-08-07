package com.hs.smarthome.db;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class SmartHomeAccessor {
	
	private static final String DATABASE_NAME = "smarthome2.db";	
	
	public static final String TBL_SWITCH = "tbl_switch";	//继电器表名
	private static final int INIT_SWITCH_ITEM = 8;			//默认初始化8个继电器
	
	//SQL格式化网站  http://www.dpriver.com/pp/sqlformat.htm
	
	//创建 继电器 数据表
	public static final String SQL_CREATE_TABLE_SWITCH = ""
			+ "CREATE TABLE IF NOT EXISTS tbl_switch "
			+ "  ( "
			+ "     itemId INTEGER PRIMARY KEY AUTOINCREMENT, " //主键,自增ID
			+ "     itemFlag  INTEGER NOT NULL, "				//开关标志 0表示关闭,1表示开启
			+ "     itemTitleName TEXT "						//继电器名称		
			+ "  )";
	
	
	private Context ctx;	

	private SmartHomeAccessor(Context ctx){
		this.ctx = ctx;
		
		SQLiteDatabase db = openDB();
		db.execSQL(SQL_CREATE_TABLE_SWITCH);
		db.close();
	}	

	static private SmartHomeAccessor accessor; 
	
	public static SmartHomeAccessor getInstance(Context context){

		if(accessor == null){
			accessor = new SmartHomeAccessor(context);
		}
		return accessor;
	}
	
	private SQLiteDatabase openDB(){		
		return ctx.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
	}
	
	/**
	 * 构建继电器对象
	 * @param c
	 * @return
	 */
	private SwitchItem buildSwitchItem(Cursor c) {
		
		SwitchItem ret = new SwitchItem();  
		ret.itemId = c.getInt(0);
		ret.itemFlag = c.getInt(1); 
        ret.itemTitleName = c.getString(2);
		return ret; 
	}
	
	/**
	 * 获取所有的继电器对象
	 * @return
	 * @throws Exception
	 */
	public ArrayList<SwitchItem> getSwitchItemList()
			throws Exception {

		ArrayList<SwitchItem> ret = new ArrayList<SwitchItem>();

		SQLiteDatabase db = openDB();
		String sql = "select * from "+TBL_SWITCH+" order by itemId ";
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();

		while (!c.isAfterLast()) {
			SwitchItem item = buildSwitchItem(c);
			ret.add(item);
			c.moveToNext();
		}

		c.close();
		db.close();
		return ret;
	}
	
	/**
	 * 通过ID获取单个继电器对象
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	public SwitchItem getSwitchItem(int itemId) throws Exception{
		SwitchItem ret = null;
		SQLiteDatabase db = openDB();
		Cursor c = db.rawQuery("select * from "+TBL_SWITCH+" where itemId=? ", new String[] {itemId+""});           
        if (c.moveToFirst()) {            
            ret = buildSwitchItem(c);
        }
        c.close();
        db.close();
        return ret;	
	}
	
	/**
	 * 增加或者修改继电器对象
	 * @param item
	 * @return
	 * @throws Exception
	 */
	public boolean updateSwitchItem(SwitchItem item) throws Exception{
		
		ContentValues values = new ContentValues();		
		//values.put("itemId", item.itemId); //系统自增
		values.put("itemFlag", item.itemFlag);
		values.put("itemTitleName", item.itemTitleName);
						
		SQLiteDatabase db = openDB();
		if(this.getSwitchItem(item.itemId)== null){			
			db.insertOrThrow(TBL_SWITCH, null, values);			
		}else{
		    db.update(TBL_SWITCH, values, "itemId=?", new String[] {item.itemId+""});		
		}		
		db.close();
		return true;
	}

	/**
	 * 创建继电器初始化数据
	 */
	public void initSwitchTable(){
		//DEBUG 使用 清空表
		//clearShortCutSlotPanelTable();
		//dropShortCutSlotPanelTable();
		if ( !isSwitchTableEmpty() )
			return;
		
		SwitchItem item = new SwitchItem();
		item.itemFlag = SwitchItem.ITEM_FLAG_OFF;//继电器默认是Off 
		ContentValues values = new ContentValues();		
		SQLiteDatabase db = openDB();
		//初始化8个继电器信息
		for (int i = 0; i < INIT_SWITCH_ITEM; i++) {
			//values.put("itemId", item.itemId); //系统自增
			values.put("itemFlag", item.itemFlag);
			values.put("itemTitleName", "继电器"+(i+1));
			
			db.insertOrThrow(TBL_SWITCH, null, values);				
		}
	
		db.close();
	}
	
	public void clearSwitchTable() {
		SQLiteDatabase db = openDB();
		db.execSQL("DELETE FROM "+TBL_SWITCH);
		db.close();
	}

	public void dropSwitchTable() {
		SQLiteDatabase db = openDB();
		db.execSQL("DROP TABLE IF EXISTS " + TBL_SWITCH);
		db.close();
	}

	public boolean isSwitchTableEmpty() {
		SQLiteDatabase db = openDB();
		Cursor c = db.query(TBL_SWITCH, null, null, null, null, null, null);
		boolean ret = c.getCount() == 0 ? true : false;
		c.close();
		db.close();
		return ret;
	}

}
