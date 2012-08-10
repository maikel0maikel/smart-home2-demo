package com.hs.smarthome.db;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class AlarmSettingAccessor {
	
	private static final String DATABASE_NAME = "alarmsetting.db";	
	
	public static final String TBL_ALARM = "tbl_alarm";	//报警表名
	private static final int INIT_ALARM_ITEM = 9;			//默认初始化100个报警
	
	//SQL格式化网站  http://www.dpriver.com/pp/sqlformat.htm
	
	//创建 报警 数据表
	public static final String SQL_CREATE_TABLE_ALARM = ""
			+ "CREATE TABLE IF NOT EXISTS tbl_alarm"
			+ "  ( "
			+ "     itemId INTEGER PRIMARY KEY AUTOINCREMENT, " //主键,自增ID
			+ "     itemTitleName TEXT, "						//报警名称	
			+ " 	itemShock INTEGER, "
			+ " 	itemSound INTEGER, "
			+ " 	itemDefaultSound INTEGER, "
			+ " 	itemOtherSoundPath TEXT "
			+ "  )";
	
	
	private Context ctx;	

	private AlarmSettingAccessor(Context ctx){
		this.ctx = ctx;
		
		SQLiteDatabase db = openDB();
		db.execSQL(SQL_CREATE_TABLE_ALARM);
		db.close();
	}	

	static private AlarmSettingAccessor accessor; 
	
	public static AlarmSettingAccessor getInstance(Context context){

		if(accessor == null){
			accessor = new AlarmSettingAccessor(context);
		}
		return accessor;
	}
	
	private SQLiteDatabase openDB(){		
		return ctx.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
	}
	
	/**
	 * 构建报警对象
	 * @param c
	 * @return
	 */
	private AlarmItem buildAlarmItem(Cursor c) {
		
		AlarmItem ret = new AlarmItem();  
		ret.itemId = c.getInt(0);
	    ret.itemTitleName = c.getString(1);
	    ret.itemShock = c.getInt(2);
	    ret.itemSound = c.getInt(3);
	    ret.itemDefaultSound = c.getInt(4);
	    ret.itemOtherSoundPath = c.getString(5);
		return ret; 
	}
	
	/**
	 * 获取所有的报警对象
	 * @return
	 * @throws Exception
	 */
	public ArrayList<AlarmItem> getAlarmItemList()
			throws Exception {

		ArrayList<AlarmItem> ret = new ArrayList<AlarmItem>();

		SQLiteDatabase db = openDB();
		String sql = "select * from "+TBL_ALARM+" order by itemId ";
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();

		while (!c.isAfterLast()) {
			AlarmItem item = buildAlarmItem(c);
			ret.add(item);
			c.moveToNext();
		}

		c.close();
		db.close();
		return ret;
	}
	
	/**
	 * 通过ID获取单个报警对象
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	public AlarmItem getAlarmItem(int itemId) throws Exception{
		AlarmItem ret = null;
		SQLiteDatabase db = openDB();
		Cursor c = db.rawQuery("select * from "+TBL_ALARM+" where itemId=? ", new String[] {itemId+""});           
        if (c.moveToFirst()) {            
            ret = buildAlarmItem(c);
        }
        c.close();
        db.close();
        return ret;	
	}
	
	/**
	 * 增加或者修改报警对象
	 * @param item
	 * @return
	 * @throws Exception
	 */
	public boolean updateAlarmItem(AlarmItem item) throws Exception{
		
		ContentValues values = new ContentValues();		
		//values.put("itemId", item.itemId); //系统自增
		values.put("itemTitleName", item.itemTitleName);
		values.put("itemShock", item.itemShock);
		values.put("itemSound", item.itemSound);
		values.put("itemDefaultSound", item.itemDefaultSound);
		values.put("itemOtherSoundPath", item.itemOtherSoundPath);
						
		SQLiteDatabase db = openDB();
		if(this.getAlarmItem(item.itemId)== null){			
			db.insertOrThrow(TBL_ALARM, null, values);			
		}else{
		    db.update(TBL_ALARM, values, "itemId=?", new String[] {item.itemId+""});		
		}		
		db.close();
		return true;
	}

	/**
	 * 创建报警初始化数据
	 */
	public void initAlarmTable(){
		//DEBUG 使用 清空表
		//clearShortCutSlotPanelTable();
		//dropShortCutSlotPanelTable();
		if ( !isAlarmTableEmpty() )
			return;
		
		AlarmItem item = new AlarmItem();
		ContentValues values = new ContentValues();		
		SQLiteDatabase db = openDB();
		//初始化9个报警信息
		values.put("itemTitleName", "温度报警");
		values.put("itemShock", AlarmItem.ITEM_FLAG_ON);
		values.put("itemSound", AlarmItem.ITEM_FLAG_ON);
		values.put("itemDefaultSound", AlarmItem.ITEM_FLAG_ON);
		values.put("itemOtherSoundPath", "");
		db.insertOrThrow(TBL_ALARM, null, values);
		
		for (int i = 0; i < INIT_ALARM_ITEM-1; i++) {
			//values.put("itemId", item.itemId); //系统自增
			values.put("itemTitleName", "报警组"+(i+1));
			
			db.insertOrThrow(TBL_ALARM, null, values);				
		}
	
		db.close();
	}
	
	public void clearAlarmTable() {
		SQLiteDatabase db = openDB();
		db.execSQL("DELETE FROM "+TBL_ALARM);
		db.close();
	}

	public void dropAlarmTable() {
		SQLiteDatabase db = openDB();
		db.execSQL("DROP TABLE IF EXISTS " + TBL_ALARM);
		db.close();
	}

	public boolean isAlarmTableEmpty() {
		SQLiteDatabase db = openDB();
		Cursor c = db.query(TBL_ALARM, null, null, null, null, null, null);
		boolean ret = c.getCount() == 0 ? true : false;
		c.close();
		db.close();
		return ret;
	}

}