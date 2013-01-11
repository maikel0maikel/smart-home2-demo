package com.nd.hilauncherdev.lib.theme.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.nd.hilauncherdev.datamodel.AbstractDataBase;

public class ThemeLibDB extends AbstractDataBase{

	private static final int VERSION = 1;
	private static final String DB_NAME = "hi_themelib_data.db";
	
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
	
	public ThemeLibDB(Context c) {
		super(c, DB_NAME, VERSION);
	}

	@Override
	public void onDataBaseCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE_DOWNING_TASK);
	}

	@Override
	public void onDataBaseUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
