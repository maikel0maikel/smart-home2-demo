package com.nd.hilauncherdev.myphone.nettraffic.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.nd.hilauncherdev.datamodel.AbstractDataBase;

public class NetTrafficByteDB extends AbstractDataBase{

	private static final int VERSION = 1;
	private static final String DB_NAME = "bytedata.db";

	//创建流量监控表
	private static final String SQL_CREATE_TABLE_NETTRAFFIC_BYTES = ""
			+ "CREATE TABLE IF NOT EXISTS NetTrafficBytes "
			+ "  ( "
			+ "     id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "     dev  INTEGER NOT NULL, "
			+ "     rx   DOUBLE, "
			+ "     tx   DOUBLE, "
			+ "     date TEXT NOT NULL, "
			+ "     data_id INTEGER DEFAULT 0 "
			+ "  )";
	
	public NetTrafficByteDB(Context c) {
		super(c, DB_NAME, VERSION);
	}

	@Override
	public void onDataBaseCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE_NETTRAFFIC_BYTES);
	}

	@Override
	public void onDataBaseUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
