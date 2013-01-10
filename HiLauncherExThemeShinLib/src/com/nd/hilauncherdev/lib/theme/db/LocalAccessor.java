package com.nd.hilauncherdev.lib.theme.db;


import java.io.File;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * 下载队列数据存储
 * @author cfb
 */
public class LocalAccessor{

	private static final String DATABASE_NAME = "hi_themelib_data.db"; 	
	private static final String T_DOWNINGTask = "DowningTask";
	
	private static final String SQL_CREATE_TABLE_DOWNING_TASK = ""
			+ "CREATE TABLE IF NOT EXISTS DowningTask "
			+ "  ( "
			+ "		_id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "     themename   TEXT, "
			+ "     themeid     TEXT, "
			+ "     startid     TEXT, "     //状态栏消息ID
			+ "     state       INTEGER, "  //1  正在下载,2  暂停,-1 下载失败
			+ "     downurl     TEXT, "
			+ "     picurl      TEXT, "
			+ "     tmpfilepath TEXT, "
			+ "     totalsize   INTEGER, "
			+ "     newthemeid  TEXT "
			+ "  )";

	private Context ctx;	

	private LocalAccessor(Context ctx){
		this.ctx = ctx;
		SQLiteDatabase db = openDB();
		db.execSQL(SQL_CREATE_TABLE_DOWNING_TASK);
		db.close();
	}	

	static private LocalAccessor accessor; 
	
	public static LocalAccessor getInstance(Context context){

		if(accessor == null){
			accessor = new LocalAccessor(context);
		}
		return accessor;
	}
	
	private SQLiteDatabase openDB(){		
		return ctx.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
	}
	
	public boolean updateDowningTaskItemState(String themeID, int newState) throws Exception{
		
		DowningTaskItem item = getDowningTaskItem(themeID);
		item.state = newState;
		
		return updateDowningTaskItem(item);
	}
	
	public boolean updateDowningTaskItem(DowningTaskItem item) throws Exception{
		ContentValues values = new ContentValues();		
		values.put("themeName", item.themeName);
		values.put("themeID", item.themeID);
		values.put("startID", item.startID);
		values.put("state", item.state);
		values.put("downUrl", item.downUrl);	
		values.put("picUrl", item.picUrl);		
		values.put("tmpFilePath", item.tmpFilePath);
		if ( item.totalSize!=0 )
			values.put("totalSize", item.totalSize);
		values.put("newThemeID", item.newThemeID);
		
		SQLiteDatabase db = openDB();
		if(this.getDowningTaskItem(item.themeID)== null){//insert
			values.put("themeID", item.themeID);
			db.insertOrThrow(T_DOWNINGTask, null, values);			
		}else{//update
		    db.update(T_DOWNINGTask, values, "themeID=?", new String[] {item.themeID});		
		}		
		db.close();
		return true;
	}
	
	public ArrayList<DowningTaskItem> getDowningTaskByState(long state)
			throws Exception {

		ArrayList<DowningTaskItem> ret = new ArrayList<DowningTaskItem>();

		SQLiteDatabase db = openDB();
		String sql = "select * from "+T_DOWNINGTask+" where state =" + state+" order by _id desc";
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();

		while (!c.isAfterLast()) {
			DowningTaskItem taskItem = buildDowningTask(c);
			
			if ( taskItem.state!=DowningTaskItem.DownState_Finish ) {
	        	if ( taskItem.tmpFilePath!=null && taskItem.totalSize!=0 )
	        		taskItem.progress = (int)(getTmpFileSize(taskItem.tmpFilePath)*100/taskItem.totalSize);
	        }
			ret.add(taskItem);
			
			c.moveToNext();
		}

		c.close();
		db.close();
		return ret;
	}
	
	
	private DowningTaskItem buildDowningTask(Cursor c) {
		
		DowningTaskItem ret = new DowningTaskItem();  
		ret.themeName = c.getString(1);
		ret.themeID = c.getString(2); 
		ret.startID = c.getInt(3);
        ret.state = c.getInt(4);
        ret.downUrl = c.getString(5);
        ret.picUrl  = c.getString(6);
        ret.tmpFilePath  = c.getString(7);
        ret.totalSize  = c.getLong(8);   
        ret.newThemeID  = c.getString(9); 
        
		return ret; 
	}

	private long getTmpFileSize(String path){
		
		File file = new File(path);
		long size = 0;
		if (!file.exists()) {
			return 0;
		} else {
			size = file.length();
		}
		return size;
	}

	public DowningTaskItem getDowningTaskItem(String themeID) throws Exception{
		DowningTaskItem ret = null;
		SQLiteDatabase db = openDB();
        Cursor c = db.rawQuery("select * from "+T_DOWNINGTask+" where themeID=?", new String[] {themeID});        
        if (c.moveToFirst()) {            
            ret = buildDowningTask(c);
        }
        c.close();
        db.close();
        return ret;	
	}	
	
	public DowningTaskItem getDowningTaskItemByAptPath(String aptPath) throws Exception{
		DowningTaskItem ret = null;
		SQLiteDatabase db = openDB();
        Cursor c = db.rawQuery("select * from "+T_DOWNINGTask+" where tmpFilePath=?", new String[] {aptPath});        
        if (c.moveToFirst()) {            
            ret = buildDowningTask(c);
        }
        c.close();
        db.close();
        return ret;	
	}	
	
	public DowningTaskItem getDowningTaskItemByDownUrl(String downURL) throws Exception{
		DowningTaskItem ret = null;
		SQLiteDatabase db = openDB();
        Cursor c = db.query(T_DOWNINGTask, null, "downUrl='"+downURL+ "'", null, null, null, null);        
        if (c.moveToFirst()) {            
            ret = buildDowningTask(c);
        }
        c.close();
        db.close();
        return ret;	
	}
	
	public int deleteDowningTask(DowningTaskItem downingTaskItem) throws Exception{
		SQLiteDatabase db = openDB();
		int count = db.delete(T_DOWNINGTask, "themeID=?", new String[]{downingTaskItem.themeID});
		db.close();
		return count;
	}

	
	public int deleteDowningTaskByNewThemeID(String newThemeID) throws Exception{
		SQLiteDatabase db = openDB();
		int count = db.delete(T_DOWNINGTask, "newThemeID=?", new String[]{newThemeID});
		db.close();
		return count;
	}
	
	public void clearDowningTask() {
		SQLiteDatabase db = openDB();		
		db.delete(T_DOWNINGTask, null, null);
		db.close();		
	}
	
	public boolean isDowningTaskEmpty(){
		SQLiteDatabase db = openDB();		
		Cursor c = db.query(T_DOWNINGTask, null, null, null, null, null, null);
		boolean ret = c.getCount() == 0 ? true : false ;
		c.close();
		db.close();
		return ret;
	}

	
    /**
     * 登记新ID到下载队列
     * @param themeID
     */
    public void updateDownTaskItemForNewThemeID(String themeID, String newThemeID){
		try {
			DowningTaskItem item = getDowningTaskItem( themeID );
			if (item!=null) {
				item.newThemeID = newThemeID;
				updateDowningTaskItem(item);                           	 		                           	 		
			}
		} catch (Exception e) {
		 	e.printStackTrace();
		}
    }
    
    /**
     * 更新队列状态
     * @param themeID
     */
    public void updateDownTaskItemForDownState(String themeID, int newDownState){
        try {
        	DowningTaskItem item = getDowningTaskItem( themeID );
        	if (item!=null) {
        		
        		item.state = newDownState;
       	 		updateDowningTaskItem(item);                           	 		                           	 		
        	}
        } catch (Exception e) {
       	 	e.printStackTrace();
        }
    }
    
    /**
     * 更新队列状态
     * @param themeID
     */
    public void updateDownTaskItemForDownState(String themeID, int newDownState, String filePath){
        try {
        	DowningTaskItem item = getDowningTaskItem( themeID );
        	if (item!=null) {
        		item.tmpFilePath = filePath;
        		item.state = newDownState;
       	 		updateDowningTaskItem(item);                           	 		                           	 		
        	}
        } catch (Exception e) {
       	 	e.printStackTrace();
        }
    }
}
