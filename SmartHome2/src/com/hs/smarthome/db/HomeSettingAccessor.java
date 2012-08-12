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
	private static final int INIT_HOME_ITEM = 100;			//默认初始化100个设备
	
	//SQL格式化网站  http://www.dpriver.com/pp/sqlformat.htm
	
	//创建 设备 数据表
	public static final String SQL_CREATE_TABLE_HOME = ""
			+ "CREATE TABLE IF NOT EXISTS tbl_home "
			+ "  ( "
			+ "     itemId INTEGER PRIMARY KEY AUTOINCREMENT, " //主键,自增ID
			+ "     itemTitleName TEXT "						//设备名称		
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
	    chooseImgRes(ret);
		return ret; 
	}
	
	/**
	 * 获取所有的设备对象
	 * @return
	 * @throws Exception
	 */
	public ArrayList<HomeItem> getHomeItemList()
			throws Exception {

		ArrayList<HomeItem> ret = new ArrayList<HomeItem>();

		SQLiteDatabase db = openDB();
		String sql = "select * from "+TBL_HOME+" order by itemId ";
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
						
		SQLiteDatabase db = openDB();
		if(this.getHomeItem(item.itemId)== null){			
			db.insertOrThrow(TBL_HOME, null, values);			
		}else{
		    db.update(TBL_HOME, values, "itemId=?", new String[] {item.itemId+""});		
		}	
		chooseImgRes(item);
		db.close();
		return true;
	}

	/**
	 * 创建设备初始化数据
	 */
	public void initHomeTable(){
		//DEBUG 使用 清空表
		//clearShortCutSlotPanelTable();
		//dropShortCutSlotPanelTable();
		if ( !isHomeTableEmpty() )
			return;
		
		HomeItem item = new HomeItem();
		ContentValues values = new ContentValues();		
		SQLiteDatabase db = openDB();
		//初始化100个设备信息
		for (int i = 0; i < INIT_HOME_ITEM; i++) {
			//values.put("itemId", item.itemId); //系统自增
			values.put("itemTitleName", "设备"+(i+1));
			
			db.insertOrThrow(TBL_HOME, null, values);	
			chooseImgRes(item);
		}
	
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
	
	public void chooseImgRes(HomeItem item){
		
		String str=item.itemTitleName;
		switch(str){
		case "电视机":item.itemImgResID=R.drawable.menu_list_equipement_tv;break;
		case "饮水机":item.itemImgResID=R.drawable.menu_list_equipement_kg;break;
		case "顶灯":item.itemImgResID=R.drawable.menu_list_equipement_kg;break;
		case "窗帘":item.itemImgResID=R.drawable.menu_list_equipement_kg;break;
		case "台灯":item.itemImgResID=R.drawable.menu_list_equipement_kg;break;
		case "卫生间顶灯":item.itemImgResID=R.drawable.menu_list_equipement_kg;break;
		case "卫生间热水器":item.itemImgResID=R.drawable.menu_list_equipement_kg;break;
		case "卫生间洗衣机":item.itemImgResID=R.drawable.menu_list_equipement_kg;break;
		case "空调":item.itemImgResID=R.drawable.menu_list_equipement_kt;break;
		case "DVD":item.itemImgResID=R.drawable.menu_list_equipement_dvd;break;
		case "多媒体":item.itemImgResID=R.drawable.menu_list_equipement_media;break;
		default:item.itemImgResID=R.drawable.menu_list_equipement_kg;break;
		
		}
	}

}